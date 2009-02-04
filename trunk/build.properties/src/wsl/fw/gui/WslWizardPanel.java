//==============================================================================
// WslWizardPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.DataObject;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Cursor;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import java.util.Vector;

//------------------------------------------------------------------------------
/**
 * A button panel whose client area is one of a sequence of WslWizardChild
 * panels which are navigated with next, prev and finish buttons.
 *
 * Usually the WslWizardPanel will have attributes to hold state and data
 * used by the individual children. It is common for the preProcess function to
 * set this shared data into a child and postProcess to extract and store it.
 *
 * Usage:
 * Subclass and set the title.
 * Override getNextChild to create subclasses of WslWizard in the desired order.
 * Override isLast to return true for any terminal panels that should display
 * a finish button.
 * If required override preProcess to perform the initialisation for each child.
 * If required override postProcess to save the results of the current child.
 * If required override finishProcess to perform the final processing when the
 *
 * More details in the documentation for each overridable function.
 *
 * To show the WslWizardPanel subclass it, call addCloseListener if
 * notification is required when the wizard is closed, then use
 * GuiManager.openWslPanel() to create and show the wizard in a framing dialog.
 *
 * @see wsl.fw.gui.WslWizardChild.
 */
public abstract class WslWizardPanel
    extends WslButtonPanel
    implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2004/01/06 01:45:33 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/WslWizardPanel.java $ ";

    // resources
    public static final ResId BTN_PREV    = new ResId("WslWizardPanel.button.prev");
    public static final ResId BTN_NEXT    = new ResId("WslWizardPanel.button.next");
    public static final ResId BTN_FINISH  = new ResId("WslWizardPanel.button.finish");
    public static final ResId BTN_CANCEL  = new ResId("OkCancelPanel.button.Cancel");
    public static final ResId BUTTON_HELP = new ResId("OkPanel.button.Help");

    // attributes
    private WslButton _btnPrev = new WslButton(BTN_PREV.getText(), this);
    private WslButton _btnNext = new WslButton(BTN_NEXT.getText(), this);
    private WslButton _btnFinish = new WslButton(BTN_FINISH.getText(), this);
    private WslButton _btnCancel = new WslButton(BTN_CANCEL.getText(), this);

    private   boolean _isFinished = false;
    private   Vector  _listeners = new Vector();
    protected Vector  _wizardChildStack = new Vector();

    //--------------------------------------------------------------------------
    /**
     * Constructor.
     * @param title, the title for the panel, will usually be displayed as the
     *   title for the framing parent.
     */
    public WslWizardPanel(String title)
    {
        // call base class constructor
        super(HORIZONTAL);

        // set the panel title
        setPanelTitle(title);

        // set up buttons
        addButton(_btnPrev);
        addButton(_btnNext);
        addButton(_btnFinish);
        addButton(_btnCancel);

        // set layout for main panel
        getMainPanel().setLayout(new BorderLayout());

        // add first child to the stack (make it current)
        WslWizardChild wc = getNextChild();
        assert wc != null;
        _wizardChildStack.add(wc);

        // init the first panel ready for display
        initCurrent(true);
    }

    //--------------------------------------------------------------------------
    /**
     * Add a listener that will be notified when the wizard is closed by
     *   pressing finish or cancel.
     * @param l, the listener.
     */
    public void addCloseListener(WizardClosedListener l)
    {
        _listeners.add(l);
    }

    //--------------------------------------------------------------------------
    /**
     * Remove a close listener.
     * @param l, the listener.
     * @see WizardClosedListener.
     */
    public void removeCloseListener(WizardClosedListener l)
    {
        _listeners.remove(l);
    }

    //--------------------------------------------------------------------------
    /**
     * Get the current wizard child.
     * @return the current wizard child or null if there are none.
     */
    protected WslWizardChild getCurrentChild()
    {
        // current wizard child is the last element opn the stack
        if (_wizardChildStack.size() > 0)
            return (WslWizardChild) _wizardChildStack.lastElement();
        else
            return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Init the controls and perform preprocessing for the current child.
     * @param bForwards, true if this init is going forwards (i.e. next), false
     *   if going backwards (i.e. prev), passed to preProcess().
     */
    private void initCurrent(boolean bForwards)
    {
        // get the current wizard child
        WslWizardChild wc = getCurrentChild();
        assert wc != null;

        // set the parent frame and wizard
        wc.setFrameParent(getFrameParent());
        wc.setWizardParent(this);

        // get state for buttons
        boolean bLast   = isLast();
        boolean bPrev   = wc.canPrev() && !isFirst();
        boolean bNext   = wc.canNext() && !bLast;
        boolean bFinish = wc.canFinish() || bLast;
        HelpId  hid     = wc.getHelpId();

        // set up the navigation button states
        _btnPrev.setEnabled(bPrev);
        _btnNext.setEnabled(bNext);
        _btnFinish.setEnabled(bFinish);

        // set up help button
        this.removeAllCustomButtons();
        if (hid != null)
            addHelpButton(BUTTON_HELP.getText(), hid, -1, true);

        // put child in the main panel, if it is a properties panel then create
        // inside its own button panel
        getMainPanel().removeAll();
        if (wc instanceof PropertiesPanel)
            getMainPanel().add(createPropBtnPanel((PropertiesPanel) wc),
                BorderLayout.CENTER);
        else
            getMainPanel().add(wc, BorderLayout.CENTER);

        // set wizard title
        setChildTitle(wc.getWizardText());

        // do preprocessing for child
        preProcess(wc, bForwards);

        // update the child buttons
        wc.updateButtons();

        // repaint
        getMainPanel().repaint();
    }

    //--------------------------------------------------------------------------
    /**
     * Create a button panel for holding the child when it is a PropertiesPanel
     * as PropertiesPanels may have custom buttons that they want added to a
     * parent button panel.
     * @param pp, the PropertiesPanel that needs a host WslButtonPanel
     * @return the WslButtonPanel that is holding the PropertiesPanel.
     */
    private WslButtonPanel createPropBtnPanel(PropertiesPanel pp)
    {
        assert pp != null;

        // create the containing button panel
        WslButtonPanel bp = new WslButtonPanel(WslButtonPanel.VERTICAL);
        bp.setFrameParent(getFrameParent());

        // set border
        pp.setBorder(BorderFactory.createLoweredBevelBorder());

        // set layout
        bp.getMainPanel().setLayout(new BorderLayout());

        // set props panel into button panel
        pp.setMaintenanceParent(bp);

        bp.getMainPanel().add(pp);

        // add custom buttons
        bp.addCustomButtons(pp.getCustomButtons());

        return bp;
    }

    //--------------------------------------------------------------------------
    /**
     * If it exists display the title string above the child.
     * @param title, the title string to display, if null the title panel is not
     *   shown.
     */
    private void setChildTitle(String title)
    {
        if (!Util.isEmpty(title))
        {
            // create label for text dislay
            JLabel label = new JLabel(title, SwingConstants.CENTER);

            // set border
            label.setBorder(BorderFactory.createLoweredBevelBorder());

            // put at top of client area
            getMainPanel().add(label, BorderLayout.NORTH);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Action performed handler, handle the buttons.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            Cursor oldCursor = null;
            try
            {
                oldCursor = getCursor();
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                // switch on source
                if (ev.getSource().equals(_btnCancel))      // cancel btn, just close
                    onCancel();
                else if (ev.getSource().equals(_btnPrev))   // prev btn
                    onPrev();
                else if (ev.getSource().equals(_btnNext))   // next btn
                    onNext();
                else if (ev.getSource().equals(_btnFinish)) // finish btn
                    onFinish();
            }
            finally
            {
                if (oldCursor != null)
                    setCursor(oldCursor);
            }
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, GuiManager.ERR_UNHANDLED.getText(), e);
            Log.error(GuiManager.ERR_UNHANDLED.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Prev button pressed, move back to the previous child panel.
     */
    protected void onPrev()
    {
        // must have 2 elements on stack to do a prev
        assert _wizardChildStack.size() > 1;

        // closing prev panel, notify it
        WslWizardChild wc = getCurrentChild();
        if (postProcess(wc, false))
        {
            wc.onClosePanel();

            // remove last element on stack, making previous element current
            _wizardChildStack.remove(_wizardChildStack.size() - 1);

            // init the new current child
            initCurrent(false);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Next button pressed, create, init and show the new child.
     */
    protected void onNext()
    {
        // perform post processing on the current child
        if (postProcess(getCurrentChild(), true))
        {
            // success, advance to the next child
            // create the net child
            WslWizardChild newWiz = getNextChild();
            assert newWiz != null;

            // add next child to the stack making it current
            _wizardChildStack.add(newWiz);

            // init the new current child
            initCurrent(true);
        }
        else
        {
            // current child failed to post process, it should have displayed
            // any error messages, stay on this child
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Finish button pressed, process the current child, finishProcess and
     * close.
     */
    protected void onFinish()
    {
        // perform post processing on the current child
        if (postProcess(getCurrentChild(), true))
            if (finishProcess())
            {
            // success, set finished flag and close
            _isFinished = true;
            closePanel();
            }
    }

    //--------------------------------------------------------------------------
    /**
     * Cancel button pressed, close the panel
     */
    protected void onCancel()
    {
        closePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Panel closing, notify children and listeners.
     */
    public void onClosePanel()
    {
        // notify all children of close
        for (int i = 0; i < _wizardChildStack.size(); i++)
        {
            if (!_isFinished)
                postProcess(((WslWizardChild) _wizardChildStack.get(i)), false);

            ((WslWizardChild) _wizardChildStack.get(i)).onClosePanel();
        }

        // notify all listeners of close
        for (int j = 0; j < _listeners.size(); j++)
            ((WizardClosedListener) _listeners.get(j)).wizardClosed(_isFinished);


        // call superclass
        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Override of getPreferredSize to ensure the window is wide enough to show
     * the help button.
     */
    public Dimension getPreferredSize()
    {
        final int minWidth = 600;

        Dimension size = super.getPreferredSize();
        if (size.width < minWidth)
            size.width = minWidth;
        return size;
    }

    //--------------------------------------------------------------------------
    /**
     * @return true if the current child is the first child in the stack.
     */
    protected boolean isFirst()
    {
        return _wizardChildStack.size() <= 1;
    }

    //--------------------------------------------------------------------------
    /**
     * Subclasses must override to examine the wizard's current state and
     * determine if the current panel is the last in a sequence.
     * @return true if the current child is a terminal panel, i.e. it is the
     *   last in its sequence.
     */
    protected abstract boolean isLast();

    //--------------------------------------------------------------------------
    /**
     * The WslWizardChild calls preProcess on each WslWizardChild just before it
     * is displayed. Override this function to perform any extra initialisation
     * required for each child.
     *
     * For example if the wizard parent maintained shared state information the
     * preProcess for a given child could set this state data into the child and
     * update the child's controls to reflect that data.
     *
     * The default behaviour is to do the default preProcessing for children
     * that are PropertiesPanels (which should already be bound to their
     * DataObject) and to do nothing for all other children.
     *
     * @param wc, the WslWizardChild subclass to perform preProcessing on.
     * @param bForward, if true this is the initial display of the child (i.e.
     *   from a next). If false the child is being displayed again due to a prev
     *   and therefore may not need full preProcessing.
     *  @see defaultPropPanelPre.
     */
    protected void preProcess(WslWizardChild wc, boolean bForward)
    {
        // default behaviour if a PropertiesPanel
        if (wc instanceof PropertiesPanel)
            defaultPropPanelPre(wc, bForward);
    }

    //--------------------------------------------------------------------------
    /**
     * Helper to perform basic preProcessing for a WslWizardChild that is a
     * PropertiesPanel. The default operation is to init the PropertiesPanel's
     * controls from its DataObject.
     * @param wc, a WslWizardChild subclass (that is also a PropertiesPanel) to
     *   perform preProcessing on.
     * @param bForward, if true this is the initial display of the child (i.e.
     *   from a next). If false the child is being displayed again due to a prev
     *   and therefore may not need full preProcessing.
     */
    protected void defaultPropPanelPre(WslWizardChild wc, boolean bForward)
    {
        assert wc instanceof PropertiesPanel;

        PropertiesPanel pp = (PropertiesPanel) wc;

        // populate the properties panel controls and update any buttons
        pp.transferData(false);
        pp.updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * The WslWizardChild calls postProcess on each WslWizardChild when the
     * child is being left due to a next or finish (bForward is true) so that
     * the state can be saved. It is also called with bForward false when the
     * child is being discarded due to a prev or cancel.
     *
     * The postProcess function may decline the next/prev/finish/cancel by
     * returning false.
     *
     * The default behaviour is to do the default postProcessing for children
     * that are PropertiesPanels (which should already be bound to their
     * DataObject) and to do nothing for all other children.
     *
     * @param wc, the WslWizardChild subclass to perform postProcessing on.
     * @param bForward, if true the child should save its state (i.e. on a next
     *   or finish operation). If false the child should undo any persistent
     *   changes it may have made (i.e. a prev or cancel operation).
     * @return true if the postProcessing succeeded and the wizard may move on
     *   to another child, if false this indicates that some part of the post
     *   processing (such as data validation) failed and the wizard should
     *   remain on the current screen. In this case the postProcess function
     *   should display any error messages.
     */
    protected boolean postProcess(WslWizardChild wc, boolean bForward)
    {
        // default behaviour if a PropertiesPanel
        if (wc instanceof PropertiesPanel)
            return defaultPropPanelPost(wc, bForward);
        else
            return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Helper to perform basic postProcessing for a WslWizardChild that is a
     * PropertiesPanel. The default operation is to update the DataObject with
     * information from the PropertiesPanel's controls if moving in a forward
     * direction. When moving backwards it does nothing.
     * @param wc, a WslWizardChild subclass (that is also a PropertiesPanel) to
     *   perform postProcessing on.
     * @param bForward, if true the child should save its state (i.e. on a next
     *   or finish operation). If false the child should undo any persistent
     *   changes it may have made (i.e. a prev or cancel operation).
     * @return true if the postProcessing succeeded and the wizard may move on
     *   to another child, if false this indicates that some part of the post
     *   processing (such as data validation) failed and the wizard should
     *   remain on the current screen. In this case the postProcess function
     *   should display any error messages.
     */
    protected boolean defaultPropPanelPost(WslWizardChild wc, boolean bForward)
    {
        assert wc instanceof PropertiesPanel;

        PropertiesPanel pp = (PropertiesPanel) wc;

        if (bForward)
        {
            // check mandatories
            boolean rv = pp.checkMandatories();
            if (rv)
            {
                // transfer data
                pp.transferData(true);
            }

            // return true if checkMandatories succeeded
            return rv;
        }
        else
        {
            // going backward, do nothing and succeed
            return true;
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Helper function that can be used in the implementation of getNextChild
     * to bind a PropertiesPanel to its DataObject. It is important to link them
     * at creation time (i.e. in getNextChild) as if this is done
     * PropertiesPanels can use the default processing and do not deed specific
     * implementation in preProcess and postProcess.
     * @param pp, a PropertiesPanel.
     * @param dobj, a DataObject that is to be set into the PropertiesPanel.
     * @see getNextChild.
     */
    protected static PropertiesPanel bindPropertiesPanel(PropertiesPanel pp,
        DataObject dobj)
    {
        Util.argCheckNull(pp);
        Util.argCheckNull(dobj);

        // set the data object into the properties panel
        pp.setDataObject(dobj);

        return pp;
    }

    //--------------------------------------------------------------------------
    /**
     * Called by the WslWizardPanel when the finish button is pressed.
     * Subclasses should examine the state of the wizard and save it.
     * @return, true if the final processing succeeded and the wizard can close,
     *   false if the processing failed and the wizard may not close.
     */
    protected boolean finishProcess()
    {
        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Subclasses must override to examine the wizard's current state and
     * determine what the next child panel should be and create that child.
     * Generally this is done by calling getCurrentChild and switching on its
     * class type to determine the type of the next child to create. After
     * creation there will be a further opportunity to initialise the child in
     * the preProcess method, which is called before the child is displayed.
     *
     * For example if the wizard is to contain 3 children that are to be
     * navigate in order getNextChild could be:
     *
     * if (getCurrentChild() == null)
     *     return new Panel1();
     * else if (getCurrentChild() instanceof Panel1)
     *     return new Panel2();
     * else if (getCurrentChild() instanceof Panel2)
     *     return new Panel3();
     * else
     *     return null;
     *
     * Note that a similar mechanism could be used in preProcess, postProcess or
     * isLast.
     *
     * @return a WslWizardChild subclass that is to become the next
     *   displayed child panel.
     */
    protected abstract WslWizardChild getNextChild();
}

//==============================================================================
// end of file WslWizardPanel.java
//==============================================================================

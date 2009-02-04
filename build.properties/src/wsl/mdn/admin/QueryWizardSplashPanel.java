package wsl.mdn.admin;

// imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextArea;
import wsl.mdn.dataview.QueryDobj;

//------------------------------------------------------------------------------
/**
 *
 */
public class QueryWizardSplashPanel extends WslWizardChild
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId RADIO_NEW_QUERY  =
        new ResId("QueryWizardSplashPanel.radio.New");
    public static final ResId LABEL_NEW_QUERY  =
        new ResId("QueryWizardSplashPanel.label.New");
    public static final ResId RADIO_EXISTING_QUERY  =
        new ResId("QueryWizardSplashPanel.radio.Existing");
    public static final ResId LABEL_EXISTING_QUERY  =
        new ResId("QueryWizardSplashPanel.label.Existing");

    public final static HelpId HID_QUERY_SPLASH = new HelpId("mdn.admin.QueryWizardSplashPanel");

    //--------------------------------------------------------------------------
    // attributes

    private QueryDobj _query = new QueryDobj();
    private QueryDobj _existingQuery;


    //--------------------------------------------------------------------------
    // controls

    private JRadioButton _rdoNew = new JRadioButton(RADIO_NEW_QUERY.getText());
    private JRadioButton _rdoExisting = new JRadioButton(RADIO_EXISTING_QUERY.getText());


    //--------------------------------------------------------------------------
    // construction

    /**
     * ctor
     */
    public QueryWizardSplashPanel()
    {
        // init controls
        initWizardControls();

        // update buttons
        updateButtons();
    }

    /**
     * Init the panel controls
     */
    private void initWizardControls()
    {
        // layout
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        this.setBorder(BorderFactory.createLoweredBevelBorder());

        // button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(_rdoNew);
        bg.add(_rdoExisting);
        _rdoNew.setSelected(true);

        // new
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET * 4;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        _rdoNew.addActionListener(this);
        add(_rdoNew, gbc);
        WslTextArea lbl = new WslTextArea(65, 4);
        lbl.setText(LABEL_NEW_QUERY.getText());
        lbl.setBackground(this.getBackground());
        gbc.gridy = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);

        // existing
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets.bottom = 0;
        _rdoExisting.addActionListener(this);
        add(_rdoExisting, gbc);
        lbl = new WslTextArea(65, 4);
        lbl.setText(LABEL_EXISTING_QUERY.getText());
        lbl.setBackground(this.getBackground());
        gbc.gridy = 3;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        add(lbl, gbc);
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * Set the existing Query
     * @param existingQuery the existing Query
     */
    public void setExistingQuery(QueryDobj existingQuery)
    {
        // validate
        Util.argCheckNull(existingQuery);

        // set attribs
        _existingQuery = existingQuery;
        _query = _existingQuery;

        // set existing radio
        _rdoExisting.setSelected(true);

        // set the radio text
        _rdoExisting.setText(RADIO_EXISTING_QUERY.getText() + ": " +
            _existingQuery.getName());

        // update buttons
        updateButtons();
    }

    /**
     * @return QueryDobj the selected Query
     */
    public QueryDobj getSelectedQuery()
    {
        return _query;
    }


    //--------------------------------------------------------------------------
    // actions

    /**
     * Button clicked
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource() == _rdoNew)
                _query = new QueryDobj();
            else if(ev.getSource() == _rdoExisting)
                _query = _existingQuery;

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Update controls
     */
    public void updateButtons()
    {
        // flags
        boolean hasExisting = _existingQuery != null;

        // enable
        _rdoExisting.setEnabled(hasExisting);
    }

    /**
     * @return boolean true if the query being edited by the wizard is new
     */
    public boolean isNewQuery()
    {
        return _rdoNew.isSelected();
    }
    //--------------------------------------------------------------------------
    /**
     * If the subclass has help override this to specify the HelpId.
     * This help is displayed using the parent wizards's help button.
     * @return the HelpId of the help to display, if null the help button is not
     *   displayed.
     */
    public HelpId getHelpId()
    {
        return HID_QUERY_SPLASH;
    }
}
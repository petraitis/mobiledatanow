//==============================================================================
// MaintenancePanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

// imports
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JButton;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.KeyConstraintException;
import wsl.fw.datasource.DataSourceException;

import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Panel that allows the user to maintain DataObjects. Ie Select Save Delete them.
 * A PropertiesPanel is set into the MaintenancePanel
 */
public class MaintenancePanel extends WslButtonPanel implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/MaintenancePanel.java $ ";

    // resources
    public static final ResId BUTTON_SEARCH  = new ResId("MaintenancePanel.button.Search");
    public static final ResId BUTTON_SAVE  = new ResId("MaintenancePanel.button.Save");
    public static final ResId BUTTON_DELETE  = new ResId("MaintenancePanel.button.Delete");
    public static final ResId BUTTON_CLEAR  = new ResId("MaintenancePanel.button.Clear");
    public static final ResId BUTTON_CLOSE  = new ResId("MaintenancePanel.button.Close");
    public static final ResId BUTTON_HELP  = new ResId("MaintenancePanel.button.Help");
    public static final ResId ERR  = new ResId("MaintenancePanel.error.Error");
    public static final ResId ERR_UNEXPECTED  = new ResId("MaintenancePanel.error.Unexpected");
    public static final ResId BUTTON_UPDATE  = new ResId("MaintenancePanel.button.Update");
    public static final ResId BUTTON_INSERT  = new ResId("MaintenancePanel.button.Insert");

    // attributes
    protected PropertiesPanel _pnlProps = null;
    protected WslButton _btnSelect = new WslButton(BUTTON_SEARCH.getText(), this);
    protected WslButton _btnSave = new WslButton(BUTTON_SAVE.getText(), this);
    protected WslButton _btnDelete = new WslButton(BUTTON_DELETE.getText(), this);
    protected WslButton _btnClear = new WslButton(BUTTON_CLEAR.getText(), this);
    protected WslButton _btnClose = new WslButton(BUTTON_CLOSE.getText(), this);

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    protected MaintenancePanel()
    {
        // super
        super(WslButtonPanel.VERTICAL);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor taking a PropertiesPanel.
     * @param pnlProps The PropertiesPanel to set into the MaintenancePanel.
     */
    public MaintenancePanel(PropertiesPanel pnlProps)
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // init controls
        initMaintenancePanelControls(pnlProps);
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Create and init controls.
     * @param pnlProps The PropertiesPanel to set into the MaintenancePanel.
     */
    protected void initMaintenancePanelControls(PropertiesPanel pnlProps)
    {
        // layout
        getMainPanel().setLayout(new GridBagLayout());

        // set the properties panel
        if(pnlProps == null)
            pnlProps = new NullPropertiesPanel();
        setPropertiesPanel(pnlProps);

        // add buttons to buttons panel
        _btnSelect.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "search.gif"));
        _btnSave.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "save.gif"));
        _btnClear.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "new.gif"));
        _btnDelete.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "delete.gif"));
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnSelect);
        addButton(_btnSave);
        addButton(_btnClear);
        addButton(_btnDelete);

        // add help button
        if (pnlProps != null)
            if (pnlProps.getHelpId() != null)
                addHelpButton(BUTTON_HELP.getText(), pnlProps.getHelpId());

        addButton(_btnClose);

        // add custom buttons
        if(pnlProps != null)
            addCustomButtons(_pnlProps.getCustomButtons());
    }

    //--------------------------------------------------------------------------
    /**
     * @return WslButton the default button.
     */
    public WslButton getDefaultButton()
    {
        return _btnSave;
    }

    //--------------------------------------------------------------------------
    /**
     * Action performed.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnClose))
                closePanel();
            else if(ev.getSource().equals(_btnSelect))
                onSelect();
            else if(ev.getSource().equals(_btnSave))
                onSave();
            else if(ev.getSource().equals(_btnClear))
                onClear();
            else if(ev.getSource().equals(_btnDelete))
                onDelete();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            GuiManager.showErrorDialog(this, ERR.getText(), e);
            Log.error(ERR.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Clear the DataObject and the controls.
     */
    protected void onClear() throws Exception
    {
        // clear the DataObject
        _pnlProps.getDataObject().clear();

        // transfer data to controls
        _pnlProps.transferData(false);
    }

    //--------------------------------------------------------------------------
    /**
     * Save the DataObject
     * @return boolean true if the save was successful
     * @exception
     * @roseuid 398FBA4801C4
     */
    protected boolean onSave() throws Exception
    {
        // check mandatories
        boolean ret = _pnlProps.checkMandatories();
        if(ret)
        {
            // transfer data
            _pnlProps.transferData(true);

            // save the DataObject
            try
            {
                _pnlProps.getDataObject().save();
            }
            catch (KeyConstraintException e)
            {

                GuiManager.showErrorDialog(this, e.getMessage(), null);
                Log.debug("MaintenancePanel.onSave: ", e);
                return false;
            }
            catch (Exception e)
            {
                GuiManager.showErrorDialog(this, ERR_UNEXPECTED.getText(), e);
                Log.error(ERR_UNEXPECTED.getText(), e);
                return false;
            }

            // transfer data back to screen
            _pnlProps.transferData(false);
        }

        return ret;
    }

    //--------------------------------------------------------------------------
    /**
     * Delete the property panel's DataObject.
     * @throws DataSourceException if there is an error deleting the DataObject.
     * @throws IllegalAccessException if there is an error creating the new
     *   DataObject.
     * @throws InstantiationException if there is an error creating the new
     *   DataObject.
     * @roseuid 3990C25E014E
     */
    protected void onDelete() throws Exception
    {
        // delete the DataObject
        _pnlProps.getDataObject().delete();

        // cannot re-use a deleted object so create and set a new one
        DataObject dobj = (DataObject) _pnlProps.getDataObject().getClass().newInstance();
        _pnlProps.setDataObject(dobj);

        // transfer data to controls
        _pnlProps.transferData(false);
    }

    //--------------------------------------------------------------------------
    /**
     * Select a DataObject with the SelectPanel and update into the
     * PropertiesPanel.
     * @roseuid 3990C2990225.
     */
    protected void onSelect()
    {
        // select a DataObject
        DataObject dobj = getPropertiesPanel().getDataObject();
        if(dobj != null)
        {
            dobj = GuiManager.selectDataObject(getFrameParent(), dobj.getClass(), true);
            if(dobj != null)
            {
                // set the new DataObject
                getPropertiesPanel().setDataObject(dobj);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return PropertiesPanel.
     * @roseuid 398FBA9D01B2.
     */
    public PropertiesPanel getPropertiesPanel()
    {
        return _pnlProps;
    }

    //--------------------------------------------------------------------------
    /**
     * Set a properties panel into the maintenance panel.
     */
    public void setPropertiesPanel(PropertiesPanel pnlProps)
    {
        // validate
        Util.argCheckNull(pnlProps);

        // remove the old panel
        if(_pnlProps != null)
            getMainPanel().remove(_pnlProps);

        // set the attribute
        _pnlProps = pnlProps;

        // set the maintenance parent
        _pnlProps.setMaintenanceParent(this);

        // add to the maintenance panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        _pnlProps.setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().add(_pnlProps, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Update controls based on state.
     */
    public void updateButtons()
    {
        // set flags
        DataObject dobj = null;
        if(getPropertiesPanel() != null)
            dobj = getPropertiesPanel().getDataObject();
        boolean indb = dobj != null && dobj.getState() == DataObject.IN_DB;

        // enable disable controls
        _btnSelect.setEnabled(true);
        if(indb)
            _btnSave.setText(BUTTON_UPDATE.getText());
        else
            _btnSave.setText(BUTTON_INSERT.getText());
        _btnDelete.setEnabled(indb);
        _btnClear.setEnabled(true);
        _btnClose.setEnabled(true);

        // notify PropertiesPanel
        if(getPropertiesPanel() != null)
            getPropertiesPanel().updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred size for the MaintenancePanel.
     */
    public Dimension getPreferredSize()
    {
        // set defaults
        int x = 400;
        int y = 300;

        // if child is bigger, go bigger
        JPanel p = getPropertiesPanel();
        if(p != null)
        {
            Dimension d = p.getPreferredSize();
            x = (x > (int)d.getWidth())? x: (int)d.getWidth();
            y = (y > (int)d.getHeight())? y: (int)d.getHeight();
        }

        // add the button panel width
        p = getButtonPanel();
        if(p != null)
        {
            Dimension d = p.getPreferredSize();
            x += (int)d.getWidth();
        }

        // return
        return new Dimension(x, y);
    }

    //--------------------------------------------------------------------------
    /**
     * Panel closing, notify properties panel as well.
     */
    public void onClosePanel()
    {
        // notify the contained properties panel
        if (_pnlProps != null)
            _pnlProps.onClosePanel();

        super.onClosePanel();
    }
}

//------------------------------------------------------------------------------
/**
 * Empty PropertiesPanel to to act as a proxy in a MaintenancePanel that has no
 * PropertiesPanel set.
 */
class NullPropertiesPanel extends PropertiesPanel
{
    //--------------------------------------------------------------------------
    /**
     * Transfer data between controls and DataObject
     */
    public void transferData(boolean toDataObject)
    {}

    //--------------------------------------------------------------------------
    /**
     * Return the preferred dimensions of the panel
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(300, 100);
    }
}

//==============================================================================
// end of file MaintenancePanel.java
//==============================================================================

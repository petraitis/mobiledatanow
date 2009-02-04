package wsl.mdn.admin;

// imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import pv.jfcx.JPVPassword;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.resource.ResId;
import wsl.mdn.dataview.EntityDobj;

//------------------------------------------------------------------------------
/**
 *
 */
public class EntityPropPanel extends PropertiesPanel
{
    // resources
    public static final ResId LABEL_NAME  = new ResId("EntityPropPanel.label.Name");
    public static final ResId LABEL_DESCRIPTION  = new ResId("EntityPropPanel.label.Description");

    public final static HelpId HID_ENTITY = new HelpId("mdn.admin.EntityPropPanel");

    // controls
    private WslTextField _txtName        = new WslTextField(200);
    private WslTextField _txtDescription = new WslTextField(250);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public EntityPropPanel()
    {
        // init controls
        initEntityPropPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initEntityPropPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Name control
        JLabel lbl = new JLabel(LABEL_NAME.getText());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtName, gbc);
        addMandatory(LABEL_NAME.getText(), _txtName);

        // description control
        lbl = new JLabel(LABEL_DESCRIPTION.getText());
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(_txtDescription, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        EntityDobj dobj = (EntityDobj) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the Category DataObject
            dobj.setName(_txtName.getText());
            dobj.setDescription(_txtDescription.getText());
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtDescription.setText(dobj.getDescription());
        }
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(350, 150);
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
        return HID_ENTITY;
    }
}
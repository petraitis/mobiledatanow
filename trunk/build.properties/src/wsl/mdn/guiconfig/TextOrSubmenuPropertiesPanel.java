//==============================================================================
// TextOrSubmenuPropertiesPanel.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.mdn.guiconfig;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import wsl.fw.datasource.DataObject;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslLabel;
import wsl.fw.security.Group;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;

//------------------------------------------------------------------------------
/**
 * Properties panel to edit TextAction and Submenu.
 * If a TextAction then phone link fields are also displayed.
 */
public class TextOrSubmenuPropertiesPanel
    extends PropertiesPanel
    implements ActionListener
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:35:35 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Mobile Data Now/Source/wsl/mdn/guiconfig/TextOrSubmenuPropertiesPanel.java $ ";

    // constants
    public final static int LABEL_WIDTH = 90;

    // resources
    public static final ResId LABEL_NAME         = new ResId("TextOrSubmenuPropertiesPanel.label.Name");
    public static final ResId MANDATORY_NAME     = new ResId("TextOrSubmenuPropertiesPanel.mandatory.Name");
    public static final ResId LABEL_DESCRIPTION  = new ResId("TextOrSubmenuPropertiesPanel.label.Description");
    public static final ResId LABEL_NOLINK       = new ResId("TextOrSubmenuPropertiesPanel.label.NoLink");
    public static final ResId LABEL_ISPHONELINK  = new ResId("TextOrSubmenuPropertiesPanel.label.IsPhoneLink");
    public static final ResId LABEL_ISURLLINK    = new ResId("TextOrSubmenuPropertiesPanel.label.IsUrlLink");
    public static final ResId LABEL_PHONELINK    = new ResId("TextOrSubmenuPropertiesPanel.label.PhoneLink");
    public static final ResId LABEL_URLLINK      = new ResId("TextOrSubmenuPropertiesPanel.label.UrlLink");

    // help id
    public final static HelpId HID_TEXTORSUBMENU = new HelpId("mdn.guiconfig.TextOrSubmenuPropertiesPanel");

    // attributes
    private boolean _isText = false;

    // controls
    private WslTextField _txtName        = new WslTextField(150);
    private WslTextField _txtDescription = new WslTextField(200);
    private JRadioButton _radNoLink      = new JRadioButton(LABEL_NOLINK.getText());
    private JRadioButton _radPhoneLink   = new JRadioButton(LABEL_ISPHONELINK.getText());
    private JRadioButton _radUrlLink     = new JRadioButton(LABEL_ISURLLINK.getText());
    private JPanel       _radioPanel     = new JPanel();
    private JLabel       _lblLink        = new WslLabel(LABEL_NOLINK.getText(),LABEL_WIDTH);
    private WslTextField _txtLink        = new WslTextField(200);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public TextOrSubmenuPropertiesPanel()
    {
        // init controls
        initTextOrSubmenuPropertiesPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initTextOrSubmenuPropertiesPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Name
        WslLabel lbl = new WslLabel(LABEL_NAME.getText(), LABEL_WIDTH);
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
        addMandatory(MANDATORY_NAME.getText(), _txtName);

        // Description
        lbl = new WslLabel(LABEL_DESCRIPTION.getText(), LABEL_WIDTH);
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtDescription, gbc);

        // link type radio buttons
        // add to radio group
        ButtonGroup grp = new ButtonGroup();
        grp.add(_radNoLink);
        grp.add(_radPhoneLink);
        grp.add(_radUrlLink);

        // set up action listeners
        _radNoLink.addActionListener(this);
        _radPhoneLink.addActionListener(this);
        _radUrlLink.addActionListener(this);

        // add to panel
        _radioPanel.add(_radNoLink);
        _radioPanel.add(_radPhoneLink);
        _radioPanel.add(_radUrlLink);

        gbc.insets.right = 0;
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(_radioPanel, gbc);

        // link
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(_lblLink, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtLink, gbc);

        // placeholder to fill area even when Link is hidden
        gbc.insets.right = 0;
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel(), gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.weighty = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(new JLabel(), gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the Category DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        MenuAction dobj = (MenuAction) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
            dobj.setName(_txtName.getText());
            dobj.setDescription(_txtDescription.getText());

            if (_isText)
            {
                TextAction taDobj = (TextAction) dobj;
                if (_radPhoneLink.getModel().isSelected())
                    taDobj.setPhoneLink(_txtLink.getText());
                else if (_radUrlLink.getModel().isSelected())
                    taDobj.setUrlLink(_txtLink.getText());
                else
                    taDobj.setPhoneLink(null);
            }
        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtDescription.setText(dobj.getDescription());

            if (_isText)
            {
                TextAction taDobj = (TextAction) dobj;

                if (taDobj.hasPhoneLink())
                {
                    _radPhoneLink.getModel().setSelected(true);
                    _txtLink.setText(taDobj.getPhoneLink());
                }
                else if (taDobj.hasUrlLink())
                {
                    _radUrlLink.getModel().setSelected(true);
                    _txtLink.setText(taDobj.getUrlLink());
                }
                else
                {
                    _radNoLink.getModel().setSelected(true);
                    _txtLink.setText("");
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * @return the preferred size.
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(380, 160);
    }

    //--------------------------------------------------------------------------
    /**
     * @return the TextOrSubmenuPropertiesPanel help id.
     */
    public HelpId getHelpId()
    {
        return HID_TEXTORSUBMENU;
    }

    //--------------------------------------------------------------------------
    /**
     * Override of setDataObject to set an internal flag and enable or disable
     * controls depending on the type of DataObject.
     */
    public void setDataObject(DataObject dobj)
    {
        // set the _isText flag
        _isText = (dobj instanceof TextAction);

        // hide link controls if not a TextAction
        _radioPanel.setVisible(_isText);
        _lblLink.setVisible(_isText);
        _txtLink.setVisible(_isText);

        // call superclass
        super.setDataObject(dobj);
    }

    //--------------------------------------------------------------------------
    /**
     * Update the state of the controls.
     */
    public void updateButtons()
    {
        // call superclass
        super.updateButtons();

        // only if thsi is a TextAction
        if (_isText)
        {
            // disable link control if noLink is selected
            _lblLink.setEnabled(!_radNoLink.getModel().isSelected());
            _txtLink.setEnabled(!_radNoLink.getModel().isSelected());

            // set label depending on radio
            if (_radPhoneLink.getModel().isSelected())
                _lblLink.setText(LABEL_PHONELINK.getText());
            else if (_radUrlLink.getModel().isSelected())
                _lblLink.setText(LABEL_URLLINK.getText());
            else
                _lblLink.setText(LABEL_NOLINK.getText());
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Action handler.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source, update state when the checkboxes change
            if (ev.getSource().equals(_radNoLink)
                || ev.getSource().equals(_radPhoneLink)
                || ev.getSource().equals(_radUrlLink))
                updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }
}

//==============================================================================
// end of file TextOrSubmenuPropertiesPanel.java
//==============================================================================

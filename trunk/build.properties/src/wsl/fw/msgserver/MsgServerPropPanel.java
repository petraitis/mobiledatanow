/**	$Id: MsgServerPropPanel.java,v 1.2 2002/06/18 23:40:11 jonc Exp $
 *
 *	Property panel for MSExchange and Outlook
 *
 */
package wsl.fw.msgserver;

import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.help.HelpId;
import wsl.fw.datasource.*;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.GuiManager;
import wsl.fw.resource.ResId;
import wsl.fw.msgserver.MessageServer;

public class MsgServerPropPanel
	extends PropertiesPanel
    implements ActionListener
{
    //--------------------------------------------------------------------------
    // resources
    public static final ResId
		LABEL_SERVER_NAME	= new ResId ("MsgServerPropPanel.label.ServerName"),
    	LABEL_DESCRIPTION	= new ResId ("MsgServerPropPanel.label.Description"),
    	LABEL_EX2000		= new ResId ("MsgServerPropPanel.label.Ex2000"),
    	LABEL_EX55			= new ResId ("MsgServerPropPanel.label.Ex55"),
    	LABEL_ISLOCALHOST	= new ResId ("MsgServerPropPanel.label.IsLocalhost"),
    	TEXT_LOCALHOST_NAME	= new ResId ("MsgServerPropPanel.text.LocalhostName");

    public final static HelpId HID_MSG_PROP_PANEL = new HelpId("wsl.fw.MsgServerPropPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslTextField _txtServerName  = new WslTextField(200);
    private WslTextField _txtDescription = new WslTextField(250);
    private JRadioButton _rdoEx2000 = new JRadioButton(LABEL_EX2000.getText());
    private JRadioButton _rdoEx55 = new JRadioButton(LABEL_EX55.getText());
    private JRadioButton _rdoLocalhost = new JRadioButton(LABEL_ISLOCALHOST.getText());
    private WslButton _btnTest = new WslButton("Test");


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public MsgServerPropPanel()
    {
        // init controls
        initMsgServerPropPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void initMsgServerPropPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // button group
        ButtonGroup bg = new ButtonGroup();
        bg.add(_rdoEx2000);
        bg.add(_rdoEx55);
        bg.add(_rdoLocalhost);
        _rdoEx2000.addActionListener(this);
        _rdoEx55.addActionListener(this);
        _rdoLocalhost.addActionListener(this);

        // ex 2000 radio
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        //gbc.gridwidth = 2;
        add(_rdoEx2000, gbc);

        // ex 55 radio
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridx = 1;
        add(_rdoEx55, gbc);

        // server name control
        JLabel lbl = new JLabel(LABEL_SERVER_NAME.getText());
        gbc.insets.right = 0;
        gbc.weightx = 0.2;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(lbl, gbc);
        gbc.gridx = 1;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        add(_txtServerName, gbc);
        addMandatory(LABEL_SERVER_NAME.getText(), _txtServerName);

        // localhost radio
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(_rdoLocalhost, gbc);

        // description control
        lbl = new JLabel(LABEL_DESCRIPTION.getText());
        gbc.insets.right = 0;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
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
    public void
	transferData (
	 boolean toDataObject)
    {
        // must have a DataObject
        MessageServer dobj = (MessageServer) getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
            dobj.setServerName(_txtServerName.getText());
            dobj.setDescription(_txtDescription.getText());
            dobj.setIsLocalhost(_rdoLocalhost.isSelected());
            String type = MsExchangeMsgServer.MST_OUTLOOK2000;
            if(_rdoEx2000.isSelected())
                type = MsExchangeMsgServer.MST_MSEX2000;
            else if(_rdoEx55.isSelected())
                type = MsExchangeMsgServer.MST_MSEX55;
            dobj.setType(type);

        } else
        {
            // to the controls
            _txtServerName.setText(dobj.getServerName());
            _txtDescription.setText(dobj.getDescription());
            String type = dobj.getType();
            if(type == null || type.length() == 0)
                type = dobj.isLocalhost () ?
						MsExchangeMsgServer.MST_OUTLOOK2000 :
						MsExchangeMsgServer.MST_MSEX2000;

            _rdoLocalhost.setSelected (
				type.equals (MsExchangeMsgServer.MST_OUTLOOK2000));
            _rdoEx2000.setSelected (
				type.equals (MsExchangeMsgServer.MST_MSEX2000));
            _rdoEx55.setSelected (
				type.equals (MsExchangeMsgServer.MST_MSEX55));
        }
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(420, 200);
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
        return HID_MSG_PROP_PANEL;
    }


    //--------------------------------------------------------------------------
    /**
     * Return a vector of custom buttons. These buttons will be added to the button panel.
     * Overriden by subclasses
     * @return Vector A Vector of WslButtons
     */
    public Vector getCustomButtons()
    {
        Vector ret = new Vector();
        _btnTest.addActionListener(this);
        ret.add(_btnTest);
        return ret;
    }

    public void actionPerformed(ActionEvent ev)
    {
        // switch on type
        try
        {
            if(ev.getSource() == _btnTest)
                onTest();
            else if(ev.getSource() == _rdoEx2000 || ev.getSource() == _rdoEx55)
            {
                if(_txtServerName.getText().equals(TEXT_LOCALHOST_NAME.getText()))
                    _txtServerName.setText("");
            }
            else if(ev.getSource() == _rdoLocalhost)
                _txtServerName.setText(TEXT_LOCALHOST_NAME.getText());

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            Log.error("", e);
        }
    }

    /**
     * Update buttons
     */
    public void updateButtons()
    {
        // get the message server
        boolean isServer = !_rdoLocalhost.isSelected();

        // enable
        _txtServerName.setEnabled(isServer);
    }

    //--------------------------------------------------------------------------
    // test

    /**
     * Test the message server
     */
    private void onTest() throws Exception
    {
    }
}
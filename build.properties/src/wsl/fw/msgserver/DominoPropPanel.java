/*	$Header: /wapsolutions/cvsroot/mdn/fw/src/wsl/fw/msgserver/DominoPropPanel.java,v 1.1.1.1 2002/06/11 23:11:42 jonc Exp $
 *
 *	Property Panel for wsl.mdn.mdnmsgserver.MdnDominoMsgServer
 */
package wsl.fw.msgserver;

// imports
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import pv.jfcx.JPVPassword;
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

public class DominoPropPanel
	extends PropertiesPanel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId LABEL_SERVER_NAME  =
           new ResId ("DominoPropPanel.label.ServerName");
    public static final ResId LABEL_HOST  =
           new ResId ("DominoPropPanel.label.Host");

    public final static HelpId HID_MSG_PROP_PANEL =
			new HelpId ("wsl.fw.MsgServerPropPanel");


    //--------------------------------------------------------------------------
    // controls
    private WslTextField _txtServerName  = new WslTextField (200);
    private WslTextField _txtHost = new WslTextField (200);


    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public
	DominoPropPanel ()
    {
        // init controls
        initDominoPropPanelontrols ();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void
	initDominoPropPanelontrols ()
    {
        // set layout
        setLayout (new GridBagLayout ());
        GridBagConstraints gbc = new GridBagConstraints ();

        // server name control
        JLabel lbl = new JLabel (LABEL_SERVER_NAME.getText ());

		gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.insets = new Insets (3, 0, 3, 0);
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtServerName, gbc);
        addMandatory (LABEL_SERVER_NAME.getText(), _txtServerName);

        // host control
        lbl = new JLabel (LABEL_HOST.getText ());
        gbc.gridwidth = 1;
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtHost, gbc);
        addMandatory (LABEL_HOST.getText (), _txtServerName);
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
        MessageServer dobj = (MessageServer) getDataObject ();
        Util.argCheckNull (dobj);

        if (toDataObject)
        {
            // to the DataObject
            dobj.setServerName (_txtServerName.getText ());
            dobj.setHost (_txtHost.getText ());

        } else
        {
            // to the controls
            _txtServerName.setText (dobj.getServerName ());
            _txtHost.setText (dobj.getHost ());
        }
    }

    /**
     * Return the preferred size
     */
    public Dimension
	getPreferredSize ()
    {
        return new Dimension (420, 200);
    }

    //--------------------------------------------------------------------------
    /**
     * If the subclass has help override this to specify the HelpId.
     * This help is displayed using the parent wizards's help button.
     * @return the HelpId of the help to display, if null the help button is not
     *   displayed.
     */
    public HelpId
	getHelpId()
    {
        return HID_MSG_PROP_PANEL;
    }
}
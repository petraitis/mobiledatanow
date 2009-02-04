/**	$Id: MsgServerActionPropPanel.java,v 1.4 2002/07/18 00:23:01 jonc Exp $
 *
 *	Message server profile settings, under Presentation Builder
 *
 */
package wsl.mdn.guiconfig;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.RecordSet;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslLabel;
import wsl.fw.gui.WslTextField;
import wsl.fw.help.HelpId;
import wsl.fw.msgserver.*;
import wsl.fw.resource.ResId;
import wsl.fw.security.Group;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.mdn.common.MdnAdminConst;

public class MsgServerActionPropPanel
	extends PropertiesPanel
    implements ActionListener
{
	// resources
	public static final ResId
		LABEL_NAME		= new ResId ("MsgServerActionPropPanel.label.Name"),
		LABEL_DESCRIPTION
			= new ResId ("MsgServerActionPropPanel.label.Description"),
		LABEL_MSGSERVER
			= new ResId ("MsgServerActionPropPanel.label.MsgServer"),
		ERR_LOAD_MSGSERVERS
			= new ResId ("MsgServerActionPropPanel.error.LoadMsgServers");

	// help id
	public final static HelpId
		HID_MSGSERVER_ACTION
			= new HelpId ("mdn.guiconfig.MsgServerActionPropPanel");

	// attributes
	private boolean _isBuilding = false;

	// controls
	private WslTextField _txtName        = new WslTextField (150);
	private WslTextField _txtDescription = new WslTextField (230);
	private WslComboBox  _cmbMsgServers    = new WslComboBox (230);

	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public
	MsgServerActionPropPanel ()
	{
		// init controls
		initMsgServerActionPropPanelControls ();

		// build the message servers combo
		buildMsgServersCombo ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Init the panel's controls.
	 */
	private void
	initMsgServerActionPropPanelControls ()
	{
        // set layout
        setLayout (new GridBagLayout ());
        GridBagConstraints gbc = new GridBagConstraints ();

		//
        JLabel lbl = new JLabel (LABEL_MSGSERVER.getText ());
		gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.insets = new Insets (3, 0, 3, 0);
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_cmbMsgServers, gbc);
		_cmbMsgServers.addActionListener (this);
        addMandatory (LABEL_MSGSERVER.getText(), _cmbMsgServers);

		//
		lbl = new JLabel (LABEL_NAME.getText ());
        gbc.gridwidth = 1;
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtName, gbc);
		addMandatory (LABEL_NAME.getText (), _txtName);

		//
        lbl = new JLabel (LABEL_DESCRIPTION.getText ());
        gbc.gridwidth = 1;
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtDescription, gbc);
	}

	/**
	 * Build message servers combo
	 */
	private void
	buildMsgServersCombo ()
	{
		try
		{
			// set building flag
			_isBuilding = true;

			// build query
			DataSource sysDs = DataManager.getSystemDS ();
			Query q = new Query (MessageServer.ENT_MSGSERVER);

			//select all data views
			RecordSet rs = sysDs.select (q);

			// build combo
			_cmbMsgServers.buildFromRecordSet (rs);

			// clear building flag
			_isBuilding = false;
		}
		catch (Exception e)
		{
			GuiManager.showErrorDialog (
				this,
				ERR_LOAD_MSGSERVERS.getText (),
				e);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Action handler.
	 */
	public void
	actionPerformed (
	 ActionEvent ev)
	{
		try
		{
			if (ev.getSource ().equals (_cmbMsgServers) && !_isBuilding)
				setDefaultName ();
		}
		catch (Exception e)
		{
			GuiManager.showErrorDialog (
				this,
				MdnAdminConst.ERR_UNHANDLED.getText (),
				e);
			Log.error (MdnAdminConst.ERR_UNHANDLED.getText (), e);
		}
	}

	/**
	 * Set the default name from the message server selection
	 */
	private void
	setDefaultName ()
	{
		MessageServer ms = (MessageServer)_cmbMsgServers.getSelectedItem ();
		if (ms != null)
			_txtName.setText (ms.getServerName ());
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
		MsgServerAction dobj = (MsgServerAction) getDataObject ();
		Util.argCheckNull (dobj);

		if (toDataObject)
		{
			// to the DataObject
			dobj.setName (_txtName.getText ());
			dobj.setDescription (_txtDescription.getText ());

			// get the message server id
			if (_cmbMsgServers.getSelectedItem () != null)
			{
				int msId = ( (MessageServer)_cmbMsgServers.getSelectedItem ()).getId ();
				dobj.setMsgServerId (msId);
			}

			dobj.setMsgServerFlags (MsgServerAction.MSF_NONE);
		}
		else
		{
			// select the message server in the combo
			if (dobj.getState () == DataObject.IN_DB)
			{
				_isBuilding = true;
				selectComboOnId (_cmbMsgServers, new Integer (dobj.getMsgServerId ()));
				_isBuilding = false;

				// set name and desc
				_txtName.setText (dobj.getName ());
				_txtDescription.setText (dobj.getDescription ());

			}
			else
				setDefaultName ();
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Used to select a DataView ina combo by its FLD_ID.
	 * @param combo, the combo.
	 * @param id, the id of the item to select.
	 * @return the index of the item selected.
	 */
	private int
	selectComboOnId (
	 WslComboBox combo,
	 Object id)
	{
		Util.argCheckNull (combo);

		if (id == null)
			combo.setSelectedIndex (-1);
		else
		{
			// iterate the combo
			for (int i = 0; i < combo.getItemCount (); i++)
			{
				// compare the item id
				if ( ((DataObject) combo.getItemAt (i)).getObjectValue (MessageServer.FLD_ID).equals (id))
				{
					combo.setSelectedIndex (i);
					return i;
				}
			}
		}

		// not found
		return -1;
	}

	//--------------------------------------------------------------------------
	/**
	 * Return the preferred size
	 */
	public Dimension
	getPreferredSize ()
	{
		return new Dimension (370, 180);
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the ActionPropertiesPanel help id.
	 */
	public HelpId
	getHelpId ()
	{
		return HID_MSGSERVER_ACTION;
	}
}

/**	$Id: MsgServerProfilePropPanel.java,v 1.2 2002/07/17 23:46:01 jonc Exp $
 *
 *
 */
package wsl.fw.msgserver;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import wsl.fw.datasource.DataManager;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.Query;
import wsl.fw.datasource.RecordSet;
import wsl.fw.gui.*;
import wsl.fw.help.HelpId;
import wsl.fw.msgserver.MessageServerProfile;
import wsl.fw.resource.ResId;
import wsl.fw.security.SecurityManager;
import wsl.fw.security.User;
import wsl.fw.util.Log;
import wsl.fw.util.Util;

public class MsgServerProfilePropPanel
	extends PropertiesPanel
    implements ActionListener
{
	// resources
	public static final ResId
		LABEL_USER		= new ResId ("MsgServerProfilePropPanel.label.User"),
		LABEL_PROFILE	= new ResId ("MsgServerProfilePropPanel.label.Profile"),
		LABEL_PASSWORD
			= new ResId ("MsgServerProfilePropPanel.label.Password"),
		LABEL_CONFIRM_PASSWORD
			= new ResId ("MsgServerProfilePropPanel.label.ConfirmPassword"),
		ERR_LOAD_USER
			= new ResId ("MsgServerProfilePropPanel.error.loadUser");

	// help id
	public final static HelpId
		HID_MSGSERVER_PROFILE = new HelpId ("wsl.fw.MsgServerProfilePropPanel");

	// attributes
	private boolean _isBuilding = false;

	// controls
	private WslTextField _txtProfile        = new WslTextField (200);
	private WslPasswordField _txtPassword = new WslPasswordField (200);
	private WslPasswordField _txtConfirmPassword = new WslPasswordField (200);
	private WslComboBox  _cmbUser    = new WslComboBox (200);

	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public
	MsgServerProfilePropPanel ()
	{
		// init controls
		initMsgServerProfilePropPanelControls ();

		// build the user combo
		buildUserCombo ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Init the panel's controls.
	 */
	private void
	initMsgServerProfilePropPanelControls ()
	{
		// set layout
		setLayout (new GridBagLayout ());
		GridBagConstraints gbc = new GridBagConstraints ();

		// User
		WslLabel lbl = new WslLabel (LABEL_USER.getText ());
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets.left = GuiConst.DEFAULT_INSET;
		gbc.insets.top = GuiConst.DEFAULT_INSET;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0.2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add (lbl, gbc);
		gbc.gridx = 1;
		gbc.weightx = 0.8;
		gbc.insets.right = GuiConst.DEFAULT_INSET;
		add (_cmbUser, gbc);
		_cmbUser.addActionListener (this);
		addMandatory (LABEL_USER.getText (), _cmbUser);

		// Profile
		lbl = new WslLabel (LABEL_PROFILE.getText ());
		gbc.insets.right = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		add (lbl, gbc);
		gbc.gridx = 1;
		gbc.insets.right = GuiConst.DEFAULT_INSET;
		add (_txtProfile, gbc);
		addMandatory (LABEL_PROFILE.getText (), _txtProfile);

		// confirm password
		lbl = new WslLabel (LABEL_PASSWORD.getText ());
		gbc.insets.right = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		add (lbl, gbc);
		gbc.gridx = 1;
		gbc.insets.right = GuiConst.DEFAULT_INSET;
		gbc.weighty = 1;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		add (_txtPassword, gbc);
	}

	/**
	 * Build User combo
	 */
	private void
	buildUserCombo ()
	{
		try
		{
			// set building flag
			_isBuilding = true;

			// build query
			DataSource sysDs = DataManager.getSystemDS ();
			Query q = new Query (User.ENT_USER);

			//select all data views
			RecordSet rs = sysDs.select (q);

			// build combo
			_cmbUser.buildFromRecordSet (rs);

			// clear building flag
			_isBuilding = false;
		}
		catch (Exception e)
		{
			GuiManager.showErrorDialog (this, ERR_LOAD_USER.getText (), e);
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
	}

	//--------------------------------------------------------------------------
	/**
	 * Transfer data between the Category DataObject and panel controls.
	 * @param toDataObject, determines the direction of the transfer.
	 */
	public void
	transferData (
	 boolean toDataObject)
	{
		// must have a DataObject
		MessageServerProfile dobj = (MessageServerProfile) getDataObject ();
		Util.argCheckNull (dobj);

		if (toDataObject)
		{
			// to the Category DataObject
			dobj.setProfileName (_txtProfile.getText ());
			dobj.setPassword (new String (_txtPassword.getPassword ()));

			// get the user id
			Object userId = null;
			if (_cmbUser.getSelectedItem () != null)
			{
				User user = (User) _cmbUser.getSelectedItem ();
				Util.argCheckNull (user);

				// set user and id into profile
				userId = user.getId ();
				dobj.setUser (user);
				dobj.setUserId (userId);
			}
			else
				throw new RuntimeException ("No selected user");
		}
		else
		{
			// select the user in the combo
			if (dobj.getState () == DataObject.IN_DB)
			{
				User user = dobj.getUser ();
				Util.argCheckNull (user);
				_isBuilding = true;
				selectComboOnId (_cmbUser, user.getId ());
				_isBuilding = false;

				// set name and pw
				_txtProfile.setText (dobj.getProfileName ());
				_txtPassword.setText (dobj.getPassword ());
			}
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Used to select a user ina combo by its FLD_ID.
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
				if ( ((DataObject) combo.getItemAt (i)).getObjectValue (User.FLD_ID).equals (id))
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
		return new Dimension (380, 180);
	}

	//--------------------------------------------------------------------------
	/**
	 * @return the ActionPropertiesPanel help id.
	 */
	public HelpId
	getHelpId ()
	{
		return HID_MSGSERVER_PROFILE;
	}
}

/**	$Id: JdbcDriverPropPanel.java,v 1.3 2002/08/06 21:23:36 jonc Exp $
 *
 *	Property Panel for defining JdbcDrivers
 *
 */
package wsl.mdn.admin;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JLabel;

import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;
import wsl.fw.util.Util;
import wsl.mdn.dataview.JdbcDriver;

public class JdbcDriverPropPanel
	extends PropertiesPanel
{
	// resources
	public static final ResId
		LABEL_NAME		= new ResId ("JdbcDriverPropPanel.label.Name"),
		LABEL_DRIVER	= new ResId ("JdbcDriverPropPanel.label.Driver"),
		LABEL_DESCRIPTION
			= new ResId ("JdbcDriverPropPanel.label.Description");

	public final static HelpId
		HID_JDBC_DRIVER = new HelpId ("mdn.admin.JdbcDriverPropPanel");

	// controls
	private WslTextField
		_txtName		= new WslTextField (250),
		_txtDriver		= new WslTextField (250),
		_txtDescription	= new WslTextField (250);


	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public
	JdbcDriverPropPanel ()
	{
		// init controls
		initJdbcDriverPropPanelControls ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Init the panel's controls.
	 */
	private void
	initJdbcDriverPropPanelControls ()
	{
		// set layout
		setLayout (new GridBagLayout ());
		GridBagConstraints gbc = new GridBagConstraints ();

		//
		JLabel lbl = new JLabel (LABEL_NAME.getText ());
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
		gbc.insets = new Insets (3, 0, 3, 0);
		add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add (_txtName, gbc);
		addMandatory (LABEL_NAME.getText(), _txtName);

		//
		lbl = new JLabel (LABEL_DRIVER.getText ());
		gbc.gridwidth = 1;
		add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add (_txtDriver, gbc);
		addMandatory (LABEL_DRIVER.getText (), _txtDriver);

		//
		lbl = new JLabel (LABEL_DESCRIPTION.getText ());
		gbc.gridwidth = 1;
		add (lbl, gbc);

		add (Box.createHorizontalStrut (10));
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add (_txtDescription, gbc);
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
		JdbcDriver dobj = (JdbcDriver) getDataObject ();
		Util.argCheckNull (dobj);

		if (toDataObject)
		{
			// to the Category DataObject
			dobj.setName (_txtName.getText ());
			dobj.setDriver (_txtDriver.getText ());
			dobj.setDescription (_txtDescription.getText ());
		}
		else
		{
			// to the controls
			_txtName.setText (dobj.getName ());
			_txtDriver.setText (dobj.getDriver ());
			_txtDescription.setText (dobj.getDescription ());
		}
	}

	/**
	 * Return the preferred size
	 */
	public Dimension
	getPreferredSize ()
	{
		return new Dimension (350, 150);
	}

	/**
	 * If the subclass has help override this to specify the HelpId.
	 * This help is displayed using the parent wizards's help button.
	 * @return the HelpId of the help to display, if null the help button is not
	 *   displayed.
	 */
	public HelpId
	getHelpId ()
	{
		return HID_JDBC_DRIVER;
	}
}

/**	$Id: MdnAdminFrame.java,v 1.3 2002/07/10 23:56:24 jonc Exp $
 *
 *
 */
package wsl.mdn.admin;

import java.beans.SimpleBeanInfo;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import wsl.fw.datasource.Query;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.WslApplicationFrame;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslDialog;
import wsl.fw.gui.WslPanel;
import wsl.fw.gui.WslSwingApplication;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;
import wsl.fw.resource.ResourceManager;
import wsl.fw.security.SecurityManager;
import wsl.fw.security.gui.LoginPanel;
import wsl.fw.security.gui.UserGroupMaintenancePanel;
import wsl.fw.util.Config;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.JdbcDataSourceDobj;
import wsl.mdn.dataview.JdbcDriver;
import wsl.mdn.guiconfig.LoginSettings;
import wsl.mdn.guiconfig.PresentationBuilderPanel;
import wsl.mdn.mdnmsgsvr.MdnMaintainMsgServers;
import wsl.mdn.server.MdnServer;

public class MdnAdminFrame
	extends WslApplicationFrame
	implements ActionListener
{
	// resources
	public static final ResId TITLE  = new ResId ("MdnAdminFrame.title");
	public static final ResId MENU_FILE  = new ResId ("MdnAdminFrame.menu.File");
	public static final ResId MENU_FILE_LOGIN  = new ResId ("MdnAdminFrame.menu.FileLogin");
	public static final ResId MENU_FILE_CHANGE_PASSWORD  = new ResId ("MdnAdminFrame.menu.FileChangePassword");
	public static final ResId MENU_FILE_EXIT  = new ResId ("MdnAdminFrame.menu.FileExit");
	public static final ResId MENU_MAINTAIN  = new ResId ("MdnAdminFrame.menu.Maintain");
	public static final ResId MENU_MAINTAIN_DATA_SOURCES  = new ResId ("MdnAdminFrame.menu.MaintainDataSources");
	public static final ResId MENU_MAINTAIN_DATA_VIEWS  = new ResId ("MdnAdminFrame.menu.MaintainDataViews");
	public static final ResId MENU_MAINTAIN_USERS_GROUPS  = new ResId ("MdnAdminFrame.menu.MaintainUsersGroups");
	public static final ResId MENU_MAINTAIN_PRESENTATION_BUILDER  = new ResId ("MdnAdminFrame.menu.MaintainPresentationBuilder");
	public static final ResId MENU_MAINTAIN_DATA_TRANSFER  = new ResId ("MdnAdminFrame.menu.MaintainDataTransfers");
	public static final ResId MENU_MAINTAIN_SCHEDULE  = new ResId ("MdnAdminFrame.menu.MaintainSchedule");
	public static final ResId MENU_MAINTAIN_JDBCDRIVERS  = new ResId ("MdnAdminFrame.menu.MaintainJdbcDrivers");
	public static final ResId MENU_MAINTAIN_MIRRORS  = new ResId ("MdnAdminFrame.menu.MaintainMirrors");
	public static final ResId MENU_MAINTAIN_MSGSERVERS  = new ResId ("MdnAdminFrame.menu.MaintainMessageServers");
	public static final ResId MENU_HELP  = new ResId ("MdnAdminFrame.menu.Help");
	public static final ResId MENU_HELP_ABOUT  = new ResId ("MdnAdminFrame.menu.HelpAbout");
	public static final ResId MENU_HELP_CONTENTS  = new ResId ("MdnAdminFrame.menu.HelpContents");
	public static final ResId TOOLTIP_MAINTAIN_DATA_SOURCES  = new ResId ("MdnAdminFrame.tooltip.MaintainDataSources");
	public static final ResId TOOLTIP_MAINTAIN_DATA_VIEWS  = new ResId ("MdnAdminFrame.tooltip.MaintainDataViews");
	public static final ResId TOOLTIP_MAINTAIN_USERS_GROUPS  = new ResId ("MdnAdminFrame.tooltip.MaintainUsersGroups");
	public static final ResId TOOLTIP_MAINTAIN_PRESENTATION_BUILDER  = new ResId ("MdnAdminFrame.tooltip.MaintainPresentationBuilder");
	public static final ResId TOOLTIP_MAINTAIN_DATA_TRANSFER  = new ResId ("MdnAdminFrame.tooltip.MaintainDataTransfers");
	public static final ResId TOOLTIP_MAINTAIN_SCHEDULE  = new ResId ("MdnAdminFrame.tooltip.MaintainSchedule");
	public static final ResId TOOLTIP_MAINTAIN_MSGSERVERS  = new ResId ("MdnAdminFrame.tooltip.MaintainMessageServers");
	public static final ResId TOOLTIP_ABOUT  = new ResId ("MdnAdminFrame.tooltip.About");
	public static final ResId TOOLTIP_EXIT  = new ResId ("MdnAdminFrame.tooltip.Exit");
	public static final ResId LOGIN_PANEL  = new ResId ("MdnAdminFrame.login.panel");

	// help id
	public final static HelpId HID_CONTENTS = new HelpId ("mdn.admin.contents");

	// attributes
	private boolean _bypassSecurity;

	// controls
	private JMenuItem _mnuFileExit;
	private JMenuItem _mnuFileLogin;
	private JMenuItem _mnuFileChangePassword;
	private JMenuItem _mnuMaintainDataSources;
	private JMenuItem _mnuMaintainDataViews;
	private JMenuItem _mnuMaintainUsersGroups;
	private JMenuItem _mnuMaintainPresentationBuilder;
	private JMenuItem _mnuMaintainDataTransfers;
	private JMenuItem _mnuMaintainTransferSchedule;
	private JMenuItem _mnuMaintainJdbcDrivers;
	private JMenuItem _mnuMaintainMirrors;
	private JMenuItem _mnuMaintainMsgServers;
	private JMenuItem _mnuHelpAbout;
	private JMenuItem _mnuHelpContents;
	private WslButton _btnMaintainDataSources;
	private WslButton _btnMaintainDataViews;
	private WslButton _btnMaintainUsersGroups;
	private WslButton _btnMaintainPresentationBuilder;
	private WslButton _btnMaintainDataTransfers;
	private WslButton _btnMaintainTransferSchedule;
	private WslButton _btnMaintainJdbcDrivers;
	private WslButton _btnMaintainMirrors;
	private WslButton _btnMaintainMsgServers;
	private WslButton _btnFileExit;
	private WslButton _btnHelpAbout;

	public
	MdnAdminFrame ()
	{
		super (TITLE.getText () + " - " + MdnAdminApp.TEXT_VERSION.getText ()
			+ " " + MdnServer.VERSION_NUMBER,
			new Dimension (MdnAdminApp.isFeatureEnabled (MdnAdminApp.FID_MSG_SERVERS)? 560: 510, 100));
		this.setResizable (false);

		// setting icon to application frame
		URL  iconURL = ClassLoader.getSystemResource (
			MdnAdminConst.SS_IMAGE_PATH + "mdn_icon.gif");
		Image img = java.awt.Toolkit.getDefaultToolkit ().createImage (iconURL);
		this.setIconImage (img);


		// load bypass login, if true we are not forced to login and ignore security
		_bypassSecurity = Config.getProp ("MdnAdminFrame.bypassLogin",
			"false").equalsIgnoreCase ("true");

		// init the menu
		initMenu ();

		// init the toolbar
		initToolBar ();

		// ensure buttons and menu items are in correct state
		updateControls ();
	}
	//--------------------------------------------------------------------------
	/**
	 * Create the menu bar and menus
	 */
	private void
	initMenu ()
	{
		// create the menu bar
		JMenuBar mb = new JMenuBar ();
		setJMenuBar (mb);

		// add the file menu
		JMenu mnu = new JMenu (MENU_FILE.getText ());
		mb.add (mnu);

		// only add login if bypass security
		_mnuFileLogin = new JMenuItem (MENU_FILE_LOGIN.getText ());
		_mnuFileLogin.addActionListener (this);
		if (_bypassSecurity)
			mnu.add (_mnuFileLogin);

		_mnuFileChangePassword = new JMenuItem (MENU_FILE_CHANGE_PASSWORD.getText ());
		_mnuFileChangePassword.addActionListener (this);
		mnu.add (_mnuFileChangePassword);

		mnu.addSeparator ();
		_mnuFileExit = new JMenuItem (MENU_FILE_EXIT.getText ());
		_mnuFileExit.addActionListener (this);
		mnu.add (_mnuFileExit);

		// add the maintain menu
		mnu = new JMenu (MENU_MAINTAIN.getText ());
		mb.add (mnu);

		_mnuMaintainDataSources = new JMenuItem (MENU_MAINTAIN_DATA_SOURCES.getText ());
		_mnuMaintainDataSources.addActionListener (this);
		mnu.add (_mnuMaintainDataSources);

		_mnuMaintainDataViews = new JMenuItem (MENU_MAINTAIN_DATA_VIEWS.getText ());
		_mnuMaintainDataViews.addActionListener (this);
		mnu.add (_mnuMaintainDataViews);

		_mnuMaintainUsersGroups = new JMenuItem (MENU_MAINTAIN_USERS_GROUPS.getText ());
		_mnuMaintainUsersGroups.addActionListener (this);
		mnu.add (_mnuMaintainUsersGroups);

		_mnuMaintainPresentationBuilder = new JMenuItem (MENU_MAINTAIN_PRESENTATION_BUILDER.getText ());
		_mnuMaintainPresentationBuilder.addActionListener (this);
		mnu.add (_mnuMaintainPresentationBuilder);

		_mnuMaintainDataTransfers = new JMenuItem (MENU_MAINTAIN_DATA_TRANSFER.getText ());
		_mnuMaintainDataTransfers.addActionListener (this);
		mnu.add (_mnuMaintainDataTransfers);

		_mnuMaintainTransferSchedule = new JMenuItem (MENU_MAINTAIN_SCHEDULE.getText ());
		_mnuMaintainTransferSchedule.addActionListener (this);
		mnu.add (_mnuMaintainTransferSchedule);

		mnu.addSeparator ();
		_mnuMaintainJdbcDrivers = new JMenuItem (MENU_MAINTAIN_JDBCDRIVERS.getText ());
		_mnuMaintainJdbcDrivers.addActionListener (this);
		mnu.add (_mnuMaintainJdbcDrivers);

		_mnuMaintainMirrors = new JMenuItem (MENU_MAINTAIN_MIRRORS.getText ());
		_mnuMaintainMirrors.addActionListener (this);
		mnu.add (_mnuMaintainMirrors);

		if (MdnAdminApp.isFeatureEnabled (MdnAdminApp.FID_MSG_SERVERS))
		{
			_mnuMaintainMsgServers = new JMenuItem (MENU_MAINTAIN_MSGSERVERS.getText ());
			_mnuMaintainMsgServers.addActionListener (this);
			mnu.add (_mnuMaintainMsgServers);
		}

		// add the help menu
		mnu = new JMenu (MENU_HELP.getText ());
		mb.add (mnu);
		_mnuHelpAbout = new JMenuItem (MENU_HELP_ABOUT.getText ());
		_mnuHelpAbout.addActionListener (this);
		mnu.add (_mnuHelpAbout);

		_mnuHelpContents = new JMenuItem (MENU_HELP_CONTENTS.getText ());
		_mnuHelpContents.addActionListener (this);
		mnu.add (_mnuHelpContents);
	}

	//--------------------------------------------------------------------------
	/**
	 * Initialise the application ToolBar
	 */
	private void
	initToolBar ()
	{
		// create the toolbar
		JToolBar tb = new JToolBar ();
		tb.setFloatable (false);
		this.getContentPane ().add (tb);

		// add buttons
		Dimension d = new Dimension (50, 50);

		_btnMaintainDataSources = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "datasources_big.gif"), d, this);
		_btnMaintainDataSources.setToolTipText (TOOLTIP_MAINTAIN_DATA_SOURCES.getText ());
		tb.add (_btnMaintainDataSources);

		_btnMaintainDataViews = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "dataviews_big.gif"), d, this);
		_btnMaintainDataViews.setToolTipText (TOOLTIP_MAINTAIN_DATA_VIEWS.getText ());
		tb.add (_btnMaintainDataViews);

		_btnMaintainUsersGroups = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "usersgroups_big.gif"), d, this);
		_btnMaintainUsersGroups.setToolTipText (TOOLTIP_MAINTAIN_USERS_GROUPS.getText ());
		tb.add (_btnMaintainUsersGroups);

		_btnMaintainPresentationBuilder = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "presbuilder_big.gif"), d, this);
		_btnMaintainPresentationBuilder.setToolTipText (TOOLTIP_MAINTAIN_PRESENTATION_BUILDER.getText ());
		tb.add (_btnMaintainPresentationBuilder);

		_btnMaintainDataTransfers = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "datatransfer_big.gif"), d, this);
		_btnMaintainDataTransfers.setToolTipText (TOOLTIP_MAINTAIN_DATA_TRANSFER.getText ());
		tb.add (_btnMaintainDataTransfers);

		_btnMaintainTransferSchedule = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "schedule_big.gif"), d, this);
		_btnMaintainTransferSchedule.setToolTipText (TOOLTIP_MAINTAIN_SCHEDULE.getText ());
		tb.add (_btnMaintainTransferSchedule);

		_btnMaintainMirrors = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "mirrors_big.gif"), d, this);
		_btnMaintainMirrors.setToolTipText (MENU_MAINTAIN_MIRRORS.getText ());
		tb.add (_btnMaintainMirrors);

		_btnMaintainJdbcDrivers = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "drivers_big.gif"), d, this);
		_btnMaintainJdbcDrivers.setToolTipText (MENU_MAINTAIN_JDBCDRIVERS.getText ());
		tb.add (_btnMaintainJdbcDrivers);

		if (MdnAdminApp.isFeatureEnabled (MdnAdminApp.FID_MSG_SERVERS))
		{
			_btnMaintainMsgServers = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "message_big.gif"), d, this);
			_btnMaintainMsgServers.setToolTipText (MENU_MAINTAIN_MSGSERVERS.getText ());
			tb.add (_btnMaintainMsgServers);
		}

		_btnHelpAbout = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "help_big.gif"), d, this);
		_btnHelpAbout.setToolTipText (TOOLTIP_ABOUT.getText ());
		tb.add (_btnHelpAbout);

		_btnFileExit = new WslButton (Util.resourceIcon (MdnAdminConst.SS_IMAGE_PATH + "exit_big.gif"), d, this);
		_btnFileExit.setToolTipText (TOOLTIP_EXIT.getText ());
		tb.add (_btnFileExit);
	}

	/**
	 * Action listener override for menu and button clicks
	 */
	public void
	actionPerformed (
	 ActionEvent ev)
	{
		try
		{
			// switch on source
			if (ev.getSource ().equals (_mnuFileLogin))
				onLogin ();
			else if (ev.getSource ().equals (_mnuFileChangePassword))
				onChangePassword ();

			else if (ev.getSource ().equals (_mnuMaintainDataSources) || ev.getSource ().equals (_btnMaintainDataSources))
				onMaintainDataSources ();
			else if (ev.getSource ().equals (_mnuMaintainDataViews) || ev.getSource ().equals (_btnMaintainDataViews))
				onMaintainDataViews ();
			else if (ev.getSource ().equals (_mnuMaintainUsersGroups) || ev.getSource ().equals (_btnMaintainUsersGroups))
				onMaintainUsersGroups ();
			else if (ev.getSource ().equals (_mnuMaintainPresentationBuilder) || ev.getSource ().equals (_btnMaintainPresentationBuilder))
				onMaintainPresentationBuilder ();
			else if (ev.getSource ().equals (_mnuMaintainDataTransfers) || ev.getSource ().equals (_btnMaintainDataTransfers))
				onMaintainDataTransfers ();
			else if (ev.getSource ().equals (_mnuMaintainTransferSchedule) || ev.getSource ().equals (_btnMaintainTransferSchedule))
				onMaintainTransferSchedule ();
			else if (ev.getSource ().equals (_mnuMaintainJdbcDrivers) || ev.getSource ().equals (_btnMaintainJdbcDrivers))
				onMaintainJdbcDrivers ();
			else if (ev.getSource ().equals (_mnuMaintainMirrors) || ev.getSource ().equals (_btnMaintainMirrors))
				onMaintainMirrors ();
			else if (ev.getSource ().equals (_mnuMaintainMsgServers) || ev.getSource ().equals (_btnMaintainMsgServers))
				onMaintainMsgServers ();
			else if (ev.getSource ().equals (_mnuHelpAbout) || ev.getSource ().equals (_btnHelpAbout))
				onHelpAbout ();
			else if (ev.getSource ().equals (_mnuHelpContents))
				onHelpContents ();
			else if (ev.getSource ().equals (_mnuFileExit) || ev.getSource ().equals (_btnFileExit))
				onFileExit ();
		}
		catch (Exception e)
		{
			GuiManager.showErrorDialog (this, MdnAdminConst.ERR_UNHANDLED.getText (), e);
			Log.error (MdnAdminConst.ERR_UNHANDLED.getText (), e);
		}
	}

	/**
	 * File / Login action selected
	 */
	private void
	onLogin ()
	{
		// open the login panel
		LoginPanel.openLoginPanel (this, LOGIN_PANEL.getText ());

		// if a login actually succeeds then we no longer bypass security
		// and no longe want login
		_bypassSecurity = false;
		_mnuFileLogin.setEnabled (false);

		// update the buttons and menu items as priv will have changed
		updateControls ();
	}

	/**
	 * File / Change Password selected.
	 */
	private void
	onChangePassword ()
	{
		SecurityManager.changeLoggedInUserPassword (this);
	}

	/**
	 * File / Exit menu item clicked
	 */
	private void
	onFileExit ()
		throws Exception
	{
		WslSwingApplication.exitApplication ();
	}

	/**
	 * Maintain / Orders menu item clicked
	 */
	private void
	onMaintainDataSources ()
	{
		GuiManager.openWslPanel (this, new MaintainDataSourcesPanel (), false);
	}

	/**
	 * Maintain JDBC Drivers menu item clicked
	 */
	private void
	onMaintainJdbcDrivers ()
	{
		GuiManager.openListMaintenancePanel (this, JdbcDriver.class, false);
	}

	/**
	 * Maintain Mirrors menu item clicked
	 */
	private void
	onMaintainMirrors ()
	{
		GuiManager.openWslPanel (this, new MirrorListPanel (), false);
	}

	/**
	 * Maintain / Products menu item clicked
	 */
	private void
	onMaintainDataViews ()
	{
		GuiManager.openWslPanel (this, new MaintainDataViewsPanel (), false);
	}

	/**
	 * Maintain / Customers menu item clicked
	 */
	private void
	onMaintainUsersGroups ()
	{
		GuiManager.openWslPanel (this, new MdnUserGroupMaintenancePanel (), false);
	}

	/**
	 * Maintain / Categories menu item clicked
	 */
	private void
	onMaintainPresentationBuilder ()
	{
		GuiManager.openWslPanel (this, new PresentationBuilderPanel (), false);
	}

	/**
	 * Maintain / Categories menu item clicked
	 */
	private void
	onMaintainDataTransfers ()
	{
		GuiManager.openWslPanel (this, new MaintainDataTransfersPanel (), false);
	}

	/**
	 * Maintain Message Servers menu item clicked
	 */
	private void
	onMaintainMsgServers ()
	{
		GuiManager.openWslPanel (this, new MdnMaintainMsgServers (), false);
	}

	/**
	 * Maintain / Categories menu item clicked
	 */
	private void
	onMaintainTransferSchedule ()
	{
		// Dummy Maintain Schedule
		//WslPanel wslPanel = new WslPanel ();
		//WslDialog d = new WslDialog (wslPanel, this, "Dummy Maintain Schedule", true);
		//d.setSize (300,500);
		//d.show ();
		// End of Dummy Maintain Schedule

		GuiManager.openWslPanel (this, new MaintainTransferSchedulePanel (), false);
	}

	/**
	 * Help / About menu item clicked
	 */
	private void
	onHelpAbout ()
	{
		// fixme, open the about box
		//GuiManager.openMaintenancePanel (this, new MdnHelpAboutPanel (), false);
		GuiManager.openWslPanel (this, new MdnHelpAboutPanel (), false);
	}

	/**
	 * Help / Contents menu item clicked
	 */
	private void
	onHelpContents ()
	{
		HID_CONTENTS.displayHelp ();
	}

	/**
	 * Overriden show method, forces a login to the application
	 */
	public void
	show ()
	{
		// super
		super.show ();

		// login
		if (!_bypassSecurity)
			onLogin ();
	}

	/**
	 * Update the buttons and menu items to reflect the logged in users
	 * Privileges.
	 */
	protected void
	updateControls ()
	{
		boolean bChangePassword = (SecurityManager.getLoggedInUser () != null);
		_mnuFileChangePassword.setEnabled (bChangePassword);
	}
}

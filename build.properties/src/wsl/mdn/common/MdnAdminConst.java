/**	$Id: MdnAdminConst.java,v 1.3 2002/07/18 21:38:25 jonc Exp $
 *
 * Mdn Admin constants.
 *
 */
package wsl.mdn.common;

import java.awt.Dimension;
import wsl.fw.resource.ResId;

public class MdnAdminConst
{
	/** resource for unhandled error message */
	public static final ResId ERR_UNHANDLED =
		new ResId("MdnAdminConst.error.unhandled");

	/** the path for images. */
	public final static String SS_IMAGE_PATH = "wsl/mdn/resource/images/";

	/** resource path to mdn config file */
	public final static String MDN_CONFIG_FILE = "resource://wsl/config/mdn/mdn.conf";

	/** config key to schedule period in MS */
	public final static String SCHEDULE_MANAGER_PERIOD = "wsl.mdn.server.MdnServer.ScheduleManager.sleeptime";

	/** config key to schedule period in MS */
	public final static String SCHEDULE_MANAGER_START = "wsl.mdn.server.MdnServer.ScheduleManager.start";

	/** config key to license manager cleanup period in MS */
	public final static String LICENSE_MANAGER_CLEANUPPERIOD = "wsl.mdn.server.LicenseManager.ScheduleManager.cleanupPeriod";

	/** config key to license manager license timeout period in MS */
	public final static String LICENSE_MANAGER_LICENSETIMEOUT = "wsl.mdn.server.LicenseManager.ScheduleManager.licenseTimeout";

	/** */
	public final static String WMLSERVLET_LOG_WML = "wsl.mdn.wap.MdnWmlServlet.logWml";

	/** Config context key for MdnWmlServlet */
	public final static String WMLSERVLET_CONTEXT = "wsl.mdn.wap.MdnWmlServlet";

	/** Config context key for MdnHtmlServlet */
	public final static String HTMLSERVLET_CONTEXT = "wsl.mdn.html.MdnHtmlServlet";

	/** default maintenence panel size */
	public static final Dimension DEFAULT_PANEL_SIZE = new Dimension (620, 475);
	
    
	/** The path for uploading the database driver */
	public final static String DATABASE_DRIVER_UPLOAD_PATH = "DatabaseDriver.uploadUrl";
	
	/** The path for exporting project */
	public final static String EXPORT_PROJECT_FILE_PATH = "ExportProject.downloadUrl";
	
	/** The path for importing project */
	public final static String IMPORT_RPOJECT_FILE_PATH = "ImportProject.uploadUrl";
	
	public final static String WEB_SERVICE_COMPILE_FILE_PATH = "WebService.compileFilePath";

	/** The path for LOG */
	public final static String LOG_FILE_PATH = "logFile.filePath";
}

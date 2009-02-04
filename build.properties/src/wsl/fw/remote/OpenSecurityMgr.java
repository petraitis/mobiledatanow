/**	$Id: OpenSecurityMgr.java,v 1.1 2002/06/27 03:10:50 jonc Exp $
 *
 *	An open security manager. Allow everything to get thru'.
 *	Gets around having to put
 *
 *			permission java.security.AllPermission;
 *
 *	in jre/lib/security/java.policy
 *
 */
package wsl.fw.remote;

import java.security.Permission;

public class OpenSecurityMgr
	extends SecurityManager
{
	public void
	checkPermission (
	 Permission p)
	{
		/*
		 *	Do nothing stub.
		 */
	}
}

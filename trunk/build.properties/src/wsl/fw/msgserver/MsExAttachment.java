/**	$Id: MsExAttachment.java,v 1.2 2002/11/12 23:18:41 jonc Exp $
 *
 *	MsExchange attachment description
 *
 */
package wsl.fw.msgserver;

public class MsExAttachment
{
	public final String _name, _application, _pathname;

	public
	MsExAttachment (
	 String name,
	 String application,
	 String pathname)
	{
		_name = name;
		_application = application;
		_pathname = pathname;
	}
}
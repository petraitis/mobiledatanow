/**	$Id: ShowMessageDelegate.java,v 1.3 2003/01/08 20:44:17 tecris Exp $
 *
 * 	Display contents of a message
 *
 */
package wsl.mdn.html;

import wsl.fw.util.Util;
import wsl.fw.msgserver.ActionDobj;
import wsl.fw.msgserver.CriteriaAction;
import wsl.fw.msgserver.MailMessageDobj;
import wsl.fw.msgserver.MessageDobj;

import java.io.IOException;
import javax.servlet.ServletException;

public class ShowMessageDelegate
	extends MdnHtmlServletDelegate
{
    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public ShowMessageDelegate()
    {
    }

    //--------------------------------------------------------------------------
    // Output WML

    /**
     * Called by servlet
     */
    public void run() throws ServletException, IOException
    {
		MessageDobj m = null;
		PagedItrMsgDelegate pmd = null; 
        try
        {
            // validate
            Util.argCheckNull(_request);
            Util.argCheckNull(_response);

            // get the record and index
            String strIndex = _request.getParameter(MdnHtmlServlet.PV_RECORD_INDEX);
            if(strIndex != null && strIndex.length() > 0)
            {
                int index = Integer.parseInt(strIndex);

                // get the message
                pmd = (PagedItrMsgDelegate) getUserState().getCurrentPagedItDelegate();
                m = pmd.getMessage(index);
                Util.argCheckNull(m);

                // delegate based on message type
                if(m instanceof CriteriaAction)
                    delegate(new GetCriteriaDelegate((CriteriaAction)m));
                else if(m instanceof ActionDobj)
				{
					getUserState ().setIterator (pmd);
                    delegate(new DoActionDelegate((ActionDobj)m));
				}
                else if(m instanceof MailMessageDobj)
                    delegate(new ShowMailDelegate());
                else
                    delegate(new ShowGenericMessageDelegate());
            }

        } catch (ArrayIndexOutOfBoundsException e)
		{
			m = ((PagedItrMsgDelegate)getUserState ().getIterator()).getMessage(0);
			delegate(new DoActionDelegate((ActionDobj)m));
            onError(e);
        } catch (Exception e)
        {
            onError(e);
        }
    }
}

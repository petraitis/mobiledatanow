/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/wap/ShowMessageDelegate.java,v 1.3 2003/01/16 21:02:18 tecris Exp $
 *
 */
package wsl.mdn.wap;

// imports
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.ecs.wml.*;
import wsl.fw.servlet.*;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.resource.ResId;
import wsl.fw.wml.WEUtil;
import wsl.fw.datasource.*;
import wsl.fw.msgserver.*;
import wsl.mdn.guiconfig.LoginSettings;

public class ShowMessageDelegate extends MdnWmlServletDelegate
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
            String strIndex = _request.getParameter(MdnWmlServlet.PV_RECORD_INDEX);
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
					getUserState ().setIndex (index);
                    delegate(new DoActionDelegate((ActionDobj)m));
			    }
                else if(m instanceof MailMessageDobj)
                    delegate(new ShowMailDelegate());
/*                else if(m instanceof ContactDobj)
                    delegate(new ShowGenericMessageDelegate());
                else if(m instanceof AppointmentDobj)
                    delegate(new ShowAppointmentDelegate());
                else if(m instanceof TaskDobj)
                    delegate(new ShowTaskDelegate());
*/                else
                    delegate(new ShowGenericMessageDelegate());
            }
        } catch (ArrayIndexOutOfBoundsException e)
		{
			int index = getUserState ().getIndex ();
			m = ((PagedItrMsgDelegate)getUserState ().getIterator()).getMessage(index);
			delegate(new DoActionDelegate((ActionDobj)m));
		}
        catch(Exception e)
        {
            onError(e);
        }
    }
}

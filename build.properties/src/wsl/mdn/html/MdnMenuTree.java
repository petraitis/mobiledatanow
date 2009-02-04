package wsl.mdn.html;


// imports
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.html.*;
import org.apache.ecs.*;
import wsl.fw.datasource.*;
import wsl.fw.util.Util;
import wsl.fw.html.WslHtmlTree;
import wsl.fw.html.WslHtmlTreeNode;
import wsl.fw.html.*;
import wsl.mdn.guiconfig.*;
import wsl.mdn.dataview.*;
import java.io.IOException;
import javax.servlet.ServletException;


/**
 * WslHtmlTree subclass for categories
 */
public class MdnMenuTree extends WslHtmlTree
{
    //--------------------------------------------------------------------------
    // attributes

    private UserState _userState = null;


    //--------------------------------------------------------------------------
    // constructions

    /**
     * Ctor
     * @param response the HttpServletResponse
     */
    public MdnMenuTree(HttpServletRequest request, HttpServletResponse response,
        UserState userState)
        throws Exception
    {
        // super
        super(new WslHtmlTreeNode(null));

        // set attribs
        _userState = userState;

        // build children
        buildChildren(getRoot());

        // build tree
        buildTree(request, response);
    }

    /**
     * Build the child nodes of param node
     * @param parent the node to build
     */
    public void buildChildren(WslHtmlTreeNode parent) throws Exception
    {
        // if null, return
        if(parent == null)
            return;

        // clear the vector
        parent.children.clear();

        // get parent menu action
        MenuAction parentMa = (MenuAction)parent.data;
        if(parentMa == null)
        {
            // if root, get root menu
            parentMa = _userState.getRootMenu();
        }

        // iterate over menu elements adding them to the parent
        WslHtmlTreeNode child;
        Vector menuItems = parentMa.getChildren();
        for (int i = 0; i < menuItems.size(); i++)
        {
            // get the item
            MenuAction item = (MenuAction) menuItems.get(i);
            parent.addChildNode(new WslHtmlTreeNode(item));
        }
    }

    /**
     * Compare a node element against a param
     * @return boolean true if elements are equal
     */
    public boolean compareNode(WslHtmlTreeNode node, Object compare)
    {
        // validate
        Util.argCheckNull(node);
        Util.argCheckNull(compare);

        // compare ma id
        MenuAction ma = (MenuAction)node.data;
        return (ma == null || ma.getId() == null)? false: ma.getId().toString().equals(compare.toString());
    }

    /**
     * @return ConcreteElement representation of the node
     * @param node
     */
    public ConcreteElement getNodeElement(HttpServletResponse response, WslHtmlTreeNode node)
    {
        try
        {
            // if cached, return
            if(node != null && node.element != null)
                return node.element;

            // create element from data
            MenuAction ma = (MenuAction)node.data;
            if(ma != null)
            {
                // build the element
                node.element = makeMenuElement(response, ma);
                return node.element;
            }
            else
                return null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }


    //--------------------------------------------------------------------------
    /**
     * Make a menu element for the specified menu item
     * @param item the menu action item
     */
    private ConcreteElement makeMenuElement(HttpServletResponse response, MenuAction item)
        throws ServletException, IOException
    {
        // get the maid
        ConcreteElement e = null;
        String maid = (item.getId() == null)? "-1": item.getId().toString();
        int groupId = item.getIntGroupId();

        // validate the dvid
        Integer objDvId = new Integer(item.getIntDataViewId());
        if(objDvId != null && objDvId.intValue() > 0)
        {
            try
            {
                DataView dv = _userState.getCache().getDataView(objDvId);
                if(groupId >= 0 && dv != null && !dv.isGroupEnabled(groupId))
                    return null;
            }
            catch(Exception ex)
            {
                throw new RuntimeException(ex.toString());
            }
        }

        if (item instanceof Submenu)
        {

            // submenu
            // submenu + menu id
            Submenu suItem = (Submenu) item;
            e = makeMenuHrefA(response,
                MdnHtmlServlet.ACT_MENU,
                suItem.getId().toString(),
                WslHtmlUtil.esc(suItem.getName()),
                maid);
        }
        else if (item instanceof QueryRecords)
        {
            // Query
            // query + query id
            QueryRecords qrItem = (QueryRecords) item;
            e = makeMenuHrefA(response,
                MdnHtmlServlet.ACT_QUERYRECORDS,
                qrItem.getQueryId().toString(),
                WslHtmlUtil.esc(qrItem.getName()),
                maid);
        }
        else if (item instanceof NewRecord)
        {
            // new record
            // new record + DV id
            NewRecord nrItem = (NewRecord) item;
            e = makeMenuHrefA(response,
                MdnHtmlServlet.ACT_NEWRECORD,
                nrItem.getDataViewId().toString(),
                WslHtmlUtil.esc(nrItem.getName()),
                maid);
        }
        else if (item instanceof TextAction)
        {
            // text
            TextAction taItem = (TextAction) item;
            // add text or link depending on type
            if (taItem.hasPhoneLink())
            {
                e = new StringElement(WslHtmlUtil.esc(taItem.getName()) +
                    ": " + WslHtmlUtil.esc(taItem.getPhoneLink()));
            }
            else if (taItem.hasUrlLink())
            {
                A link = new A(WslHtmlUtil.esc(taItem.getUrlLink()));
                link.addElement(WslHtmlUtil.esc(taItem.getName()));
                e = link;
            }
            else
                e = new StringElement(WslHtmlUtil.esc(taItem.getName()));
        }
        else if (item instanceof LogoutAction)
        {
            // logout
            e = new A(MdnHtmlServlet.makeHref(response, MdnHtmlServlet.ACT_LOGOUT),
                MdnHtmlServlet.TEXT_LOGOUT.getText());
        }
        else if (item instanceof MsgServerAction)
        {
            // get the action
            MsgServerAction msa = (MsgServerAction)item;
            int actionType = msa.getActionType();
            String action = MdnHtmlServlet.ACT_MSGSERVER;

            // make the element
            //e = new StringElement(WslHtmlUtil.esc(msa.getName()));
            e = makeMenuHrefA(response, action,
                msa.getId().toString(),
                WslHtmlUtil.esc(msa.getName()),
                maid);
        }

        // return
        return e;
    }


   //--------------------------------------------------------------------------
    // make menu href

    /**
     * Make a menu href and A
     * @param action
     * @param subAction
     * @param text
     * @param groupId
     * @return A
     */
    private A makeMenuHrefA(HttpServletResponse response, String action, String subAction, String text, String maid)
    {
        // create href
        String href = MdnHtmlServlet.makeHref(response, action, subAction);

        // add groupId
        if(maid != null && maid.length() > 0)
            href += WslHtmlUtil.esc("&" + MdnHtmlServlet.PV_MENUACTIONID + "=" + maid);

        // create p
        return new A(href, text);
    }


    //--------------------------------------------------------------------------
    // tree icons

    /**
     * @return IMG the open image
     */
    public IMG getOpenImage(String context, WslHtmlTreeNode node)
    {
        // get the menu action
        MenuAction ma = (MenuAction)node.data;
        if(ma != null)
        {
            // build the image path
            String imagePath = "/images/";

            // switch on type
            if(ma instanceof TextAction)
            {
                TextAction ta = (TextAction)ma;
                if(ta.hasPhoneLink())
                    imagePath += "phone.gif";
                else if(ta.hasUrlLink())
                    imagePath += "url.gif";
                else
                    imagePath += "text.gif";
            }
            else if(ma instanceof QueryRecords)
                imagePath += "query.gif";
            else if(ma instanceof NewRecord)
                imagePath += "newrec.gif";
            else if(ma instanceof MsgServerAction)
                imagePath += "msgserver.gif";
            else
                return super.getOpenImage(context, node);
            return new IMG(context + imagePath);
        }
        else
            return super.getOpenImage(context, node);
    }

    /**
     * @return IMG the closed image
     */
    public IMG getClosedImage(String context, WslHtmlTreeNode node)
    {
        // get the menu action
        MenuAction ma = (MenuAction)node.data;
        if(ma != null)
        {
            // build the image path
            String imagePath = "/images/";

            // switch on type
            if(ma instanceof TextAction)
            {
                TextAction ta = (TextAction)ma;
                if(ta.hasPhoneLink())
                    imagePath += "phone.gif";
                else if(ta.hasUrlLink())
                    imagePath += "url.gif";
                else
                    imagePath += "text.gif";
            }
            else if(ma instanceof QueryRecords)
                imagePath += "query.gif";
            else if(ma instanceof NewRecord)
                imagePath += "newrec.gif";
            else if(ma instanceof MsgServerAction)
                imagePath += "msgserver.gif";
            else
                return super.getClosedImage(context, node);
            return new IMG(context + imagePath);
        }
        else
            return super.getClosedImage(context, node);
    }
}
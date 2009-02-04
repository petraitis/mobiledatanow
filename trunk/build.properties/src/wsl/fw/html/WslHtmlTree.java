
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.html;

// imports
import java.util.Vector;
import java.net.URL;
import java.lang.ClassLoader;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.ecs.Element;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ECSDefaults;
import org.apache.ecs.html.*;
import wsl.fw.util.Util;
import wsl.fw.html.WslHtmlTable;

/**
 * Implements an explorer-like tree in HTML
 */
public class WslHtmlTree extends WslHtmlTable
{
    //--------------------------------------------------------------------------
    // attributes

    private WslHtmlTreeNode _root;
    private Caption _caption;
    private static IMG _imgOpen;
    private static IMG _imgClosed;
    private static Vector _spaces = new Vector();


    //--------------------------------------------------------------------------
    // init

    /**
     * Blank ctor
     */
    public WslHtmlTree()
    {
        // init
        init();
    }

    /**
     * Ctor taking a root
     * @param root the root node
     */
    public WslHtmlTree(WslHtmlTreeNode root)
    {
        this(root, null);
    }

    /**
     * Ctor taking a root and a caption
     * @param root the root node
     * @param caption the caption for the tree
     */
    public WslHtmlTree(WslHtmlTreeNode root, Caption caption)
    {
        // init
        init();

        // set the caption
        setCaption(caption);

        // set the root
        setRoot(root);
    }

    /**
     * Initialise the static table contents
     */
    private void init()
    {
    }

    /**
     * Sets the caption
     * @param caption the caption to set
     */
    public void setCaption(Caption caption)
    {
        _caption = caption;
    }

    /**
     * Set the root into the table
     * @param root
     */
    public void setRoot(WslHtmlTreeNode root)
    {
        _root = root;
    }

    /**
     * @return WslHtmlTreeNode the root node
     */
    public WslHtmlTreeNode getRoot()
    {
        return _root;
    }

    /**
     * @return IMG the open image
     */
    public IMG getOpenImage(String context, WslHtmlTreeNode node)
    {
        // if null, create
        if(_imgOpen == null)
            _imgOpen = new IMG(context + "/images/folder_open.gif");
        return _imgOpen;
    }

    /**
     * @return IMG the closed image
     */
    public IMG getClosedImage(String context, WslHtmlTreeNode node)
    {
        // if null, create
        if(_imgClosed == null)
            _imgClosed = new IMG(context + "/images/folder_closed.gif");
        return _imgClosed;
    }

    /**
     * @return IMG the transparent image
     */
    public IMG getTransparentImage(int index, String context)
    {
        // if null, create
        if(_spaces.size() <= index)
            _spaces.add(new IMG(context + "/images/transparent.gif"));
        return (IMG)_spaces.elementAt(index);
    }


    //--------------------------------------------------------------------------
    // build tree

    /**
     * Build the categories into the tree
     */
    public void buildTree(HttpServletRequest request, HttpServletResponse response)
    {
        // clear the table
        removeAllElements();

        // add the caption
        if(_caption != null)
            addElement(_caption);

        // build the tree from the data
        //setPrettyPrint(true);
        recurseBuildTree(_root, request, response);
    }

    /**
     * Recursively build the tree
     */
    private void recurseBuildTree(WslHtmlTreeNode parent, HttpServletRequest request,
        HttpServletResponse response)
    {
        // must have a parent
        if(parent == null)
            return;

        // iterate the children
        WslHtmlTreeNode child;
        for(int i = 0; parent.children != null && i < parent.children.size(); i++)
        {
            // get the node
            child = (WslHtmlTreeNode)parent.children.elementAt(i);
            if(child != null && child.data != null)
            {
                // create the row and cell
                TR row = new TR();
                TD cell = new TD();

                // indent
                for(int j = 0; j < (child.level - 1); j++)
                    cell.addElement(getTransparentImage(j, request.getContextPath()));

                // get the cell element
                ConcreteElement cellElement;
                String cellString = "";
                if((cellElement = getNodeElement(response, child)) == null)
                    cellString = getNodeString(child);

                // open closed folder
                IMG image;
                if(child.state == WslHtmlTreeNode.CLOSED)
                    image = getClosedImage(request.getContextPath(), child);
                else
                    image = getOpenImage(request.getContextPath(), child);

                // if the cell element is a link, carete a link for the folder
                ConcreteElement folderElement;
                if(cellElement != null && cellElement instanceof A)
                {
                    folderElement = new A(cellElement.getAttribute("href"), image);
                    image.addAttribute("border", "0");
                }
                else
                    folderElement = image;

                // add folder and cell element to cell
                cell.addElement(folderElement);
                if(cellElement != null)
                    cell.addElement(cellElement);
                else
                    cell.addElement(cellString);

                // add the cell to the row and row to table
                row.addElement(cell);
                addElement(row);

                // recurse into children
                recurseBuildTree(child, request, response);
            }
        }
    }


    /**
     * Find a node from a compare object
     * @param compare the data to compare
     * @return WslHtmlTreeNode the node if found, else null
     */
    public WslHtmlTreeNode findNode(Object compare)
    {
        // recurse
        return recurseFindNode(_root, compare);
    }

    /**
     * Recursively find a node from a compare object
     * @param compare the data to compare
     * @return WslHtmlTreeNode the node if found, else null
     */
    private WslHtmlTreeNode recurseFindNode(WslHtmlTreeNode parent, Object compare)
    {
        // must have a parent
        if(parent == null)
            return null;

        // compare
        if(compareNode(parent, compare))
            return parent;

        // recurse children
        else if(parent.children != null)
        {
            // iterate
            WslHtmlTreeNode found = null;
            for(int i = 0; found == null && i < parent.children.size(); i++)
                found = recurseFindNode((WslHtmlTreeNode)parent.children.elementAt(i), compare);
            return found;
        }

        // invalid bottom leaf
        else
            return null;
    }

    /**
     * Toggle a node
     * @param request
     * @param response
     * @param node the node to toggle
     */
    public void toggleNode(HttpServletRequest request, HttpServletResponse response, WslHtmlTreeNode node) throws Exception
    {
        // validate
        if(node == null)
            return;

        // if closed, open
        if(node.state == WslHtmlTreeNode.CLOSED)
            openNode(request, response, node, true);

        // else close
        else
            closeNode(request, response, node, true);
    }

    /**
     * Close the node
     * @param request
     * @param response
     * @param node the node to close
     * @param doRebuildTree builds tree if true
     */
    public void closeNode(HttpServletRequest request, HttpServletResponse response,
        WslHtmlTreeNode node, boolean doRebuildTree)
    {
        // validate
        Util.argCheckNull(node);

        // clear children
        if(node.children.size() > 0)
            node.children.clear();

        // change state
        node.state = WslHtmlTreeNode.CLOSED;

        // rebuild the tree
        if(doRebuildTree)
            buildTree(request, response);
    }

    /**
     * Open the node
     * @param request
     * @param response
     * @param node the node to open
     * @param doRebuildTree builds tree if true
     */
    public void openNode(HttpServletRequest request, HttpServletResponse response,
        WslHtmlTreeNode node, boolean doRebuildTree) throws Exception
    {
        // validate
        Util.argCheckNull(node);

        // close other nodes
        closeOtherNodes(request, response, node);

        // build children
        buildChildren(node);

        // change state
        node.state = WslHtmlTreeNode.OPEN;

        // rebuild the tree
        if(doRebuildTree)
            buildTree(request, response);
    }

    /**
     * Close other nodes at same level
     * @param node the node (and parents of the is node) not to be closed
     * @param request
     * @param response
     */
    private void closeOtherNodes(HttpServletRequest request, HttpServletResponse response,
        WslHtmlTreeNode node)
    {
        // validate
        Util.argCheckNull(node);

        // get the parent of the param
        WslHtmlTreeNode parentNode = node.parent;

        // iterate parents children (ie siblings)
        WslHtmlTreeNode temp;
        for(int i = 0; parentNode != null && i < parentNode.children.size(); i++)
        {
            // if not param parent, remove children
            temp = (WslHtmlTreeNode)parentNode.children.elementAt(i);
            if(temp != null && temp != node && temp.state == WslHtmlTreeNode.OPEN)
                closeNode(request, response, temp, false);
        }
    }

    /**
     * Compare a node element against a param
     * @return boolean true if elements are equal
     */
    public boolean compareNode(WslHtmlTreeNode node, Object compare)
    {
        return (node.data == null)? false: node.data.equals(compare);
    }

    /**
     * @return Element representation of the node
     * @param node
     */
    public ConcreteElement getNodeElement(HttpServletResponse response, WslHtmlTreeNode node)
    {
        return null;
    }

    /**
     * @return String representation of the node
     * @param node
     */
    public String getNodeString(WslHtmlTreeNode node)
    {
        return (node == null)? "": node.toString();
    }

    /**
     * Build the child nodes of param node
     * @param parent the node to build
     */
    public void buildChildren(WslHtmlTreeNode parent) throws Exception
    {
    }
}
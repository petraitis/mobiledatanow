//==============================================================================
// DoTreeNode.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;
import wsl.fw.datasource.DataObject;

//--------------------------------------------------------------------------
/**
 * A subclass of DefaultMutableTreeNode, to allow it to support DataObjects
 * and icons. Uses the icon specified for rendering in cooperation with
 * DataObjectTree and DataObjectTreeCellRenderer. If no icon is set and the
 * userObject is a DataObject The GuiManager is interrogated for the icon.
 *
 * Normal use is to construct containing a DataObject or a String and icon
 * then add to a parent node in the model of a DataObjectTree.
 * If any extra data (beside the string or data object) is required then it can
 * be stored in the _extraData using setExtraData() or the appropriate
 * constructor.
 *
 * Alternatively the addNode... methods on DataObjectTree can be used to create
 * DoTreeNodes and add them to the model.
 */
public class DoTreeNode extends DefaultMutableTreeNode
{
    // version tag
    private final static String _ident = "$Date: 2004/01/20 04:01:34 $  $Revision: 1.2 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/DoTreeNode.java $ ";

    // attributes
    private Icon    _icon = null;
    private Object  _extraData = null;
    private boolean _areChildrenBuilt = false;

    //--------------------------------------------------------------------------
    /**
     * Required Constructor, see DefaultMutableTreeNode constructors.
     */
    public DoTreeNode()
    {
    }

    //--------------------------------------------------------------------------
    /**
     * Required Constructor, see DefaultMutableTreeNode constructors.
     */
    public DoTreeNode(Object userObject)
    {
        super(userObject);
    }

    //--------------------------------------------------------------------------
    /**
     * Required Constructor, see DefaultMutableTreeNode constructors.
     */
    public DoTreeNode(Object obj, boolean allowsChildren)
    {
        super(obj, allowsChildren);
    }

    //--------------------------------------------------------------------------
    /**
     * New constructor taking a userObject, extra data and icon.
     * @param obj, the userObject.
     * @param extraData, any extra data to be assorciated with this node.
     * @param icon, the Icon to use in displaying this node, if null the
     *   DataObject icon or default icon will be used.
     */
    public DoTreeNode(Object obj, Object extraData, Icon icon)
    {
        super(obj);
        _extraData = extraData;
        _icon = icon;
    }

    //--------------------------------------------------------------------------
    /**
     * New constructor taking a userObject and icon.
     * @param obj, the userObject.
     * @param icon, the Icon to use in displaying this node, if null the
     *   DataObject icon or default icon will be used.
     */
    public DoTreeNode(Object obj, Icon icon)
    {
        this(obj, null, icon);
    }

    //--------------------------------------------------------------------------
    /**
     * New constructor taking a userObject and extra data.
     * @param obj, the userObject.
     * @param extraData, any extra data to be assorciated with this node.
     */
    public DoTreeNode(Object obj, Object extraData)
    {
        this(obj, extraData, null);
    }

    //--------------------------------------------------------------------------
    /**
     * New constructor taking a userObject and icon.
     * @param obj, the userObject.
     * @param allowsChildren, if true node may have children.
     * @param icon, the Icon to use in displaying this node, if null the
     *   DataObject icon or default icon will be used.
     */
    public DoTreeNode(Object obj, boolean allowsChildren, Icon icon)
    {
        super(obj, allowsChildren);
        _icon = icon;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the DataObject.
     * @return the DataObject, may be null.
     */
    public DataObject getDataObject()
    {
        return (userObject instanceof DataObject) ? (DataObject) userObject : null;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the icon fo rendering this node.
     * @param icon, the icon used for tree display, if null the DataObject
     *   icon or default icon will be used.
     */
    public void setIcon(Icon icon)
    {
        _icon = icon;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the icon to use when displaying our DoTreeNode as a leaf.
     * @return the Icon;
     */
    public Icon getLeafIcon()
    {
        if (_icon != null)
            return _icon;

        GuiMapNode gmn = getGuiMapNode();
        return (gmn != null) ? gmn._leafIcon : null;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the icon to use when displaying our DoTreeNode as open.
     * @return the Icon;
     */
    public Icon getOpenIcon()
    {
        if (_icon != null)
            return _icon;

        GuiMapNode gmn = getGuiMapNode();
        return (gmn != null) ? gmn._openIcon : null;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the icon to use when displaying our DoTreeNode as closed.
     * @return the Icon;
     */
    public Icon getClosedIcon()
    {
        if (_icon != null)
            return _icon;

        GuiMapNode gmn = getGuiMapNode();
        return (gmn != null) ? gmn._closedIcon : null;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the GuiMapNode associated with this class of DataObject.
     * @return the GuiMapNode or null if there is no DataObject or association.
     */
    protected GuiMapNode getGuiMapNode()
    {
        DataObject dobj = getDataObject();
        if (dobj != null)
        {
            // is a DataObject, get the class and return the GuiMapNode
            Class cls = dobj.getClass();
            return GuiManager.getGuiManager().getGuiMapNode(dobj);
        }

        // not a data object
        return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the extra data object, used to store any application specific extra
     * data that is needed in conjunction with the basic user object or
     * DataObject.
     * @param extraData, the extra data object to set.
     */
    public void setExtraData(Object extraData)
    {
        _extraData = extraData;
    }

    //--------------------------------------------------------------------------
    /**
     * Get the extra data.
     * @return the extra data.
     */
    public Object getExtraData()
    {
        return _extraData;
    }

    //--------------------------------------------------------------------------
    /**
     * @return the value of the _areChildrenBuilt flag. Used by users of the
     *   tree to monitor the build state.
     */
    public boolean getChildrenBuilt()
    {
        return _areChildrenBuilt;
    }

    //--------------------------------------------------------------------------
    /**
     * Set the value of the _areChildrenBuilt flag. Used by users of the
     *   tree to control the build state.
     */
    public void setChildrenBuilt(boolean val)
    {
        _areChildrenBuilt = val;
    }
    
    public boolean isLeaf ()
    {
        return false;
    }
}

//==============================================================================
// end of file DoTreeNode.java
//==============================================================================

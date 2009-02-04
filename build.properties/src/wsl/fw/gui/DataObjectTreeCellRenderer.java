//==============================================================================
// DataObjectTreeCellRenderer.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTree;
import wsl.fw.datasource.DataObject;

//------------------------------------------------------------------------------
/**
 * Renderer for tree nodes that may be DoTreeNodes, which have individual icon
 * infomation. Uses default behaviour if the TreeNode is not a DoTreeNode.
 */
public class DataObjectTreeCellRenderer extends DefaultTreeCellRenderer
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/DataObjectTreeCellRenderer.java $ ";

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public DataObjectTreeCellRenderer()
    {
    }

    //--------------------------------------------------------------------------
    /**
      * Custom renderer that checks if we have a DefaultMutableTreeNode
      * containing a DataUserObject, if so then it uses the icons from the
      * DataUserObject, otherwise it has the default behaviour.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        if (value instanceof DoTreeNode)
        {
            DoTreeNode node = (DoTreeNode) value;
            // copied and modified from the superclass implementation
            // we have a DoTreeNode, use it to set up the text and
            // icon rendering
            String stringValue = node.toString();
            this.hasFocus = hasFocus;
            setText(stringValue);
            if(sel)
                setForeground(getTextSelectionColor());
            else
                setForeground(getTextNonSelectionColor());

            // choose the icon, if the node does not have an icon use the
            // renderer's defualt.
            Icon icon;
            if (leaf)
                icon = (node.getLeafIcon() != null) ? node.getLeafIcon() : getLeafIcon();
            else if (expanded)
                icon = (node.getOpenIcon() != null) ? node.getOpenIcon() : getOpenIcon();
            else
                icon = (node.getClosedIcon() != null) ? node.getClosedIcon() : getClosedIcon();

            // set the icon
            if (!tree.isEnabled())
            {
                setEnabled(false);
                setDisabledIcon(icon);
            }
            else
            {
                setEnabled(true);
                setIcon(icon);
            }

            setComponentOrientation(tree.getComponentOrientation());
            selected = sel;

            // return our customised renderer
            return this;
        }

        // no DataUserObject, use default behaviour
        return super.getTreeCellRendererComponent(tree, value, sel, expanded,
            leaf, row, hasFocus);
    }
}

//==============================================================================
// end of file DataObjectTreeCellRenderer.java
//==============================================================================

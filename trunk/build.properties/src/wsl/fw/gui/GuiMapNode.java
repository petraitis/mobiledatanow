
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package wsl.fw.gui;

import javax.swing.Icon;

/**
 * A structure that contains information mapped to DataObject class
 */
public class GuiMapNode
{
    // attributes
    public Class  _dataObjectClass = null;
    public Class  _propPanelClass = null;
    public String _title = "";
    public Icon   _defaultIcon = null;
    public Icon   _leafIcon = null;
    public Icon   _openIcon = null;
    public Icon   _closedIcon = null;

    /**
     * Param constructor
     * @param Class dataObjectClass The class of the DataObject
     * @param Class propPanelClass The class of the PropertiesPanel
     * @param title readable title of the class
     */
    public GuiMapNode(Class dataObjectClass, Class propPanelClass, String title)
    {
        _dataObjectClass = dataObjectClass;
        _propPanelClass = propPanelClass;
        _title = title;
    }

    /**
     * Construct a GuiMapNode specifying the icon to be used to display for
     * the DataObject when used in a DataObjectTree.
     * @param Class dataObjectClass The class of the DataObject
     * @param Class propPanelClass The class of the PropertiesPanel
     * @param title readable title of the class
     * @param defaultIcon, the icon for any display.
     * @param leafIcon, the icon for leaf tree display, default used if null.
     * @param openIcon, the icon for open tree display, default used if null.
     * @param closedIcon, the icon for closed tree display, default used if null.
     */
    public GuiMapNode(Class dataObjectClass, Class propPanelClass, String title,
        Icon defaultIcon, Icon leafIcon, Icon openIcon, Icon closedIcon)
    {
        // call standard constructor
        this(dataObjectClass, propPanelClass, title);

        // set the icons, any that are null will use the default icon
        _defaultIcon = defaultIcon;
        _leafIcon = (leafIcon != null) ? leafIcon : defaultIcon;
        _openIcon = (openIcon != null) ? openIcon : defaultIcon;
        _closedIcon = (closedIcon != null) ? closedIcon : defaultIcon;
    }

    /**
     * Construct a GuiMapNode specifying the icon to be used to display for
     * the DataObject when used in a DataObjectTree.
     * @param Class dataObjectClass The class of the DataObject
     * @param Class propPanelClass The class of the PropertiesPanel
     * @param title readable title of the class
     * @param defaultIcon, the icon for any display.
     */
    public GuiMapNode(Class dataObjectClass, Class propPanelClass, String title,
        Icon defaultIcon)
    {
        // delegate to other constructor, default Icon will be used for all
        this(dataObjectClass, propPanelClass, title, defaultIcon, null, null, null);
    }
}
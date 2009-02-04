//==============================================================================
// DoComparator.java
// Copyright (c) 2000 WAP Solutions Ltd.
//==============================================================================

package wsl.fw.gui;

import java.util.Comparator;
import java.io.Serializable;
import javax.swing.tree.DefaultMutableTreeNode;

//--------------------------------------------------------------------------
/**
 * A class to act as a comparator for DataObjects. Intended for use with
 * DataObjectTree.setSort(), but could also be used with SortedSet or SortedMap.
 * Will work fine with any target object, not just data objects.
 *
 * Sorts as text by the toString() value of the objects, which is what the tree
 * will display for the item's text.
 * Also sorts by the class of the object so that objects of the same class will
 * be grouped together.
 * The classFirst param determines whether toString or class is the primary
 * sort.
 * The reverse flag determines whether the sort order is reversed.
 * Null objects are considered smaller than any real object.
 *
 * Uses basic String.compareTo/compareToIgnoreCase. could be extended to allow
 * use of a locale sensitive Collator.
 *
 * Has special handling for DefaultMutableTreeNode (and therefore DoTreeNode)
 * that will convert objects of this type to their contained userObjects to
 * allow easier searching and sorting of trees.
 *
 * Could easily be extended to sort on an arbitrary DataObject field.
 */
public class DoComparator implements Comparator, Serializable
{
    // version tag
    private final static String _ident = "$Date: 2002/06/11 23:11:42 $  $Revision: 1.1.1.1 $ "
        + "$Archive: /Framework/Source/wsl/fw/gui/DoComparator.java $ ";

    // attributes that define the comparison order
    private boolean _reverse;
    private boolean _caseSensitive;
    private boolean _classFirst;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     * Class first case insensitive sort.
     */
    public DoComparator()
    {
        this(true, false, false);
    }

    //--------------------------------------------------------------------------
    /**
     * Constructor specifying all sort criteria.
     * @param classFirst, if true objects are first sorted by class, then by
     *   toString() text, else text then class.
     * @param caseSensitive, if true the text comparison is case sensitive.
     * @param reverse, if true the sort order is reversed.
     */
    public DoComparator(boolean classFirst, boolean caseSensitive,
        boolean reverse)
    {
        _classFirst    = classFirst;
        _caseSensitive = caseSensitive;
        _reverse       = reverse;
    }

    //--------------------------------------------------------------------------
    /**
     * The compare function.
     * @param o1, the first object to compare.
     * @param o2, the second object to compare.
     * @return a negative integer, zero, or a positive integer as the first
     *   argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2)
    {
        int cmp;

        // convert DefaultMutableTreeNodes to their contained userObjects
        if (o1 != null && o1 instanceof DefaultMutableTreeNode)
            o1 = ((DefaultMutableTreeNode) o1).getUserObject();

        if (o2 != null && o2 instanceof DefaultMutableTreeNode)
            o2 = ((DefaultMutableTreeNode) o2).getUserObject();


        // handle nulls
        if (o1 == null)
            if (o2 == null)
                cmp = 0;
            else
                cmp = -1;
        else
            if (o2 == null)
                cmp = 1;
            else
            {
                // normal case, no nulls, compare normally
                // get class and text comparisons
                int cmpText = (_caseSensitive ?
                    o1.toString().compareTo(o2.toString()) :
                    o1.toString().compareToIgnoreCase (o2.toString()));
                int cmpClass = o1.getClass().getName().compareTo(o2.getClass().getName());

                if (_classFirst)
                {
                    // class first sort
                    cmp = cmpClass;
                    if (cmp == 0)
                        cmp = cmpText;
                }
                else
                {
                    // text first sort
                    cmp = cmpText;
                    if (cmp == 0)
                        cmp = cmpClass;
                }
            }

        // reverse sort order if reverse flag
        if (_reverse)
            cmp = -cmp;

        return cmp;
    }
}

//==============================================================================
// end of file DoComparator.java
//==============================================================================

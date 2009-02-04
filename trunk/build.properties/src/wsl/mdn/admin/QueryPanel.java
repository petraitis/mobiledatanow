package wsl.mdn.admin;

import wsl.fw.gui.WslPanel;
import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import wsl.fw.gui.GuiConst;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class QueryPanel extends WslPanel
{
    private Vector _criteriaPanels = new Vector();
    private GridBagConstraints _gbc;

    public QueryPanel()
    {
        setLayout(new GridBagLayout());
        _gbc = new GridBagConstraints();
        _gbc.fill = GridBagConstraints.HORIZONTAL;
/*
        _gbc.insets.top = GuiConst.DEFAULT_INSET;
        _gbc.insets.bottom = GuiConst.DEFAULT_INSET;
*/
        _gbc.anchor = GridBagConstraints.NORTHWEST;
        _gbc.insets.left = GuiConst.DEFAULT_INSET;
        _gbc.insets.right = GuiConst.DEFAULT_INSET;
        _gbc.weightx = 1;
        _gbc.gridwidth = 1;
        _gbc.gridx = -1;
        _gbc.gridy = 0;

/*
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEFT);
        setLayout(fl);
*/
    }

    /**
     * Add a HorizontalCriteriaPanel to the panel and Vector
     */
    public void addCriteriaField(HorizontalCriteriaPanel panel)
    {
        if (++_gbc.gridx == 2)
        {
            _gbc.gridx = 0;
            ++_gbc.gridy;
        }

        _criteriaPanels.add(panel);
        add(panel, _gbc);
    }

    /**
     * Return the collection of HorizontalCriteriaPanels
     */
    public Vector getCriteriaFields()
    {
        return _criteriaPanels;
    }
/*
    public Dimension getPreferredSize()
    {
        return new Dimension(700, 200);
    }
*/
}
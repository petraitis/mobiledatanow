package wsl.mdn.admin;

// imports
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslLabel;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.WslPanel;
import wsl.fw.resource.ResId;
import wsl.mdn.dataview.QueryDobj;
import wsl.mdn.dataview.DataViewField;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class HorizontalCriteriaPanel extends WslPanel
{

    //--------------------------------------------------------------------------
    // controls

    private WslLabel _lblField;
    private WslLabel _lblOperator;
    private WslTextField _txtValue;

    //--------------------------------------------------------------------------
    // attributes
    public final static ResId  TEXT_OP_EQUALS              = new ResId("QueryRecordsDelegate.text.op.Equals");
    public final static ResId  TEXT_OP_NOT_EQUALS          = new ResId("QueryRecordsDelegate.text.op.NotEquals");
    public final static ResId  TEXT_OP_LIKE                = new ResId("QueryRecordsDelegate.text.op.Like");
    public final static ResId  TEXT_OP_NOT_LIKE            = new ResId("QueryRecordsDelegate.text.op.NotLike");
    public final static ResId  TEXT_OP_GREATER_THAN        = new ResId("QueryRecordsDelegate.text.op.GreaterThan");
    public final static ResId  TEXT_OP_GREATER_THAN_EQUALS = new ResId("QueryRecordsDelegate.text.op.GreaterThanEquals");
    public final static ResId  TEXT_OP_LESS_THAN           = new ResId("QueryRecordsDelegate.text.op.LessThan");
    public final static ResId  TEXT_OP_LESS_THAN_EQUALS    = new ResId("QueryRecordsDelegate.text.op.LessThanEquals");


    private QueryCriterium _qc;
    String _displayName;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default constructor.
     */
    public HorizontalCriteriaPanel(String displayName, QueryCriterium qc)
    {
        // set attrib
        _qc = qc;
        _displayName = displayName;

        // init controls
        initControls();
    }

    /**
     * Init the panel's controls.
     */
    private void initControls()
    {
        _lblField = new WslLabel(_displayName);
        _lblOperator = new WslLabel(getOperatorText(_qc));
        _txtValue = new WslTextField(50);

        // set layout
        setBorder(BorderFactory.createLoweredBevelBorder());
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // field and operator
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.top = 0;
        gbc.insets.right = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(_lblField, gbc);
        gbc.insets.left = 10;
        gbc.gridx = 1;
        add(_lblOperator, gbc);

        // value
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        add(_txtValue, gbc);
    }

    /**
     * @return the text content of the editable field
     */
    public String getValue()
    {
        return _txtValue.getText();
    }

    /**
     * @return the QueryCriterium that this criteria field was created with
     */
    public QueryCriterium getCriterium()
    {
        return _qc;
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(300, 30);
    }

    /**
     * @return text describing the query criterium operator
     */
    private String getOperatorText(QueryCriterium qc)
    {
        // fixme
        String op = qc._op;

        if (op.equals(QueryCriterium.OP_EQUALS))
            return TEXT_OP_EQUALS.getText();
        else if (op.equals(QueryCriterium.OP_NOT_EQUALS))
            return TEXT_OP_NOT_EQUALS.getText();
        else if (op.equals(QueryCriterium.OP_LIKE))
            return TEXT_OP_LIKE.getText();
        else if (op.equals(QueryCriterium.OP_NOT_LIKE))
            return TEXT_OP_NOT_LIKE.getText();
        else if (op.equals(QueryCriterium.OP_GREATER_THAN))
            return TEXT_OP_GREATER_THAN.getText();
        else if (op.equals(QueryCriterium.OP_GREATER_THAN_EQUALS))
            return TEXT_OP_GREATER_THAN_EQUALS.getText();
        else if (op.equals(QueryCriterium.OP_LESS_THAN))
            return TEXT_OP_LESS_THAN.getText();
        else if (op.equals(QueryCriterium.OP_LESS_THAN_EQUALS))
            return TEXT_OP_LESS_THAN_EQUALS.getText();
        else
            return "";
    }

}
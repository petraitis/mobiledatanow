/*	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/admin/ExecuteDataViewsPanel.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 *	Run a query
 *
 */
package wsl.mdn.admin;

// imports
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import wsl.fw.security.SecurityManager;
import wsl.fw.security.User;
import wsl.fw.security.UserWrapper;
import wsl.fw.util.Util;
import wsl.fw.help.HelpId;
import wsl.fw.util.Log;
import wsl.fw.util.Type;
import wsl.fw.util.TypeConversionException;
import wsl.fw.resource.ResId;
import wsl.fw.datasource.*;
import wsl.fw.gui.*;
import wsl.mdn.common.MdnDataManager;
import wsl.mdn.dataview.*;
import javax.swing.JOptionPane;

//------------------------------------------------------------------------------
/**
 * Panel used to execute qnd display a query.
 */
public class ExecuteDataViewsPanel extends WslButtonPanel
    implements ActionListener, ListSelectionListener
{
    // resources
    public static final ResId BUTTON_CLOSE  =
        new ResId("ExecuteDataViewsPanel.button.Close");
    public static final ResId BUTTON_DELETE  =
        new ResId("ExecuteDataViewsPanel.button.Delete");
    public static final ResId BUTTON_INSERT  =
        new ResId("ExecuteDataViewsPanel.button.Insert");
    public static final ResId BUTTON_UPDATE  =
        new ResId("ExecuteDataViewsPanel.button.Update");
    public static final ResId BUTTON_REQUERY  =
        new ResId("ExecuteDataViewsPanel.button.Requery");
    public static final ResId PANEL_TITLE  =
        new ResId("ExecuteDataViewsPanel.title");
    public static final ResId BUTTON_HELP
        = new ResId("OkPanel.button.Help");
    public static final ResId ERR_REQUERY =
        new ResId("ExecuteDataViewsPanel.error.requery");
    public static final ResId ERR_EMPTY_FIELDS =
        new ResId("ExecuteDataViewsPanel.error.emptyFields");
    public static final ResId ERR_SELECT =
        new ResId("ExecuteDataViewsPanel.error.select");
    public static final ResId ERR_DELETE =
        new ResId("ExecuteDataViewsPanel.error.delete");
    public static final ResId TEXT_CONFIRM_DELETE =
        new ResId("ExecuteDataViewsPanel.text.confirmDelete");

    // help id
    public final static HelpId HID_EXEC_DATAVIEW = new HelpId("mdn.admin.ExecuteDataViewsPanel");

    // constants
    private static final int BTN_WIDTH = 132;

    // attributes
    private WslButton _btnClose = new WslButton(BUTTON_CLOSE.getText(), BTN_WIDTH, this);
    private WslButton _btnDelete = new WslButton(BUTTON_DELETE.getText(), BTN_WIDTH, this);
    private WslButton _btnRequery = new WslButton(BUTTON_REQUERY.getText(), BTN_WIDTH, this);
    private WslButton _btnInsert = new WslButton(BUTTON_INSERT.getText(), BTN_WIDTH, this);
    private WslButton _btnUpdate = new WslButton(BUTTON_UPDATE.getText(), BTN_WIDTH, this);

    private WslPvTableView _tableView = null;
    private ExecDataTableModel _model = null;
    private RecordSet _rs = null;
    private QueryDobj _query = null;
    private DataView _dv = null;
    private QueryDobj _querydobj;
    private QueryPanel _queryPanel = null;

    //--------------------------------------------------------------------------
    /**
     * Blank constructor.
     */
    public ExecuteDataViewsPanel(QueryDobj querydobj)
    {
        // super
        super(WslButtonPanel.VERTICAL);

        // get the view ds
        DataViewDataSource dvds = MdnDataManager.getDataViewDS();

        _querydobj = querydobj;
        _dv = querydobj.getDataView();
        _query = querydobj;// querydobj.createImpl();
        try
        {
            if (_query.isComplete(null))
                _rs = executeQuery(dvds, _querydobj);
            else
            {
                // we have to supply query parameters
                buildQueryParamList();
            }
        }
        catch (Exception e)
        {
            GuiManager.showErrorDialog(this.getFrameParent(),
                "Error while selecting from view.", e);
            Log.error("Execute Query", e);
        }

        if (_rs == null)
            _rs = new RecordSet();

        // set title
        setPanelTitle(PANEL_TITLE.getText());

        // init controls
        initExecuteDataViewsPanelControls();

        // update buttons
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialise controls.
     */
    private void initExecuteDataViewsPanelControls()
    {
        // add buttons
        addButton(_btnInsert);
        addButton(_btnUpdate);
        addButton(_btnDelete);
        addHelpButton(BUTTON_HELP.getText(), HID_EXEC_DATAVIEW, BTN_WIDTH);
        _btnClose.setIcon(Util.resourceIcon(GuiConst.FW_IMAGE_PATH + "close.gif"));
        addButton(_btnClose);

        Vector dobjs = _rs.getRows();
        Vector flds = null;
        if(dobjs != null && dobjs.size() > 0)
            flds = ((DataObject)dobjs.elementAt(0)).getEntity().getFields();
        else
            flds = _dv.getFields();
        String colTitles[] = new String[flds.size()];
        DataViewField fld;

        for (int i = 0; i < flds.size(); i++)
        {
            fld = (DataViewField)flds.elementAt(i);
            colTitles[i] = fld.getDisplayName();
        }

        _model = new ExecDataTableModel(dobjs, colTitles, (Vector)flds.clone());
        _tableView = new WslPvTableView(_model, false); // true == editable, false is not
        _tableView.addRowSelectionListener(this);
        _tableView.setPreferredSize(new Dimension(700, 500));

        // add the tree to the main panel
        getMainPanel().setBorder(BorderFactory.createLoweredBevelBorder());
        getMainPanel().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        if (_queryPanel != null)
        {
            addButton(_btnRequery);
            getMainPanel().add(_queryPanel, gbc);
        }
        gbc.weightx = 1;
        gbc.weighty = 1;
        getMainPanel().add(_tableView, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Called by framework to get the default button for a WslPanel
     * @return a WslButton
     */
    public WslButton getDefaultButton()
    {
        return _btnRequery;
    }

    //--------------------------------------------------------------------------
    /**
     * Button clicked.
     */
    public void actionPerformed(ActionEvent ev)
    {
        try
        {
            // switch on source
            if(ev.getSource().equals(_btnClose))
                closePanel();
            else if(ev.getSource().equals(_btnDelete))
                onDelete();
            else if(ev.getSource().equals(_btnRequery))
                onRequery();

            // update buttons
            updateButtons();
        }
        catch(Exception e)
        {
            wsl.fw.gui.GuiManager.showErrorDialog(this,
                wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
            Log.error(wsl.mdn.common.MdnAdminConst.ERR_UNHANDLED.getText(), e);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Panel closing.
     */
    public void onClosePanel()
    {
        // remove DataChangeListeners
        _model.removeAllDataChangeListeners();

        // super
        super.onClosePanel();
    }

    //--------------------------------------------------------------------------
    /**
     * Requery the recordset based on the values entered in the criteria boxes.
     */
    public void onRequery()
    {
        Util.argCheckNull(_queryPanel);
        Util.argCheckNull(_query);

        Vector v = _queryPanel.getCriteriaFields();
        String val;
        HorizontalCriteriaPanel fld;

        // fill in criteria from CriteriaPAnel
        for (int i = 0; v != null && i < v.size(); i++)
        {
            fld = (HorizontalCriteriaPanel)v.elementAt(i);
            val = fld.getValue();
            QueryCriterium qc = fld.getCriterium();

            // check that the param is parseable to the desired type
            if (QueryCriterium.isBinary(qc._op) && !Util.isEmpty(val))
            {
                // get the field type
                int fieldType = -1;
                try
                {
                    // get DataViewField and use it to get the source Field
                    DataViewField dvf = (DataViewField) _dv.getField(qc._fieldName);
                    DataSourceDobj dsDobj    = _query.getDataView().getSourceDataSource();
                    EntityDobj     entDobj   = dsDobj.getEntity(dvf.getSourceEntity());
                    FieldDobj      fieldDobj = entDobj.getField(dvf.getSourceField());

                    // get the field type
                    fieldType = fieldDobj.getType();
                }
                catch (Exception e)
                {
                    // failed, show error
                    GuiManager.showErrorDialog(getFrameParent(), CriteriaPanel.ERR_FIELDTYPE.getText(), e);
                    return;
                }

                // test the conversion and display error if invalid
                try
                {
                    Type.convertValueOnType(val, fieldType);
                }
                catch (TypeConversionException e)
                {
                    GuiManager.showErrorDialog(getFrameParent(), e.getMessage(), null);
                    return;
                }
            }

            // parsed ok, set the criteria value
            qc._value = val;
        }

        DataViewDataSource dvds = MdnDataManager.getDataViewDS();

        // fixme, do we want to allow incomplete for qbe
        if (!_query.isComplete(null))
        {
            GuiManager.showMessageDialog(getFrameParent(), ERR_REQUERY.getText(),
                ERR_EMPTY_FIELDS.getText());
            return;
        }

        try
        {
            // do select and display results
            _rs = executeQuery(dvds, _query);
            Vector dobjs = _rs.getRows();
            _model.setDataObjects(dobjs);
        }
        catch (DataSourceException e)
        {
            // display error
            GuiManager.showErrorDialog(this, ERR_SELECT.getText(), e);
            Log.error("ExecuteDataViewsPanel.onRequery: ", e);
        }

        for (int i = 0; v != null && i < v.size(); i++)
        {
            // clear criterium ready for next time
            fld = (HorizontalCriteriaPanel)v.elementAt(i);
            QueryCriterium qc = fld.getCriterium();
            qc._value = QueryCriterium.INCOMPLETE_VALUE;
        }

    }

    /**
     * Execute the query. replace UID with the actual value from User
     */
    private RecordSet
	executeQuery (
	 DataViewDataSource dvds,
	 QueryDobj query)
	 	throws DataSourceException
    {
		// replace any userid criteria
		String uid = "";
//		UserWrapper userWrapper = SecurityManager.getSecurityManager ().getLoggedInUser ();
//		if (userWrapper.getLoginUser() != null &&
//			userWrapper.getLoginUser().getConfigSettings () != null)
//		{
//			uid = userWrapper.getLoginUser().getConfigSettings ();
//		}

		Vector v = query.getCriteria(null);
		QueryCriterium qc;
		for(int i = 0; v != null && i < v.size (); i++)
		{
			qc = (QueryCriterium) v.elementAt (i);
			if (qc._value != null &&
				qc._value.toString().equals (QueryCriterium.USERID_VALUE))
			{
				qc._value = uid;
			}
		}

        // select
        return dvds.select(query);
    }

    //--------------------------------------------------------------------------
    /**
     * Delete the currently selected record
     */
    public void onDelete()
    {
        int row = _tableView.getTable().getSelectedRow();
        if (row >= 0)
        {
            Record r = (Record)_model.getDataObjectAt(row);
            if (r != null)
            {
                StringBuffer msg = new StringBuffer(TEXT_CONFIRM_DELETE.getText());
                msg.append(r.toString());

                if (true == GuiManager.showConfirmDialog(getFrameParent(), msg.toString()))
                {
                    try
                    {
                        r.delete();
                    }
                    catch (DataSourceException e)
                    {
                        GuiManager.showErrorDialog(getFrameParent(), ERR_DELETE.getText(), e);
                    }
                }
            }
        }

    }

    //--------------------------------------------------------------------------
    /**
     * Update controls
     */
    public void updateButtons()
    {
        // check table selection
        int row = _tableView.getTable().getSelectedRow();
        boolean selected = (row >= 0);

        // enable
        _btnInsert.setEnabled(false);
        _btnUpdate.setEnabled(false);
        _btnDelete.setEnabled(selected);
    }

    //--------------------------------------------------------------------------
    /**
     * Post creation call by framework. Allows non-constructor-safe
     * initialisation of some controls.
     */
    public void postCreate()
    {
        // configure the table model
        _model.configureTable(_tableView);
    }

    //--------------------------------------------------------------------------
    /**
     *
     */
    public void valueChanged(ListSelectionEvent e)
    {
        updateButtons();
    }

    //--------------------------------------------------------------------------
    /**
     *
     */
    private void buildQueryParamList()
    {
        Vector criteria = _query.getCriteria(null);
        _queryPanel = new QueryPanel();

        for (int i = 0; i < criteria.size(); i++)
        {
            QueryCriterium qc = (QueryCriterium) criteria.get(i);

            if (!qc.isComplete())
            {
                DataViewField dvField = (DataViewField)_dv.getField(qc._fieldName);
                // get display name, field name and operator text
                String displayName = dvField.getDisplayName();
                qc._value = QueryCriterium.INCOMPLETE_VALUE; // param value goes here
                _queryPanel.addCriteriaField(new HorizontalCriteriaPanel(displayName, qc));
            }
        }
    }
}

//==============================================================================
// end of file ExecuteDataViewsPanel.java
//==============================================================================

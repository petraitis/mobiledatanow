package wsl.mdn.admin;

// imports
import wsl.fw.resource.ResId;
import wsl.fw.gui.ListMaintenancePanel;
import wsl.fw.datasource.*;
import wsl.mdn.dataview.JdbcDataSourceDobj;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MirrorListPanel extends ListMaintenancePanel
{
    //--------------------------------------------------------------------------
    // constants

    public static final ResId TITLE = new ResId("MirrorListPanel.label.Title");


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public MirrorListPanel()
    {
        // build only mirror dbs
        super(JdbcDataSourceDobj.class, new Query(new QueryCriterium(
                JdbcDataSourceDobj.ENT_DATASOURCE,
                JdbcDataSourceDobj.FLD_IS_MIRROR_DB, QueryCriterium.OP_EQUALS,
                new Boolean(true))),
            true);
        this.setPanelTitle(TITLE.getText());
    }


    //--------------------------------------------------------------------------
    // misc

    /**
     * Create a new instance
     */
    protected DataObject createNewInstance() throws Exception
    {
        JdbcDataSourceDobj dobj = (JdbcDataSourceDobj)_editClass.newInstance();
        dobj.setIsMirrorDb(true);
        return dobj;
    }
}
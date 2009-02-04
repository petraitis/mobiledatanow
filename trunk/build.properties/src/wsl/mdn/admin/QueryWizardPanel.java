package wsl.mdn.admin;

// imports
import java.awt.Dimension;
import wsl.fw.resource.ResId;
import wsl.fw.util.Util;
import wsl.fw.gui.*;
import wsl.mdn.dataview.*;

/**
 * Wizard for creating and maintaining Queries
 */
public class QueryWizardPanel extends WslWizardPanel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId WIZARD_TITLE  =
        new ResId("QueryWizardPanel.Title");


    //--------------------------------------------------------------------------
    // attributes

    private QueryDobj _query = null;
    private DataView _parentDv;
    private boolean _isNew = false;

    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public QueryWizardPanel()
    {
        super(WIZARD_TITLE.getText());
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return QueryDobj the Query created or edited by this wizard
     */
    public QueryDobj getQuery()
    {
        return _query;
    }

    /**
     * Set the existing Query
     * @param existingQuery the existing Query
     */
    public void setExistingQuery(QueryDobj existingQuery)
    {
        // validate
        Util.argCheckNull(existingQuery);

        // if splash is current, set existing
        if(getCurrentChild() instanceof QueryWizardSplashPanel)
            ((QueryWizardSplashPanel)getCurrentChild()).setExistingQuery(existingQuery);
    }

    /**
     * Set the parent dataview
     * @param parentDv the parent DataView
     */
    public void setParentDataView(DataView parentDv)
    {
        _parentDv = parentDv;
    }


    //--------------------------------------------------------------------------
    // wizard

    /**
     * Create and return the next wizard client panel
     */
    protected WslWizardChild getNextChild()
    {
        WslWizardChild cc = getCurrentChild();

        if (cc == null)
            return new QueryWizardSplashPanel();
        else if (cc instanceof QueryWizardSplashPanel)
        {
            // get the ds
            _query = ((QueryWizardSplashPanel)cc).getSelectedQuery();
            _isNew = ((QueryWizardSplashPanel)cc).isNewQuery();
            QueryPropPanel p = new QueryPropPanel();
            if(_parentDv != null && _isNew)
            {
                _query.setDataView(_parentDv);
                _query.setViewOrTableId(_parentDv.getId());
            }
            return bindPropertiesPanel(p, _query);
        }
        else if (cc instanceof QueryPropPanel)
            return new CriteriaPanel(_query);
        else if (cc instanceof CriteriaPanel)
            return new SortsPanel(_query);
        else if (cc instanceof SortsPanel)
            return new GroupByPanel(_query);
        else
            return null;
    }

    /**
     * Return true if the current panel is the last
     */
    protected boolean isLast()
    {
        return (getCurrentChild() instanceof GroupByPanel);
    }


    //--------------------------------------------------------------------------
    /**
     * Overload onCancel to unwind changes
     */
    protected void onCancel()
    {
        // revert
        if(_query != null && _query.isImaging())
            _query.revertToImage();
        super.onCancel();
    }

    //--------------------------------------------------------------------------
    // misc

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(600, 400);
    }

    /**
     * @return boolean true if the query being edited by the wizard is new
     */
    public boolean isNewQuery()
    {
        return _isNew;
    }
}
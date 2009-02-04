package wsl.mdn.admin;

// imports
import java.awt.Dimension;
import wsl.fw.resource.ResId;
import wsl.fw.util.Util;
import wsl.fw.gui.*;
import wsl.mdn.dataview.*;

//------------------------------------------------------------------------------
/**
 * Wizard for importing DataStores.
 */
public class DataStoreImportWizard extends WslWizardPanel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId WIZARD_TITLE  =
        new ResId("DataStoreImportWizard.Title");


    //--------------------------------------------------------------------------
    // attributes

    private DataSourceDobj _ds = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public DataStoreImportWizard()
    {
        super(WIZARD_TITLE.getText());
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return DataSourceDobj the datasource created or edited by this wizard
     */
    public DataSourceDobj getDataSource()
    {
        return _ds;
    }

    /**
     * Set the existing datasource
     * @param existingDs the existing datasource
     */
    public void setExistingDataSource(DataSourceDobj existingDs)
    {
        // validate
        Util.argCheckNull(existingDs);

        // if splash is current, set existing
        if(getCurrentChild() instanceof DataStoreWizardSplashPanel)
            ((DataStoreWizardSplashPanel)getCurrentChild()).setExistingDataSource(existingDs);
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
            return new DataStoreWizardSplashPanel();
        else if (cc instanceof DataStoreWizardSplashPanel)
        {
            // get the ds
            _ds = ((DataStoreWizardSplashPanel)cc).getSelectedDataSource();
            return bindPropertiesPanel(new JdbcPropPanel(), _ds);
        }
        else if (cc instanceof JdbcPropPanel)
            return new ImportTablesPanel(_ds);
        else if (cc instanceof ImportTablesPanel)
            return new RelationshipsPanel(_ds);
        else if (cc instanceof RelationshipsPanel)
            return new DataStoreWizardKeyHelpPanel();
        else
            return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Return true if the current panel is the last
     */
    protected boolean isLast()
    {
        return (getCurrentChild() instanceof DataStoreWizardKeyHelpPanel);
    }


    //--------------------------------------------------------------------------
    /**
     * Overload onCancel to unwind changes
     */
    protected void onCancel()
    {
        if (_ds != null)
        {
            // revert ds entities and joins to image
            _ds.revertEntitiesToImage();
            _ds.revertJoinsToImage();
        }
        // super
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
}
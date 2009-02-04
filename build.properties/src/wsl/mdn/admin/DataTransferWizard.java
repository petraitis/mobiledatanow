package wsl.mdn.admin;

// imports
import java.awt.Dimension;
import wsl.fw.resource.ResId;
import wsl.fw.util.Util;
import wsl.fw.gui.*;
import wsl.mdn.dataview.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class DataTransferWizard extends WslWizardPanel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId WIZARD_TITLE  =
        new ResId("DataTransferWizard.Title");


    //--------------------------------------------------------------------------
    // attributes

    private DataTransfer _dt = null;
    private QueryDobj _q = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public DataTransferWizard()
    {
        super(WIZARD_TITLE.getText());
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return DataTransfer the DataTransfer created or edited by this wizard
     */
    public DataTransfer getDataTransfer()
    {
        return _dt;
    }

    /**
     * Set the existing DataTransfer
     * @param existingDt the existing DataTransfer
     */
    public void setExistingDataTransfer(DataTransfer existingDt)
    {
        // validate
        Util.argCheckNull(existingDt);

        // if splash is current, set existing
        if(getCurrentChild() instanceof TransferWizardSplashPanel)
            ((TransferWizardSplashPanel)getCurrentChild()).setExistingDataTransfer(existingDt);
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
            return new TransferWizardSplashPanel();
        else if (cc instanceof TransferWizardSplashPanel)
        {
            // get the dt
            _dt = ((TransferWizardSplashPanel)cc).getSelectedDataTransfer();
            return bindPropertiesPanel(new DataTransferPropPanel(), _dt);
        }
        else if (cc instanceof DataTransferPropPanel)
            return new TransferEntitiesPanel(_dt);
        //else if (cc instanceof TransferEntitiesPanel)
        //    return new TransferFilterPanel(_dt);
        else
            return null;
    }

    /**
     * Return true if the current panel is the last
     */
    protected boolean isLast()
    {
        return (getCurrentChild() instanceof TransferEntitiesPanel);
    }


    //--------------------------------------------------------------------------
    /**
     * Overload onCancel to unwind changes
     */
    protected void onCancel()
    {
        // revert to image
        if(_dt != null && _dt.isImaging())
            _dt.revertToImage();

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
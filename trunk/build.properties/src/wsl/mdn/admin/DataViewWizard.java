package wsl.mdn.admin;

// imports
import java.awt.Dimension;
import javax.swing.JOptionPane;
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

public class DataViewWizard extends WslWizardPanel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId WIZARD_TITLE  =
        new ResId("DataViewWizard.Title");


    //--------------------------------------------------------------------------
    // attributes

    private DataView _dv = null;


    //--------------------------------------------------------------------------
    // construction

    /**
     * Default ctor
     */
    public DataViewWizard()
    {
        super(WIZARD_TITLE.getText());
    }


    //--------------------------------------------------------------------------
    // accessors

    /**
     * @return DataView the DataView created or edited by this wizard
     */
    public DataView getDataView()
    {
        return _dv;
    }

    /**
     * Set the existing DataView
     * @param existingDv the existing DataView
     */
    public void setExistingDataView(DataView existingDv)
    {
        // validate
        Util.argCheckNull(existingDv);

        // if splash is current, set existing
        if(getCurrentChild() instanceof DataViewWizardSplashPanel)
            ((DataViewWizardSplashPanel)getCurrentChild()).setExistingDataView(existingDv);
    }

    /**
     * Set the existing DataSourceDobj
     * @param dsDobj the existing DataSourceDobj
     */
    public void setExistingDataSourceDobj(DataSourceDobj dsDobj)
    {
        // validate
        Util.argCheckNull(dsDobj);

        // if splash is current, set existing
        if(getCurrentChild() instanceof DataViewWizardSplashPanel)
            ((DataViewWizardSplashPanel)getCurrentChild()).setExistingDataSourceDobj(dsDobj);
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
            return new DataViewWizardSplashPanel();
        else if (cc instanceof DataViewWizardSplashPanel)
        {
            // get the ds
            _dv = ((DataViewWizardSplashPanel)cc).getSelectedDataView();
            return bindPropertiesPanel(new DataViewPropPanel(), _dv);
        }
        else if (cc instanceof DataViewPropPanel)
            return new MapViewFieldsPanel(_dv);
        else
            return null;
    }

    //--------------------------------------------------------------------------
    /**
     * Override protected void onPrev() in superclass
     */
    protected void onPrev()
    {
        super.onPrev();
    }
    /**
     * Return true if the current panel is the last
     */
    protected boolean isLast()
    {
        return (getCurrentChild() instanceof MapViewFieldsPanel);
    }


    //--------------------------------------------------------------------------
    /**
     * Overload onCancel to unwind changes
     */
    protected void onCancel()
    {
        // revert view to image
        if(_dv != null && _dv.isFieldImaging())
            _dv.revertToImage();

        // super
        super.onCancel();
    }

    //--------------------------------------------------------------------------
    /**
     * This method is from super class WslWizardPanel.
     */
    protected void onFinish()
    {
        if (getCurrentChild() instanceof MapViewFieldsPanel)
        {
            boolean empty = ((MapViewFieldsPanel)getCurrentChild()).isTargetListEmpty();
            if (((MapViewFieldsPanel)getCurrentChild()).isTargetListEmpty())
            {
                //msg = MSG_NOT_FILLED.getText() + " " + msg;
                String msg = "You must have mapped fields to continue.";
                //JOptionPane.showMessageDialog(this, msg, MSG_TITLE.getText(), JOptionPane.ERROR_MESSAGE);
                JOptionPane.showMessageDialog(this, msg, "Mapped Fields Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        super.onFinish();
        return;
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
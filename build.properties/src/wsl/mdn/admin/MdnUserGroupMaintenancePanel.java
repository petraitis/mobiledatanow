package wsl.mdn.admin;

import java.awt.Dimension;
import wsl.fw.resource.ResId;
import wsl.fw.security.gui.UserGroupMaintenancePanel;
import wsl.mdn.common.MdnAdminConst;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c)
 * Company:
 * @author
 * @version 1.0
 */

public class MdnUserGroupMaintenancePanel extends UserGroupMaintenancePanel
{
    // resources
    public static final ResId PANEL_TITLE  = new ResId("MdnUserGroupMaintenancePanel.PanelTitle");

    public MdnUserGroupMaintenancePanel()
    {
        super();

        setPanelTitle(PANEL_TITLE.getText());

        DataViewPermissionsTabPane dvTabChild = new DataViewPermissionsTabPane(this);
        this.addTabPanel(dvTabChild);
    }

    /**
     * Preferred size
     */
    public Dimension getPreferredSize()
    {
        return MdnAdminConst.DEFAULT_PANEL_SIZE;
    }
}
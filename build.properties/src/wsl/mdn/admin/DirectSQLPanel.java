package wsl.mdn.admin;

// imports
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.fw.util.Log;
import wsl.fw.util.Util;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.Entity;
import wsl.fw.datasource.DataSourceException;
import wsl.fw.datasource.DataSource;
import wsl.fw.datasource.DataObject;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.mdn.dataview.*;
import wsl.fw.gui.PropertiesPanel;

//------------------------------------------------------------------------------
/**
 *
 */
public class DirectSQLPanel extends PropertiesPanel
{
    //--------------------------------------------------------------------------
    // resources
    private Dimension _dim = new Dimension(600, 400);

    public static final ResId LABEL_SQL_PANEL  =
        new ResId("DirectSQLPanel.label.SQLPanel");
    public static final ResId LABEL_NAME  =
        new ResId("DirectSQLPanel.label.Name");
    public static final ResId LABEL_SQL  =
        new ResId("DirectSQLPanel.label.SQL");

    public static final ResId ERR_DV_INVALID = new ResId("DirectSQLPanel.error.dvInvalid");
    public final static HelpId HID_DIRECTSQL = new HelpId("mdn.admin.DirectSQLPanel");

    //--------------------------------------------------------------------------
    // attributes

    private boolean _isValidSQL = false;


    //--------------------------------------------------------------------------
    // controls

    private WslTextField _name = new WslTextField();
    private WslTextArea _sqlSource = new WslTextArea();

    //--------------------------------------------------------------------------
    // construction

    /**
     * Blank ctor
     */
    public DirectSQLPanel()
    {
        // init controls
        initDirectSQLPanelControls();

        // update buttons
        updateButtons();
    }

    /**
     * DataSourceDobj ctor
     * @param ds the DatSourceDobj to import tables for
     */
    public DirectSQLPanel(DataSourceDobj ds)
    {
        setDataObject(ds);

        // init controls
        initDirectSQLPanelControls();

        // update buttons
        updateButtons();
    }

    /**
     * Init controls
     */
    private void initDirectSQLPanelControls()
    {
        // set layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        int y = 0;
        // entity name
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets.top = GuiConst.DEFAULT_INSET;
        gbc.insets.left = GuiConst.DEFAULT_INSET;
        gbc.insets.right = GuiConst.DEFAULT_INSET;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets.bottom = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        add(new JLabel(LABEL_NAME.getText()), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = y++;
        add(_name, gbc);
        addMandatory(LABEL_NAME.getText(), _name);

        // SQL source panel
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = y++;
        add(new JLabel(LABEL_SQL.getText()), gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets.bottom = GuiConst.DEFAULT_INSET;
        _sqlSource.setBorder(BorderFactory.createEtchedBorder());
        add(_sqlSource, gbc);
        addMandatory(LABEL_SQL.getText(), _sqlSource);
    }

    //--------------------------------------------------------------------------
    // misc

    public Dimension getPreferredSize()
    {
        return _dim;
    }

    /**
     * Returns the contents of the query text box
     * @return String
     */
    public String getQuery()
    {
        return _sqlSource.getText();
    }

    /**
     * Sets the contents of the query text box
     */
    public void setQuery(String query)
    {
        _sqlSource.setText(query);
    }

    /**
     * Returns the contents of the name text box
     * @return String
     */
    public String getName()
    {
        return _name.getText();
    }

    /**
     * Sets the contents of the name text box
     */
    public void setName(String name)
    {
        _name.setText(name);
    }

    /**
     * Transfer data between DataObject and controls
     * @param toDataObject
     * @return void
     * @exception
     * @roseuid 398FB791003E
     */
    public void transferData(boolean toDataObject)
    {
        DirectQueryDataView dv = getDataView();
        try
        {
            if (dv == null)
            {
                Log.error(ERR_DV_INVALID.getText());
                return;
            }

            if (toDataObject)
            {
                String sql = _sqlSource.getText();
                String name = _name.getText();
                if (sql.trim().length() > 0 && name.trim().length() > 0)
                {
                    dv.setName(name);
                    dv.setQuery(sql);
                }
            }
            else
            {

                _sqlSource.setText(dv.getQuery());
                _name.setText(dv.getName());
            }
        }
        catch (DataSourceException e)
        {
            GuiManager.showErrorDialog(getFrameParent(), "Error in SQL", e);
            throw new RuntimeException(e.toString());
        }
    }

    private DirectQueryDataView getDataView()
    {
        DataObject obj = getDataObject();
        if (obj != null && obj instanceof DirectQueryDataView)
            return (DirectQueryDataView)obj;
        else
            return null;
    }

    //--------------------------------------------------------------------------
    /**
     * If the subclass has help override this to specify the HelpId.
     * This help is displayed using the parent wizards's help button.
     * @return the HelpId of the help to display, if null the help button is not
     *   displayed.
     */
    public HelpId getHelpId()
    {
        return HID_DIRECTSQL;
    }
}

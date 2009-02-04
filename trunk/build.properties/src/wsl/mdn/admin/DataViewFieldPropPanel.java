/**	$Header: /wapsolutions/cvsroot/mdn/mdn/src/wsl/mdn/admin/DataViewFieldPropPanel.java,v 1.1.1.1 2002/06/11 23:35:35 jonc Exp $
 *
 * PropertiesPanel to edit DataViewFields.
 */
package wsl.mdn.admin;

import java.util.Vector;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

import pv.jfcx.JPVPassword;

import wsl.fw.gui.WslLabel;
import wsl.fw.gui.WslTextArea;
import wsl.fw.util.Util;
import wsl.fw.util.Log;
import wsl.fw.datasource.*;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.PropertiesPanel;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslComboBox;
import wsl.fw.resource.ResId;
import wsl.fw.help.HelpId;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.DataViewField;


public class DataViewFieldPropPanel
	extends PropertiesPanel
{
    //--------------------------------------------------------------------------
    // resources

    public static final ResId
		LABEL_NAME			= new ResId ("DataViewFieldPropPanel.label.Name"),
    	LABEL_DESCRIPTION	= new ResId ("DataViewFieldPropPanel.label.Description"),
    	LABEL_DISPLAY_NAME	= new ResId ("DataViewFieldPropPanel.label.DisplayName"),
		LABEL_ATTRIBUTES	= new ResId ("DataViewFieldPropPanel.label.Attributes"),
    	LABEL_NAMING_FIELD	= new ResId ("DataViewFieldPropPanel.label.NamingField"),
    	LABEL_PHONEDIAL_FIELD = new ResId ("DataViewFieldPropPanel.label.PhoneDialField"),
    	LABEL_LARGE_FIELD	= new ResId ("DataViewFieldPropPanel.label.LargeField"),
    	LABEL_SOURCE_ENTITY	= new ResId ("DataViewFieldPropPanel.label.SourceEntity"),
    	LABEL_SOURCE_FIELD	= new ResId ("DataViewFieldPropPanel.label.SourceField"),
    	LABEL_OPTION_LIST	= new ResId ("DataViewFieldPropPanel.label.OptionList");

    public final static HelpId HID_DATAVIEW_FIELD = new HelpId("mdn.admin.DataViewFieldPropPanel");

    //--------------------------------------------------------------------------
    // controls

    private WslTextField _txtName         = new WslTextField (250);
    private WslTextField _txtDescription  = new WslTextField (250);
    private WslTextField _txtDisplayName  = new WslTextField (250);
    private WslTextArea _txtOptionList    = new WslTextArea (50, 10);
    private JCheckBox
		_chkNamingField		= new JCheckBox (LABEL_NAMING_FIELD.getText ()),
		_chkPhonelinkField	= new JCheckBox (LABEL_PHONEDIAL_FIELD.getText ()),
		_chkLargeField		= new JCheckBox (LABEL_LARGE_FIELD.getText ());


    //--------------------------------------------------------------------------
    // attributes
    private transient boolean _isLoading  = false;
    private transient boolean _updateName = false;

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public DataViewFieldPropPanel()
    {
        // init controls
        initDataViewFieldPropPanelControls();
    }

    //--------------------------------------------------------------------------
    /**
     * Init the panel's controls.
     */
    private void
	initDataViewFieldPropPanelControls ()
    {
        // set layout
        setLayout (new GridBagLayout ());
		Insets stdInset = new Insets (5, 0, 0, 0);

        GridBagConstraints gbc = new GridBagConstraints ();
		gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = stdInset;

        // Name control
        WslLabel lbl = new WslLabel (LABEL_NAME.getText ());
		gbc.gridwidth = 1;
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));

		gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtName, gbc);
        _txtName.setEnabled (false);			// make read only

        // description
        lbl = new WslLabel (LABEL_DESCRIPTION.getText ());
		gbc.gridwidth = 1;
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));

		gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtDescription, gbc);

        // display name
        lbl = new WslLabel (LABEL_DISPLAY_NAME.getText ());
        gbc.gridwidth = 1;
        add (lbl, gbc);

		add (Box.createHorizontalStrut (10));

		gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_txtDisplayName, gbc);
        addMandatory (LABEL_DISPLAY_NAME.getText (), _txtDisplayName);

		// Check boxes
		lbl = new WslLabel (LABEL_ATTRIBUTES.getText ());
        gbc.gridwidth = 1;
		add (lbl, gbc);

		add (Box.createHorizontalStrut (10));

        // naming field
		gbc.gridwidth = 2;
        add (_chkNamingField, gbc);

		// phonelink
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_chkPhonelinkField, gbc);

		// empty label for continuation line
		lbl = new WslLabel ("");
        gbc.gridwidth = 1;
		gbc.insets = new Insets (0, 0, 0, 0);
		add (lbl, gbc);

		add (Box.createHorizontalStrut (10));

		// Large field
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (_chkLargeField, gbc);

        // option list
        lbl = new WslLabel (LABEL_OPTION_LIST.getText ());
		gbc.insets = stdInset;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add (lbl, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JScrollPane sp = new JScrollPane (_txtOptionList);
        add (sp, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     * Transfer data between the DataObject and panel controls.
     * @param toDataObject, determines the direction of the transfer.
     */
    public void transferData(boolean toDataObject)
    {
        // must have a DataObject
        DataViewField dobj = (DataViewField)getDataObject();
        Util.argCheckNull(dobj);

        if (toDataObject)
        {
            // to the DataObject
            // name
            dobj.setName(_txtName.getText());

            // description
            dobj.setDescription(_txtDescription.getText());

            // display name
            dobj.setDisplayName(_txtDisplayName.getText());

            // naming field
            if (_chkNamingField.getModel().isSelected())
                dobj.setFlags(dobj.getFlags() | Field.FF_NAMING);
            else
                dobj.setFlags(dobj.getFlags() & ~Field.FF_NAMING);

            // phonelink
            if (_chkPhonelinkField.getModel ().isSelected())
                dobj.setFlags (dobj.getFlags () | Field.FF_PHONELINK);
            else
                dobj.setFlags (dobj.getFlags() & ~Field.FF_PHONELINK);

            // phonelink
            if (_chkLargeField.getModel ().isSelected())
                dobj.setFlags (dobj.getFlags () | Field.FF_LARGE);
            else
                dobj.setFlags (dobj.getFlags() & ~Field.FF_LARGE);

            // option list
            dobj.setOptionList(_txtOptionList.getText());

        }
        else
        {
            // to the controls
            _txtName.setText(dobj.getName());
            _txtOptionList.setText(dobj.getOptionList());

            // description
            _txtDescription.setText(dobj.getDescription());

            // display name
            _txtDisplayName.setText(dobj.getDisplayName());

            _chkNamingField.getModel().setSelected(dobj.hasFlag(Field.FF_NAMING));
            _chkPhonelinkField.getModel ().setSelected (dobj.hasFlag (Field.FF_PHONELINK));
            _chkLargeField.getModel ().setSelected (dobj.hasFlag (Field.FF_LARGE));
        }
    }

    /**
     * Return the preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(430, 350);
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
        return HID_DATAVIEW_FIELD;
    }
}
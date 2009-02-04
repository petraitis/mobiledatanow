/**	$Id: CriteriaPanel.java,v 1.3 2003/06/12 23:26:53 tecris Exp $
 *
 *	Panel to specify query criteria, used in query wizard.
 *
 */
package wsl.mdn.admin;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wsl.fw.datasource.DataObject;
import wsl.fw.datasource.Field;
import wsl.fw.datasource.QueryCriterium;
import wsl.fw.gui.GuiConst;
import wsl.fw.gui.GuiManager;
import wsl.fw.gui.WslButton;
import wsl.fw.gui.WslButtonPanel;
import wsl.fw.gui.WslComboBox;
import wsl.fw.gui.WslList;
import wsl.fw.gui.WslTextArea;
import wsl.fw.gui.WslTextField;
import wsl.fw.gui.WslWizardChild;
import wsl.fw.help.HelpId;
import wsl.fw.resource.ResId;
import wsl.fw.util.Log;
import wsl.fw.util.Type;
import wsl.fw.util.TypeConversionException;
import wsl.fw.util.Util;
import wsl.mdn.common.MdnAdminConst;
import wsl.mdn.dataview.DataSourceDobj;
import wsl.mdn.dataview.DataViewField;
import wsl.mdn.dataview.EntityDobj;
import wsl.mdn.dataview.FieldDobj;
import wsl.mdn.dataview.QueryDobj;

public class CriteriaPanel
	extends WslWizardChild
    implements ActionListener, ListSelectionListener
{
	//--------------------------------------------------------------------------
	// resources

	public static final ResId
		LABEL_FIELD			= new ResId ("CriteriaPanel.label.Field"),
		LABEL_OPERATOR		= new ResId ("CriteriaPanel.label.Operator"),
		LABEL_VALUE			= new ResId ("CriteriaPanel.label.Value"),
		LABEL_USER_INPUT	= new ResId ("CriteriaPanel.label.UserInput"),
		LABEL_USER_ID		= new ResId ("CriteriaPanel.label.UserId"),
		LABEL_CRITERIA_LIST	= new ResId ("CriteriaPanel.label.CriteriaList"),
		BUTTON_ADD			= new ResId ("CriteriaPanel.btn.Add"),
		BUTTON_REMOVE		= new ResId ("CriteriaPanel.btn.Remove"),
		BUTTON_OK			= new ResId ("CriteriaPanel.btn.Ok"),
		BUTTON_CANCEL		= new ResId ("CriteriaPanel.btn.Cancel"),
		ENTER_VALUE_ERROR	= new ResId ("CriteriaPanel.error.EnterValue"),
		ERR_FIELDTYPE		= new ResId ("CriteriaPanel.error.fieldType"),
		ERR_EMPTY_BINARY	= new ResId ("CriteriaPanel.error.emptyBinary");

	// help
	public final static HelpId HID_CRITERIA = new HelpId ("mdn.admin.CriteriaPanel");

	//--------------------------------------------------------------------------
	// controls

	private WslComboBox _cmbField = new WslComboBox (150);
	private WslComboBox _cmbOperator = new WslComboBox (150);
	private WslTextField _txtValue = new WslTextField (150);
	private WslList _lstCriteria = new WslList ();
	private WslButton _btnAdd = new WslButton (BUTTON_ADD.getText (), this);
	private WslButton _btnRemove = new WslButton (BUTTON_REMOVE.getText (), this);
	private JRadioButton _rdoValue = new JRadioButton (LABEL_VALUE.getText ());
	private JRadioButton _rdoUserInput = new JRadioButton (LABEL_USER_INPUT.getText ());
	private JRadioButton _rdoUserId = new JRadioButton (LABEL_USER_ID.getText ());

	//--------------------------------------------------------------------------
	// attributes

	private QueryDobj _query;

	//--------------------------------------------------------------------------
	// construction

	/**
	 * Default constructor.
	 */
	public
	CriteriaPanel (
	 QueryDobj query)
	{
		// set attrib
		_query = query;
		_query.imageQuery ();

		// init controls
		initCriteriaPanelControls ();

		// build combos
		buildCombos ();

		// build criteria list
		buildCriteriaList ();

		// update buttons
		updateButtons ();
	}

	//--------------------------------------------------------------------------
	/**
	 * Init the panel's controls.
	 */
	private void
	initCriteriaPanelControls ()
	{
		// set layout
		setBorder (BorderFactory.createLoweredBevelBorder ());
		setLayout (new GridBagLayout ());
		GridBagConstraints gbc = new GridBagConstraints ();

		// field
		JLabel lbl = new JLabel (LABEL_FIELD.getText ());
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets.left = GuiConst.DEFAULT_INSET;
		gbc.insets.top = GuiConst.DEFAULT_INSET;
		gbc.insets.right = GuiConst.DEFAULT_INSET;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		add (lbl, gbc);
		gbc.gridy = 1;
		add (_cmbField, gbc);

		// operator
		lbl = new JLabel (LABEL_OPERATOR.getText ());
		gbc.gridwidth = 1;
		gbc.gridy = 2;
		add (lbl, gbc);
		gbc.gridy = 3;
		add (_cmbOperator, gbc);

		// value and user input radios
		ButtonGroup bg = new ButtonGroup ();
		bg.add (_rdoValue);
		bg.add (_rdoUserInput);
		bg.add (_rdoUserId);
		_rdoValue.setSelected (true);
		_rdoValue.addActionListener (this);
		_rdoUserInput.addActionListener (this);
		_rdoUserId.addActionListener (this);
		gbc.gridx = 1;
		gbc.gridy = 2;
		add (_rdoValue, gbc);
		gbc.gridx = 2;
		add (_rdoUserInput, gbc);
		gbc.gridx = 3;
		add (_rdoUserId, gbc);

		// value txt
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		add (_txtValue, gbc);

		// criteria list
		lbl = new JLabel (LABEL_CRITERIA_LIST.getText ());
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 4;
		add (lbl, gbc);
		gbc.gridy = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		_lstCriteria.addListSelectionListener (this);
		_lstCriteria.getScrollPane ().setBorder (BorderFactory.createLoweredBevelBorder ());
		add (_lstCriteria.getScrollPane (), gbc);

		// buttons
		gbc.weighty = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets.bottom = GuiConst.DEFAULT_INSET;
		add (_btnAdd, gbc);
		gbc.gridx = 1;
		add (_btnRemove, gbc);
	}

	//--------------------------------------------------------------------------
	/**
	 * Build list of criteria.
	 */
	private void
	buildCriteriaList ()
	{
		// validate
		Util.argCheckNull (_query);

		// get criteria from the query
		Vector criteria = _query.getCriteria (null);

		// build list
		_lstCriteria.buildFromVector (criteria);
	}

	//--------------------------------------------------------------------------
	/**
	 * Build combos.
	 */
	private void
	buildCombos ()
	{
		// validate
		Util.argCheckNull (_query);

		// build fields
		_cmbField.buildFromVector (_query.getDataView ().getFields ());
		if (_cmbField.getItemCount () > 0)
			_cmbField.setSelectedIndex (0);

		// build operators
		_cmbOperator.addItem (QueryCriterium.OP_EQUALS);
		_cmbOperator.addItem (QueryCriterium.OP_GREATER_THAN);
		_cmbOperator.addItem (QueryCriterium.OP_GREATER_THAN_EQUALS);
		_cmbOperator.addItem (QueryCriterium.OP_IS_NOT_NULL);
		_cmbOperator.addItem (QueryCriterium.OP_IS_NULL);
		_cmbOperator.addItem (QueryCriterium.OP_LESS_THAN);
		_cmbOperator.addItem (QueryCriterium.OP_LESS_THAN_EQUALS);
		_cmbOperator.addItem (QueryCriterium.OP_LIKE);
		_cmbOperator.addItem (QueryCriterium.OP_NOT_EQUALS);
		_cmbOperator.addItem (QueryCriterium.OP_NOT_LIKE);
		_cmbOperator.setSelectedIndex (0);
	}

	//--------------------------------------------------------------------------
	/**
	 * List clicked.
	 */
	public void
	valueChanged (
	 ListSelectionEvent ev)
	{
		updateButtons ();
	}

	//--------------------------------------------------------------------------
	/**
	 * handle actions.
	 */
	public void
	actionPerformed (
	 ActionEvent ev)
	{
		try
		{
			// switch on source
			if (ev.getSource () == _btnAdd)
				onAdd ();
			else if (ev.getSource () == _btnRemove)
				onRemove ();
			else if (ev.getSource () == _rdoValue)
				onRadioValue ();
			else if (ev.getSource () == _rdoUserInput)
				onRadioUserInput ();
			else if (ev.getSource () == _rdoUserId)
				onRadioUserId ();

			// update buttons
			updateButtons ();

		} catch (Exception e)
		{
			GuiManager.showErrorDialog (this, MdnAdminConst.ERR_UNHANDLED.getText (), e);
			Log.error (MdnAdminConst.ERR_UNHANDLED.getText (), e);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Add button clicked.
	 */
	private void
	onAdd ()
	{
		// validate
		Util.argCheckNull (_query);

		// create a QueryCriterium and set vals
		boolean isUserInput = _rdoUserInput.isSelected ();
		boolean isUserId = _rdoUserId.isSelected ();
		QueryCriterium qc = new QueryCriterium ();

		// entity / field
		qc._entityName = _query.getDataView ().getName ();
		qc._fieldName = ((DataViewField)_cmbField.getSelectedItem ()).getName ();

		// operator
		qc._op = _cmbOperator.getSelectedItem ().toString ();
		boolean isBinary = QueryCriterium.isBinary (qc._op);

		// value
		qc._value = isBinary? _txtValue.getText (): null;
		boolean isEmpty = Type.isValueEmpty (qc._value);
        boolean isFunction = qc.isFunction();
        

		// binary value checking
		if (isBinary && !isFunction && !isUserInput && !isUserId)
		{
			// mustnt be empty
			if (isEmpty)
			{
				GuiManager.showErrorDialog (getFrameParent (), ERR_EMPTY_BINARY.getText (), null);
				return;

			} else
			{
				// get the field type
				int fieldType = -1;
				try
				{
					// get data view field and use it to get the source field
					DataViewField dvf = (DataViewField) _cmbField.getSelectedItem ();
					DataSourceDobj dsDobj    = _query.getDataView ().getSourceDataSource ();
					EntityDobj     entDobj   = dsDobj.getEntity (dvf.getSourceEntity ());
					FieldDobj      fieldDobj = entDobj.getField (dvf.getSourceField ());

					// get field type
					fieldType = fieldDobj.getType ();

				} catch (Exception e)
				{
					// failed, show error.
					GuiManager.showErrorDialog (getFrameParent (), ERR_FIELDTYPE.getText (), e);
					return;
				}

				// test the conversion and display error if invalid
				try
				{
					Type.convertValueOnType (qc._value, fieldType);

				} catch (TypeConversionException e)
				{
					GuiManager.showErrorDialog (getFrameParent (), e.getMessage (), null);
					return;
				}
			}
		}

		// check than none of the QeryDobj's special delimeter chars have been
		// used as this will mess up QueryDobj parsing
		String val = (String)qc._value;
		if (val != null && (val.indexOf (QueryDobj.DELIM_AND) > 0
			|| val.indexOf (QueryDobj.DELIM_OPS) > 0))
		{
			JOptionPane.showMessageDialog (getFrameParent (),
				ENTER_VALUE_ERROR.getText ());
			return;
		}

		try
		{
			// add to list
			_lstCriteria.getDefaultModel ().addElement (qc);

			// add to query
			_query.addCriterium (qc);

		} catch (Exception e)
		{
			throw new RuntimeException (e.toString ());
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Remove button clicked
	 */
	private void
	onRemove ()
	{
		// must be selected
		if (_lstCriteria.getSelectedIndex () >= 0)
		{
			// remove from list
			QueryCriterium qc = (QueryCriterium)_lstCriteria.getDefaultModel ().remove (
				_lstCriteria.getSelectedIndex ());

			// remove from query
			_query.removeCriterium (qc, true);
		}
	}

	//--------------------------------------------------------------------------
	/**
	 * Radio Value clicked
	 */
	private void
	onRadioValue ()
	{
		// clear the text values
		_txtValue.setText ("");
	}

	//--------------------------------------------------------------------------
	/**
	 * Radio User Input clicked
	 */
	private void
	onRadioUserInput ()
	{
		// set special user input char
		_txtValue.setText (QueryCriterium.INCOMPLETE_VALUE);
	}

	//--------------------------------------------------------------------------
	/**
	 * Radio User Id clicked
	 */
	private void
	onRadioUserId ()
	{
		// set special user input char
		_txtValue.setText (QueryCriterium.USERID_VALUE);
	}

	//--------------------------------------------------------------------------
	/**
	 * @return true if the finish button is to be enabled.
	 */
	public boolean
	canFinish ()
	{
		return true;
	}

	//--------------------------------------------------------------------------
	/**
	 * update controls
	 */
	public void
	updateButtons ()
	{
		// flags
		boolean isSel = _lstCriteria.getSelectedIndex () >= 0;
		boolean isValue = _rdoValue.isSelected ();

		// enable
		_btnRemove.setEnabled (isSel);
		_txtValue.setEnabled (isValue);
	}

	//--------------------------------------------------------------------------
	/**
	 * Return the preferred size
	 */
	public Dimension
	getPreferredSize ()
	{
		return new Dimension (550, 350);
	}

	//--------------------------------------------------------------------------
	/**
	 * If the subclass has help override this to specify the HelpId.
	 * This help is displayed using the parent wizards's help button.
	 * @return the HelpId of the help to display, if null the help button is not
	 *   displayed.
	 */
	public HelpId
	getHelpId ()
	{
		return HID_CRITERIA;
	}
}

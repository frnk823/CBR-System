/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * This class contains functions for graphical interactions regarding searches
 *
 * @author Lars Johanson
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class GUISearch extends JDialog
{
	/**
	 * Scroll pane
	 */
	private JScrollPane scrollPane;
	/**
	 * The table
	 */
	private JTable table;
	/**
	 * The table model used by the table
	 */
	private CBRSearchTableModel searchModel;
	/**
	 * Column names
	 */
	static final private String[] COLUMN_NAMES = {"特征", "数据类型", "权重", "模式", "反向计算", "逻辑关系", "值"};
	/**
	 * Valid values for search options. Corresponds to CBR.SEARCH_XXXX
	 */
	 static final private String[] SCALE_NAMES = {"Fuzzy linear", "Fuzzy logarithmic", "Flat", "Strict"};
	 static final private String[] TERM_NAMES = {"=", "!=", ">=", ">", "<=", "<", "Max", "Min"};
	
	/**
	* Constructor that initiates the window
	* 
	* @param parent the owner of this window
	* @param cbr the CBR to search
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public GUISearch(Frame parent, CBR cbr)
	{
		super(parent, false);	// Would really like it to be modal, but then the result dialog behaves strange
		
		final Frame window = parent;
		
		JComboBox comboBox;
		JCheckBox checkBox;
		TableColumn column;
		Component comp;
		int maxWidth;
		
		// Create the table
		searchModel = new CBRSearchTableModel(cbr, COLUMN_NAMES, SCALE_NAMES, TERM_NAMES);
		table = new JTable(searchModel);
		
		// No auto resize
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// Set column width. Pretty heavy...
		if (cbr != null)
		{
			int col;
			
			// First column - feature name
			col = 0;
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
			for (int row = 0; row < cbr.getNumFeatures(); row++)
			{
				comp = table.getDefaultRenderer(searchModel.getColumnClass(col)).getTableCellRendererComponent(
						table, searchModel.getValueAt(row, col), 
						false, false, row, col);
				maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			column.setPreferredWidth(maxWidth + 10);
			
			// Second column - feature type
			col = 1;
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
			for (int row = 0; row < cbr.getNumFeatures(); row++)
			{
				comp = table.getDefaultRenderer(searchModel.getColumnClass(col)).getTableCellRendererComponent(
						table, searchModel.getValueAt(row, col), 
						false, false, row, col);
				maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			column.setPreferredWidth(maxWidth + 10);
			
			// Third column - weight
			col = 2;
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			column.setPreferredWidth(comp.getPreferredSize().width + 10);
			
			// Fourth column - scale
			col = 3;
			comboBox = new JComboBox();
			for (int i = 0; i < SCALE_NAMES.length; i++)
			{
				comboBox.addItem(SCALE_NAMES[i]);
			}
			table.getColumn(COLUMN_NAMES[col]).setCellEditor(new DefaultCellEditor(comboBox));
			
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, col);
			maxWidth = comp.getPreferredSize().width;
			for (int i = 0; i < SCALE_NAMES.length; i++)
			{
				comp = table.getDefaultRenderer(searchModel.getColumnClass(col)).getTableCellRendererComponent(
						table, SCALE_NAMES[i], 
						false, false, 0, 0);
				maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			column.setPreferredWidth(maxWidth + 10);
			
			// Fifth column - options
			col = 4;
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
			column.setPreferredWidth(maxWidth + 10);
			
			// Sixth column - term
			col = 5;
			comboBox = new JComboBox();
			for (int i = 0; i < TERM_NAMES.length; i++)
			{
				comboBox.addItem(TERM_NAMES[i]);
			}
			table.getColumn(COLUMN_NAMES[col]).setCellEditor(new DefaultCellEditor(comboBox));
			
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
			for (int i = 0; i < TERM_NAMES.length; i++)
			{
				comp = table.getDefaultRenderer(searchModel.getColumnClass(col)).getTableCellRendererComponent(
						table, TERM_NAMES[i], 
						false, false, 0, col);
				maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			column.setPreferredWidth(maxWidth + 10);
			
			// Seventh column - search value
			col = 6;
			column = table.getColumnModel().getColumn(col);
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
			comp = table.getDefaultRenderer(searchModel.getColumnClass(col)).getTableCellRendererComponent(
					table, "This is a test string with appropriate length", 
					false, false, 0, col);
			maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			column.setPreferredWidth(maxWidth + 10);
		}
		// No column reordering allowed
		table.getTableHeader().setReorderingAllowed(false);
		// Set window size
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		// Create the scroll pane and add the table to it. 
		JScrollPane scrollPane = new JScrollPane(table);
		
		// Create the window content
		String msgString1 = "输入需要检索案例的特征值及相应条件";
		Object[] array = {msgString1, scrollPane};
		final JOptionPane optionPane = new JOptionPane(array, 
						JOptionPane.PLAIN_MESSAGE, 
						JOptionPane.OK_CANCEL_OPTION); 
		setContentPane(optionPane);
		optionPane.addPropertyChangeListener(new java.beans.PropertyChangeListener()
		{
		    public void propertyChange(java.beans.PropertyChangeEvent e)
		    {
			String prop = e.getPropertyName();
			
			if (isVisible() 
			 && (e.getSource() == optionPane)
			 && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
			     prop.equals(JOptionPane.INPUT_VALUE_PROPERTY)))
			{
			    Object value = optionPane.getValue();
	
			    if (value == JOptionPane.UNINITIALIZED_VALUE)
			    {
				//ignore reset
				return;
			    }
	
			    // Reset the JOptionPane's value.
			    // If you don't do this, then if the user
			    // presses the same button next time, no
			    // property change event will be fired.
			    optionPane.setValue(
				    JOptionPane.UNINITIALIZED_VALUE);
	
			    if (value.equals(new Integer(JOptionPane.OK_OPTION)))
			    {
				int weights[] = new int[searchModel.getRowCount()];
				String featureNames[] = new String[searchModel.getRowCount()];
				int options[] = new int[searchModel.getRowCount()];
				
				for (int i = 0; i < searchModel.getRowCount(); i++)
				{
					weights[i] = searchModel.searchWeights[i].intValue();
					featureNames[i] = (String) searchModel.getValueAt(i, 0);
					options[i] = searchModel.searchOptions[i] ? CBR.SEARCH_OPTION_INVERTED : 0;
				}
				GUIResult resultDialog = new GUIResult(window, 
						searchModel.cbr, 
						weights, 
						searchModel.searchScales, 
						options, 
						searchModel.searchTerms,
						featureNames, 
						searchModel.searchValues);
				resultDialog.pack();
				resultDialog.setVisible(true);
			    } else
			    { // user closed dialog or clicked cancel
				setVisible(false);
			    }
			}
		    }
		});
		
		this.setTitle("案例检索");

		// Listen for window closing
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					setVisible(false);
				}
			});
	}
}


/**
 * Class that defines the data model
 */
class CBRSearchTableModel extends AbstractTableModel
{
	/**
	 * Default weight
	 */
	static final private int DEFAULT_WEIGHT = 5; 
	/**
	 * Search option values
	 */
	public Integer searchWeights[];
	public int searchScales[];
	public boolean searchOptions[];
	public int searchTerms[];
	public String searchValues[];
	/**
	 * Names of the options, corresponds to CBR.SEARCH_xxx_yyy
	 */
	private String scaleNames[];
	private String termNames[];
	/**
	 * CBR that stores the case data
	 */
	public CBR cbr;
	/**
	 * The column names
	 */
	private String columnNames[];
	
	
	/**
	* Constructor that initiates the CBRSearchTableModel
	* 
	* @param cbr the CBR data to use in the table
	* @param columnNames the names to use for the columns
	* @param scaleNames the names to use for the scales, must correspond to CBR.SEARCH_SCALE_xxx
	* @param optionNames the names to use for the options, must correspond to CBR.SEARCH_OPTION_xxx
	* @param termNames the names to use for the terms, must correspond to CBR.SEARCH_TYPE_xxx
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	CBRSearchTableModel(CBR cbr, 
			String columnNames[],
			String scaleNames[], 
			String termNames[])
	{
		int numFeatures = cbr.getNumFeatures();
		
		this.cbr = cbr;
		this.columnNames = columnNames;
		this.scaleNames	= scaleNames;
		this.termNames = termNames;
		
		searchWeights = new Integer[numFeatures];
		java.util.Arrays.fill(searchWeights, new Integer(DEFAULT_WEIGHT));
		searchScales = new int[numFeatures];
		java.util.Arrays.fill(searchScales, 0);
		searchOptions = new boolean[numFeatures];
		java.util.Arrays.fill(searchOptions, false);
		searchTerms = new int[numFeatures];
		java.util.Arrays.fill(searchTerms, 0);
		searchValues = new String[numFeatures];
		java.util.Arrays.fill(searchValues, Feature.FEATURE_VALUE_UNDEFINED);
	}
	
	
	/**
	* Updates the table
	* 
	* @return true if the table is read only, otherwise false
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void updateTable()
	{
		this.fireTableStructureChanged();
	}
	
	
	/**
	* Returns the number of columns in the table
	* 
	* @return the number of columns in the table
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int getColumnCount()
	{
		return 7;
	}
	
	
	/**
	* Returns the number of rows in the table
	* 
	* @return the number of rows
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int getRowCount()
	{
		try
		{
			return cbr.getNumFeatures();
		} catch (Exception e)
		{
			return 0;
		}
	}
	
	
	/**
	* Returns the name of the specified column
	* 
	* @param col the column number
	* @return the name of the column
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
	
	
	/**
	* Returns the value of the specified cell
	* 
	* @param row the row number
	* @param col the column number
	* @return the contents of the specified cell
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Object getValueAt(int row, int col)
	{
		switch (col)
		{
		case 0:
			return cbr.getFeatureName(row);
		case 1:
			return Feature.typeToString(cbr.getFeatureType(row));
		case 2:
			return searchWeights[row];
		case 3:
			return this.scaleNames[searchScales[row]];
		case 4:
			return new Boolean(searchOptions[row]);
		case 5:
			return this.termNames[searchTerms[row]];
		case 6:
			return searchValues[row];
		default:
			return "";
		}
	}
	
	
	/**
	* Sets the value of the specified cell
	* 
	* @param obj the value to set
	* @param row the row number
	* @param col the column number
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setValueAt(Object obj, int row, int col)
	{
		switch (col)
		{
		case 0:	// Feature name
			break;
		case 1:	// Feature type
			break;
		case 2:	// Weight
			try
			{
				searchWeights[row] = (Integer) obj;
				if (searchWeights[row].intValue() < 0)
				{
					searchWeights[row] = new Integer(DEFAULT_WEIGHT);
				}
			} catch (Exception e)
			{
				searchWeights[row] = new Integer(DEFAULT_WEIGHT);
			}
			break;
		case 3:	// Scale
			for (int i = 0; i < scaleNames.length; i++)
			{
				if (scaleNames[i].equals((String) obj))
				{
					searchScales[row] = i;
					break;
				}
			}
			break;
		case 4:	// Option
			searchOptions[row] = ((Boolean) obj).booleanValue();
			break;
		case 5:	// Term
			for (int i = 0; i < termNames.length; i++)
			{
				if (termNames[i].equals((String) obj))
				{
					searchTerms[row] = i;
					break;
				}
			}
			break;
		case 6:	// Value
			if (((String) obj).equals(Feature.FEATURE_VALUE_UNDEFINED))
			{
				searchValues[row] = Feature.FEATURE_VALUE_UNDEFINED;
			} else
			{
				switch (cbr.getFeatureType(row))
				{
				case Feature.FEATURE_TYPE_STRING:
					searchValues[row] = (String) obj;
					break;
				case Feature.FEATURE_TYPE_MULTISTRING:
					searchValues[row] = (String) obj;
					break;
				case Feature.FEATURE_TYPE_INT:
					try
					{
						searchValues[row] = Integer.valueOf((String) obj).toString();
					} catch (Exception e)
					{
					}
					break;
				case Feature.FEATURE_TYPE_FLOAT:
					try
					{
						searchValues[row] = Double.valueOf((String) obj).toString();
					} catch (Exception e)
					{
					}
					break;
				case Feature.FEATURE_TYPE_BOOL:
					try
					{
						searchValues[row] = Boolean.valueOf((String) obj).toString();
					} catch (Exception e)
					{
					}
					break;
				default:
					break;
				}
				break;
			}
			default:
				break;
		}
	}
 	
	
	/**
	* Returns the class (type) of the specified column
	* 
	* @param col the column number
	* @return the class
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Class getColumnClass(int col)
	{
		switch (col)
		{
		case 0:
			return String.class;	// Feature name
		case 1:
			return String.class;	// Feature type
		case 2:
			return Integer.class;	// Search weight
		case 3:
			return String.class;	// Search scale
		case 4:
			return Boolean.class;	// Search option
		case 5:
			return String.class;	// Search term
		case 6:
			return String.class;	// Search value
		default:
			return String.class;
		}
	}
	
	
	/**
	* Returns true if the cell at rowIndex and columnIndex is editable. 
	*		Otherwise, setValueAt on the cell will not change the value 
	*		of that cell
	* 
	* @param row the row whose value to be queried
	* @param col the column whose value to be queried
	* @return true if the cell is editable
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean isCellEditable(int row, int col)
	{
		switch (col)
		{
		case 0:
			return false;	// Feature name
		case 1:
			return false;	// Feature type
		case 2:
			return true;	// Search weight
		case 3:
			return true;	// Search scale
		case 4:
			return true;	// Search distance
		case 5:
			return true;	// Search term
		case 6:
			return true;	// Search value
		default:
			return true;
		}
	}
}


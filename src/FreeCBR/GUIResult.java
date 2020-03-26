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
 * This class contains functions for graphical interactions 
 *	(startpoint for a GUI application)
 *
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class GUIResult extends JDialog
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
	private CBRResultTableModel resultModel;
	
	/**
	* Constructor that initiates the window
	* 
	* @param parent the owner of this window
	* @param cbr the CBR to search
	* @param weights array of the weights to use for the search
	* @param scales array of scales to use for the search
	* @param distances array of distances to use for the search
	* @param terms array of search terms
	* @param names array of the names of the values to search for
	* @param values array of the values to search for
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public GUIResult(Frame parent, CBR cbr, int weights[], int scales[], int distances[], int terms[], String names[], String values[])
	{
		super(parent, "检索结果");
		
		JComboBox comboBox;
		TableColumn column;
		Component comp;
		int maxWidth;
		
		// Create the table
		resultModel = new CBRResultTableModel(cbr, weights, scales, distances, terms, names, values);
		table = new JTable(resultModel);
		
		// No auto resize
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// Set column width
		for (int col = 0; col < resultModel.getColumnCount(); col++)
		{
			column = table.getColumnModel().getColumn(col);
			
			comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
					null, column.getHeaderValue(), 
					false, false, 0, 0);
			maxWidth = comp.getPreferredSize().width;
				
			for (int row = 0; row < resultModel.getRowCount(); row++)
			{
				comp = table.getDefaultRenderer(resultModel.getColumnClass(row)).getTableCellRendererComponent(
						table, resultModel.getValueAt(row, col), 
						false, false, row, col);
				maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			column.setPreferredWidth(maxWidth + 10);
		}
		// Set window size
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		// Create the scroll pane and add the table to it. 
		JScrollPane scrollPane = new JScrollPane(table);
		
		// Create the window content
		final JOptionPane optionPane = new JOptionPane(scrollPane, 
						JOptionPane.PLAIN_MESSAGE, 
						JOptionPane.DEFAULT_OPTION);
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
				setVisible(false);
			}
		    }
		});
		
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
class CBRResultTableModel extends AbstractTableModel
{
	/**
	 * CBR that stores the case data
	 */
	private CBR cbr;
	/**
	 * The result of the search
	 */
	private CBRResult searchResult[];
	
	
	/**
	* Constructor that initiates the CBRresultTableModel
	* 
	* @param cbr the CBR data to use in the table
	* @param weights the weights to use for the search
	* @param scales the scales to use for the search
	* @param distances the distances to use for the search
	* @param terms the terms to use for the search
	* @param names the names of the values to search for
	* @param values the values to search for
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	CBRResultTableModel(CBR cbr, 
			int weights[],
			int scales[], 
			int distances[],
			int terms[], 
			String names[], 
			String values[])
	{
		this.cbr = cbr;
		
		searchResult = cbr.search(names, values, weights, terms, scales, distances);
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
		if (cbr == null)
			return 0;
		return cbr.getNumFeatures() + 1;
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
		return searchResult.length;
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
		if (cbr == null)
			return null;
		if (col == 0)
			return "匹配率 %";
		else
			return cbr.getFeatureName(col-1);
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
		if (col == 0)
			return "" + searchResult[row].matchPercent;
		else
			return cbr.getFeatureValue(searchResult[row].caseNum, col - 1).toString();
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
		return String.class;
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
		return false;
	}
}


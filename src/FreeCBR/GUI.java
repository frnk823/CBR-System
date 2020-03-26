/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import FreeCBR.*;

/**
 * This class contains functions for graphical interactions 
 *	(startpoint for the GUI application)
 *
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
public class GUI extends JFrame implements ActionListener, ItemListener
{
	/**
	 * true if there is no file associated with the case set - the case set is created
	 */
	private boolean newSet;
	/**
	 * Current data set. If null then no data set is in memory
	 */
	private CBR cbr=null;
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
	private CBRTableModel cbrModel;
	/**
	 * Path and name of the icon to use
	 */
	private static final String ICON_PATH = "/images/FreeCBR_icon.gif";
	/**
	 * Verbose flag specified at start up
	 */
	private boolean startVerbose = false;
	/**
	 * Silence flag specified at start up
	 */
	private boolean startSilent = false;
	/**
	 * Log file specified at startup
	 */
	private String startLogfile = null;
	
	/**
	* Constructor that initiates the GUI
	* 
	* @param filename the file to read the case set from
	* @param logfile the file to use for logging, may be null
	* @param verbose set to true if extra log information is needed
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public GUI(String filename, String logfile, boolean verbose, boolean silent)
	{
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		JCheckBoxMenuItem cbMenuItem;
		
		startLogfile = logfile;
		startVerbose = verbose;
		startSilent = silent;
		
		// Create the table
		cbrModel = new CBRTableModel();
		table = new JTable(cbrModel);
		
		// Set icon
		try
		{
			setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(ICON_PATH)));
		} catch (Exception e)
		{
			// On error use the default icon
		}
		
		// Try to read file specified (if any)
		if (filename == null)
		{
			cbr = null;
			try
			{
				CBR newCBR = new CBR(startLogfile, startVerbose, startSilent);
				setCBR(newCBR);
				newSet = true;
			} catch (Exception e)
			{
				e.printStackTrace();
				cbr = null;
			}
		} else
		{
			try
			{
				cbr = new CBR(filename, startLogfile, startVerbose, startSilent);
				newSet = false;
				setCBR(cbr);
			} catch (Exception e)
			{
				cbr = null;
				newSet = true;
				JOptionPane.showMessageDialog(this, "无法打开文件 \"" + filename + "\". 文件不存在或易损坏.\n提示: 用命令行启动 \"/log:logfile\" 以获得更多信息.");
			}
		}
		
		// No auto resize
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
///		// No column reordering allowed
///		table.getTableHeader().setReorderingAllowed(false);
		// Set window size
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		this.setSize(Math.max(400, table.getPreferredSize().width + 15), Math.max(300, table.getPreferredSize().height + 80));
		
		// Create the scroll pane and add the table to it. 
		JScrollPane scrollPane = new JScrollPane(table);
		
		// Add the scroll pane to this window.
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// Create the menu bar.
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// File menu
		menu = new JMenu("文件");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("文件选项");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("新建", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription("新建数据集");
		menuItem.setActionCommand("FileNew");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("打开", KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription("打开已存在数据集");
		menuItem.setActionCommand("FileOpen");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("保存", KeyEvent.VK_S);
		menuItem.getAccessibleContext().setAccessibleDescription("Save current set");
		menuItem.setActionCommand("FileSave");
		menuItem.addActionListener(this);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menuItem = new JMenuItem("另存为...", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("Save current set in specified file");
		menuItem.setActionCommand("FileSave_as");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menu.addSeparator();
		
		cbMenuItem = new JCheckBoxMenuItem("只读模式", false);
		cbMenuItem.setMnemonic(KeyEvent.VK_M);
		cbMenuItem.getAccessibleContext().setAccessibleDescription("Allow modifications in values");
		cbMenuItem.setActionCommand("FileRead_only");
		cbMenuItem.addItemListener(this);
		menu.add(cbMenuItem);
		
		menu.addSeparator();
		
		menuItem = new JMenuItem("退出", KeyEvent.VK_X);
		menuItem.getAccessibleContext().setAccessibleDescription("Exit application");
		menuItem.setActionCommand("FileExit");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		// Case menu
		menu = new JMenu("案例");
		menu.setMnemonic(KeyEvent.VK_C);
		menu.getAccessibleContext().setAccessibleDescription("案例选项");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("新增", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("在案例库中增加案例");
		menuItem.setActionCommand("CaseAdd");
		menuItem.addActionListener(this);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menuItem = new JMenuItem("删除", KeyEvent.VK_D);
		menuItem.getAccessibleContext().setAccessibleDescription("Delete case from case set");
		menuItem.setActionCommand("CaseDelete");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("上移");
		menuItem.getAccessibleContext().setAccessibleDescription("Move case upwards");
		menuItem.setActionCommand("CaseMove_up");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("下移");
		menuItem.getAccessibleContext().setAccessibleDescription("Move case downwards");
		menuItem.setActionCommand("CaseMove_down");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("案例检索", KeyEvent.VK_F);
		menuItem.getAccessibleContext().setAccessibleDescription("Find best case");
		menuItem.setActionCommand("CaseFind");
		menuItem.addActionListener(this);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		// Feature menu
		menu = new JMenu("特征");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription("Feature menu");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("新增", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("Add new feature");
		menuItem.setActionCommand("FeatureAdd");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("删除", KeyEvent.VK_D);
		menuItem.getAccessibleContext().setAccessibleDescription("Delete selected feature");
		menuItem.setActionCommand("FeatureDelete");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("修改数据类型", KeyEvent.VK_T);
		menuItem.getAccessibleContext().setAccessibleDescription("Change type of selected feature");
		menuItem.setActionCommand("FeatureChange_type");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("修改特征名称", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription("Change name of selected feature");
		menuItem.setActionCommand("FeatureChange_name");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		// Help menu
		menu = new JMenu("帮助");
		menu.setMnemonic(KeyEvent.VK_H);
		menu.getAccessibleContext().setAccessibleDescription("Show help");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("关于", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("Get information about the application");
		menuItem.setActionCommand("HelpAbout");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		// Listen for window closing
		super.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					if (saveQuery())
					{
						System.exit(0);
					}
				}
			});
	}
	
	
	/**
	* Function called when a normal menu button is pressed
	* 
	* @param ae event that caused the action
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void actionPerformed(ActionEvent ae)
	{
		final JFileChooser fc = new JFileChooser();
		CBR newCBR;
		GUISearch searchDialog;
		String typeChoice[] = 
		{
			Feature.typeToString(Feature.FEATURE_TYPE_STRING), 
			Feature.typeToString(Feature.FEATURE_TYPE_FLOAT), 
			Feature.typeToString(Feature.FEATURE_TYPE_INT), 
			Feature.typeToString(Feature.FEATURE_TYPE_MULTISTRING), 
			Feature.typeToString(Feature.FEATURE_TYPE_BOOL)
		};
		
		if (ae.getActionCommand().equals("FileNew"))
		{
			//
			// Create new case set
			//
			if (saveQuery())
			{
				try
				{
					if (cbr == null)
					{
						newCBR = new CBR(startLogfile, startVerbose, startSilent);
					} else
					{
						newCBR = new CBR(cbr.getLogfile(), cbr.getVerbose(), cbr.getSilent());
					}
					setCBR(newCBR);
					newSet = true;
				} catch (Exception e)
				{
					e.printStackTrace();///
					JOptionPane.showMessageDialog(this, "无法创建新的数据集.");
				}
			}
		} else if (ae.getActionCommand().equals("FileOpen"))
		{
			//
			// Open existing case set
			//
			if (saveQuery())
			{
				if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
				{
					return;
				}
				try
				{
					if (cbr == null)
					{
						newCBR = new CBR(fc.getSelectedFile().toString(), startLogfile, startVerbose, startSilent);
					} else
					{
						newCBR = new CBR(fc.getSelectedFile().toString(), cbr.getLogfile(), cbr.getVerbose(), cbr.getSilent());
					}
					setCBR(newCBR);
					newSet = true;
				} catch (Exception e)
				{
					JOptionPane.showMessageDialog(this, "无法打开CBR文件 \"" + fc.getSelectedFile().toString() + "\".");
				}
			}
		} else if (ae.getActionCommand().equals("FileSave"))
		{
			//
			// Save current case set
			//
			if (this.saveSet(newSet))
			{
				newSet = false;
			}
		} else if (ae.getActionCommand().equals("FileSave_as"))
		{
			//
			// Save current case set in new file
			//
			if (saveSet(true))
			{
				newSet = false;
			}
		} else if (ae.getActionCommand().equals("FileExit"))
		{
			//
			// Exit application
			//
			if (saveQuery())
			{
				System.exit(0);
			}
		} else if (ae.getActionCommand().equals("CaseAdd"))
		{
			//
			// Add new case to the set
			//
			if (cbr != null)
			{
				Feature feats[] = new Feature[cbr.getNumFeatures()];
				for (int i = 0; i < cbr.getNumFeatures(); i++)
				{
					feats[i] = new Feature(null, cbr.getFeatureType(i));
				}
				cbr.addCase(feats);
				cbrModel.setDirty(true);
				setCBR(null);
			}
		} else if (ae.getActionCommand().equals("CaseDelete"))
		{
			//
			// Delete currently selected case from set
			//
			int selRow = table.getSelectedRow();
			int ret;
			
			if (selRow == -1)
			{
				JOptionPane.showMessageDialog(this, "Please choose the row to delete first.");
			} else
			{
				ret = JOptionPane.showConfirmDialog(this, 
						"是否删除所选案例 " + (selRow + 1) + "?",
						"删除确认",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION)
				{
					cbr.removeCase(selRow);
					cbrModel.setDirty(true);
					setCBR(null);
				}
			}
		} else if (ae.getActionCommand().equals("CaseMove_up"))
		{
			//
			// Move currently selected case one step up
			//
			int selRow = table.getSelectedRow();
			Feature feats[];
			
			if (selRow < 1)
			{
				JOptionPane.showMessageDialog(this, "Please choose the row to move upwards.");
			} else
			{
				feats = cbr.getCase(selRow);
				feats = cbr.editCase(selRow - 1, feats);
				cbr.editCase(selRow, feats);
				cbrModel.setDirty(true);
			}
		} else if (ae.getActionCommand().equals("CaseMove_down"))
		{
			//
			// Move currently selected case one step down
			//
			int selRow = table.getSelectedRow();
			Feature feats[];
			
			if (selRow < 0 || selRow >= cbrModel.getRowCount() - 1)
			{
				JOptionPane.showMessageDialog(this, "Please choose the row to move upwards.");
			} else
			{
				feats = cbr.getCase(selRow);
				feats = cbr.editCase(selRow + 1, feats);
				cbr.editCase(selRow, feats);
				cbrModel.setDirty(true);
			}
		} else if (ae.getActionCommand().equals("CaseFind"))
		{
			//
			// Find best case
			//
			searchDialog = new GUISearch(this, cbr);
			searchDialog.pack();
			searchDialog.setVisible(true);
		} else if (ae.getActionCommand().equals("FeatureAdd"))
		{
			//
			// Add new feature
			//
			String name;
			String type;
			
			name = JOptionPane.showInputDialog(this, "请输入特征名");
			if (name != null && !name.equals(""))
			{
				type = (String) JOptionPane.showInputDialog(this, 
						"请选择数据类型",
						"选择数据类型",
						JOptionPane.QUESTION_MESSAGE, 
						null, 
						typeChoice, 
						typeChoice[0]);
				if (type != null)
				{
					cbr.addFeature(name, Feature.stringToType(type));
					cbrModel.setDirty(true);
					setCBR(null);
				}
			}
		} else if (ae.getActionCommand().equals("FeatureDelete"))
		{
			//
			// Delete currently selected feature
			//
			int selCol = table.getSelectedColumn();
			int ret;
			
			if (selCol == -1)
			{
				JOptionPane.showMessageDialog(this, "Please choose the column to delete first.");
			} else
			{
				ret = JOptionPane.showConfirmDialog(this, 
						"是否删除所选特征 \"" + cbrModel.getColumnName(selCol) + "\" on column " + (selCol + 1) + "?",
						"删除确认",
						JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION)
				{
					cbr.removeFeature(selCol);
					cbrModel.setDirty(true);
					setCBR(null);
				}
			}
		} else if (ae.getActionCommand().equals("FeatureChange_type"))
		{
			//
			// Change the type of the currently selected feature
			//
			String oldType, newType;
			int selCol = table.getSelectedColumn();
			
			if (selCol == -1)
			{
				JOptionPane.showMessageDialog(this, "请选中要修改数据类型的特征.");
			} else
			{
				oldType = Feature.typeToString(cbr.getFeatureType(selCol));
				newType = (String) JOptionPane.showInputDialog(this, 
															"请选择数据类型",
															"选择数据类型",
															JOptionPane.QUESTION_MESSAGE, 
															null, 
															typeChoice, 
															oldType);
				if (newType != null && !oldType.equals(newType))
				{
					cbr.setFeatureType(selCol, Feature.stringToType(newType));
					cbrModel.setDirty(true);
					setCBR(null);
				}
			}
		} else if (ae.getActionCommand().equals("FeatureChange_name"))
		{
			//
			// Change the name of the currently selected feature
			//
			String oldName, newName;
			int selCol = table.getSelectedColumn();
			
			if (selCol == -1)
			{
				JOptionPane.showMessageDialog(this, "请选中需要修改名称的特征.");
			} else
			{
				oldName = cbr.getFeatureName(selCol);
				newName = (String) JOptionPane.showInputDialog(this, 
															"请输入特征名称",
															oldName);
				if (newName != null && !oldName.equals(newName))
				{
					cbr.setFeatureName(selCol, newName);
					cbrModel.setDirty(true);
					setCBR(null);
				}
			}
		} else if (ae.getActionCommand().equals("HelpAbout"))
		{
			//
			// Display About information
			//
			JOptionPane.showMessageDialog(this, "毕设演示！");
		} else
		{
			//
			// Should never get here...
			//
			JOptionPane.showMessageDialog(this, "Unknown function is not yet implemented");
		}
	}
	
	
	/**
	* Function called when a check button menu button is pressed
	* 
	* @param ie event that caused the action
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void itemStateChanged(ItemEvent ie)
	{
        JMenuItem source = (JMenuItem)(ie.getSource());
		
		if (source.getActionCommand().equals("FileRead_only"))
		{
			cbrModel.setReadonly(ie.getStateChange() == ItemEvent.SELECTED);
		}
	}
	
	
	/**
	* Entry function for the GUI
	* 
	* @param args the arguments to the application
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public static void main(String[] args)
	{
		String filename = null;
		String logfile = null;
		boolean verbose = false;
		boolean silent = false;
		
		// Parse command line
		for (int i = 0 ; i < args.length ; i++)
		{
			if (args[i].compareTo("?") == 0 || args[i].compareTo("/?") == 0)
			{
				usageDialog();
				System.exit(0);
			}
			if (args[i].equalsIgnoreCase("/v"))
			{
				verbose = true;
			} else if (args[i].startsWith("/log:"))
			{
				if (logfile != null)
				{
					usageDialog();
					System.exit(1);
				}
				logfile = args[i].substring(5);
			} else if (args[i].equalsIgnoreCase("/silent"))
			{
				silent = true;
			} else
			{
				if (filename != null)
				{
					usageDialog();
					System.exit(1);
				}
				filename = args[i];
			}
		}
		
		// Start GUI
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { }
		
		GUI window = new GUI(filename, logfile, verbose, silent);
		
		window.setTitle("CBR辅助诊疗系统");
		window.setVisible(true);
	}
	
	
	/**
	* Shows usage information in a dialog
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static void usageDialog()
	{
		String message;
		
		message = "This is an application (and API) that provides CBR (Case Based Reasoning) functionality.\n\n" +
							"Usage:\n" + 
							"    java FreeCBR.Console [? | /?]\n" +
							"    java FreeCBR.Console [filename] [/log:logfile] [/v]\n" +
							"where\n" + 
							"    ? or /? means print out usage information\n" + 
							"    filename means read the case set in this file\n" + 
							"    logfile means output error messages to this file\n" +
							"    /v means verbose, output extra information to logfile\n" + 
							"    /silent means do not output any information to standard error\n\n";

		JOptionPane.showMessageDialog(null, message);
	}
	
	
	/**
	* If the data is dirty then asks if the data should be saved and saves it
	* 
	* @return false if the user wants to cancel the operation that caused the 
	*		action to save, otherwise true
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private boolean saveQuery()
	{
		int ret;
		boolean res;
		
		if (!cbrModel.getDirty())
		{
			return true;
		}
		
		ret = JOptionPane.showConfirmDialog(this, 
				"案例库数据发生过修改. 是否保存?",
				"提示",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (ret == JOptionPane.YES_OPTION)
		{
			return saveSet(newSet);
		} else if (ret == JOptionPane.NO_OPTION)
		{
			return true;
		} else if (ret == JOptionPane.CANCEL_OPTION)
		{
			return false;
		}
		
		return false;	// Should never get here...
	}
	
	
	/**
	* Saves the current case set
	* 
	* @param newFile defines if the set should be saved in a new file or if 
	*		the old default should be used
	* @return true if the file was succesfully stored, otherwise false
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private boolean saveSet(boolean newFile)
	{
		int ret;
		final JFileChooser fc = new JFileChooser();
		
		if (newFile)
		{
			// Save the set in a new file
			while (true)
			{
				ret = fc.showSaveDialog(this);
				if (ret != JFileChooser.APPROVE_OPTION)
				{
					return false;
				}
				try
				{
					cbr.saveSet(fc.getSelectedFile().toString(), true);
					cbrModel.setDirty(false);
					return true;
				} catch (Exception e)
				{
					ret = JOptionPane.showConfirmDialog(this, 
							"无法保存文件 \"" + fc.getSelectedFile().toString() + "\". 是否重试?",
							"保存失败",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION)
					{
						continue;
					} else if (ret == JOptionPane.NO_OPTION)
					{
						return false;
					}
				}
			}
		} else
		{
			// The set is already stored in a file, but the file should be overwritten
			while (true)
			{
				try
				{
					cbr.saveSet(null, false);
					cbrModel.setDirty(false);
					return true;
				} catch (Exception e)
				{
					ret = JOptionPane.showConfirmDialog(this, 
							"无法保存文件. 是否重试?",
							"保存失败",
							JOptionPane.YES_NO_OPTION);
					if (ret == JOptionPane.YES_OPTION)
					{
						continue;
					} else if (ret == JOptionPane.NO_OPTION)
					{
						return false;
					}
				}
			}
		}
	}
	
	
	/**
	* Sets the CBR data set to use
	* 
	* @param cbr the new data set to use. If null then updates the table 
	*		with the current set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void setCBR(CBR cbr)
	{
		String bool_values[] = {"?", "true", "false"};
		TableColumn column = null;
		Component comp = null;
		int maxWidth = 0;
		JComboBox boolBox = new JComboBox();
		
		for (int i = 0; i < bool_values.length; i++)
		{
			boolBox.addItem(bool_values[i]);
		}
		
		// Use new data set
		if (cbr == null)
		{
			cbrModel.updateTable();
		} else
		{
			cbrModel.setCBR(cbr);
			this.cbr = cbr;
		}
		
		// Set column width
		if (this.cbr != null)
		{
			for (int col = 0; col < cbrModel.getColumnCount(); col++) 
			{
				column = table.getColumnModel().getColumn(col);
				
				// Set editor for boolean columns
				if (this.cbr.getFeatureType(col) == Feature.FEATURE_TYPE_BOOL)
				{
					column.setCellEditor(new DefaultCellEditor(boolBox));
				}
				
				comp = table.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(
						null, column.getHeaderValue(), 
						false, false, 0, 0);
				maxWidth = comp.getPreferredSize().width;
					
				for (int row = 0; row < cbrModel.getRowCount(); row++)
				{
					comp = table.getDefaultRenderer(cbrModel.getColumnClass(col)).getTableCellRendererComponent(
							table, cbrModel.getValueAt(row, col), 
							false, false, row, col);
					maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
				}
				column.setPreferredWidth(maxWidth + 10);
			}
		}
	}
}


/**
 * Class that defines the data model
 */
class CBRTableModel extends AbstractTableModel
{
	/**
	 * true if case set is changed since it was last saved, true otherwise
	 */
	private boolean dirty = false;
	/**
	 * true if it is allowed to change the data, otherwise false
	 */
	private boolean readonly = false;
	/**
	 * CBR that stores the case data
	 */
	private CBR cbr;
	
	
	/**
	* Constructor that initiates the CBRTableModel
	* 
	* @param cbr the CBR data to use in the table
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	CBRTableModel(CBR cbr)
	{
		this.cbr = cbr;
	}
	/**
	* Empty constructor that initiates the CBRTableModel
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	CBRTableModel()
	{
		this.cbr = null;
	}
	
	
	/**
	* Sets the CBR to use for the table
	* 
	* @param cbr the CBR data to use in the table
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setCBR(CBR cbr)
	{
		this.cbr = cbr;
		dirty = false;
		updateTable();
	}
	
	
	/**
	* Sets the dirty flag
	* 
	* @param dirty should the data be ragarded as dirty?
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}
	
	
	/**
	* Returns the dirty flag
	* 
	* @return true if the data is regarded as dirty, else false
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getDirty()
	{
		return dirty;
	}
	
	
	/**
	* Sets the read-only flag
	* 
	* @param readonly should the table be read only?
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
	}
	
	
	/**
	* Returns the read-only flag
	* 
	* @return true if the table is read only, otherwise false
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getReadonly()
	{
		return readonly;
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
		try
		{
			return cbr.getNumFeatures();
		} catch (Exception e)
		{
			return 0;
		}
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
			return cbr.getNumCases();
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
		try
		{
			return cbr.getFeatureName(col) + " [" + Feature.typeToString(cbr.getFeatureType(col)) + "]";
		} catch (Exception e)
		{
			return null;
		}
	}
	
	
	/**
	* Returns the value of the specified cell
	* 
	* @row the row number
	* @param col the column number
	* @return the contents of the specified cell
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Object getValueAt(int row, int col)
	{
		try
		{
			return cbr.getFeatureValue(row, col).toString();
		} catch (Exception e)
		{
			return null;
		}
	}
	
	
	/**
	* Sets the value of the specified cell
	* 
	* @param obj the object to use at the specified cell
	* @param row the row number
	* @param col the column number
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setValueAt(Object obj, int row, int col)
	{
		try
		{
			cbr.setFeatureValue(row, col, obj.toString());
			dirty = true;
		} catch (Exception e)
		{
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
		return !readonly;
	}
}


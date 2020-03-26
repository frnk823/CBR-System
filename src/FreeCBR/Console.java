/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

import java.io.*;

/**
 * This class contains functions for interactive console-oriented input/output
 *
 * @author Lars Johanson
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
public class Console
{
	/**
	 * The main entry point for this application
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.
	 */
	public static void main (String[] args)
	{
		String filename = null;
		String logfile = null;
		MainLoop actualProgram;
		boolean verbose = false;
		boolean silent = false;
		
		// Parse command line
		for (int i = 0 ; i < args.length ; i++)
		{
			if (args[i].compareTo("?") == 0 || args[i].compareTo("/?") == 0)
			{
				usage();
				System.exit(0);
			}
			if (args[i].equalsIgnoreCase("/v"))
			{
				verbose = true;
			} else if (args[i].startsWith("/log:"))
			{
				if (logfile != null)
				{
					usage();
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
					usage();
					System.exit(1);
				}
				filename = args[i];
			}
		}
		
		actualProgram = new MainLoop(filename, logfile, verbose, silent);
		actualProgram.start();
	}
	
	
	/**
	* Prints usage information to screen
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static void usage()
	{
		System.out.println("");
		System.out.println("This is an application (and API) that provides CBR (Case Based Reasoning) functionality.");
		System.out.println("");
		System.out.println("Usage:");
		System.out.println("\tjava FreeCBR.Console [? | /?]");
		System.out.println("\tjava FreeCBR.Console [filename] [/log:logfile] [/v]");
		System.out.println("where");
		System.out.println("\t? or /? means print out usage information");
		System.out.println("\tfilename means read the case set in this file");
		System.out.println("\tlogfile means output error messages to this file");
		System.out.println("\t/v means verbose, output extra information to logfile");
		System.out.println("\t/silent means do not output any messages to standard error");
		System.out.println("");
		System.out.println("Made by Lars Johanson, lars.johanson@mail.com");
	}
}



/**
 * This class is the main program, only wrapped by the "main" function
 */
class MainLoop
{
	/**
	 * Is the data changed since it was saved last?
	 */
	private boolean dataDirty = false;
	
	/**
	 * File name of current case set. If null then no case set is read
	 */
	private String filename;
	
	/**
	 * File name of log file. May be set to null which means no logging
	 */
	private String logfile;
	
	/**
	 * Determines if extra verbose information should be output to logfile
	 */
	private boolean verbose;
	
	/**
	 * Determines if no output should be done to screen
	 */
	private boolean silent;
	
	/**
	 * Current data set. If null then no data set is in memory
	 */
	private CBR cbr;
	
	
	/**
	* Constructor
	* 
	* @param filename name of a file with a case set to read at start
	* @param logfile file to use as log file
	* @param verbose determines if extra information output should be done 
	*		to screen
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	MainLoop(String filename, String logfile, boolean verbose, boolean silent)
	{
		this.filename = filename;
		this.logfile = logfile;
		this.verbose = verbose;
		this.silent = silent;
	}
	
	
	/**
	* Starts the main loop
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void start()
	{
		char ch;
		int num;
		
		if (filename != null)
		{
			try
			{
				cbr = new CBR(filename, logfile, verbose, silent);
			} catch (Exception e)
			{
			}
		}
		
		// Empty screen
		for(int i = 0 ; i < 100 ; i++)
		{
			System.out.println("");
		}
		
		// Loop eternally, wait for input...
		try
		{
			while (true)
			{
				System.out.println();
				printCommandLine();
				ch = (char) System.in.read();
				num = System.in.available();
				System.in.skip(num);
				
				switch (ch)
				{
				case 'n':
				case 'N':
					// New case set (file)
					doNewCaseSet();
					break;
					
				case 'o':
				case 'O':
					// Open case set (file)
					doOpenCaseSet();
					break;
					
				case 's':
				case 'S':
					// Save case set
					doSaveCaseSet();
					break;
					
				case 'q':
				case 'Q':
					// Quit
					doQuit();
					break;
					
				case 'f':
				case 'F':
					// Find case (search)
					doFindCase();
					break;
					
				case 'l':
				case 'L':
					// List all cases
					doListCases();
					break;
					
				case 'v':
				case 'V':
					// View single case
					doViewCase();
					break;
					
				case 'a':
				case 'A':
					// Add case
					doAddCase();
					break;
					
				case 'e':
				case 'E':
					// Edit case
					doEditCase();
					break;
					
				case 'd':
				case 'D':
					// Delete case
					doDeleteCase();
					break;
					
				case 'h':
				case 'H':
					// Help
					doPrintHelp();
					break;
					
				case 'i':
				case 'I':
					// Info
					doPrintInfo();
					break;
					
				default:
					break;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	* Prints command line
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static void printCommandLine()
	{
		System.out.print("N(ew) O(pen) S(ave) Q(uit) I(nfo) L(ist) V(iew) A(dd) E(dit) D(el) F(ind) H(elp)");
	}
	
	
	/**
	* Creates a new empty case set
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doNewCaseSet()
	{
		String req;
		String line;
		int featureNum;
		String featureNames[];
		String featureTypes[];
		
		System.out.println("New case set");
		if (dataDirty)
		{
			while (true)
			{
				System.out.println("The data is changed since it was last saved. The changes will be lost. Continue (y/n)? [y]");
				req = readLine();
				if (req.trim().compareTo("") == 0 || req.equalsIgnoreCase("y"))
				{
					break;
				} else if (req.equalsIgnoreCase("n"))
				{
					return;
				}
			}
		}
		
		if (cbr == null)
		{
			try
			{
				cbr = new CBR(logfile, verbose, silent);
			} catch (Exception e)
			{
				cbr = null;
				System.out.println("Unable to create new data set, error message " + e.toString());
			}
		}
		
		while (true)
		{
			System.out.println("Print the number of features of the new case set");
			line = readLine();
			try
			{
				featureNum = Integer.parseInt(line);
			} catch (Exception e)
			{
				System.out.println("Unable to convert \"" + line.trim() + "\" to integer. Please try again.");
				continue;
			}
			break;
		}
		
		featureNames = new String[featureNum];
		featureTypes = new String[featureNum];
		for (int i = 0 ; i < featureNum ; i++ )
		{
			System.out.println("Print the name of the feature number " + (i + 1));
			featureNames[i] = readLine();
			while (true)
			{
				System.out.println("Print the data type of feature number " + (i + 1) + " (Int/Float/Bool/String/MultiString)");
				line = readLine();
				try
				{
					Feature.stringToType(line);
				} catch (Exception e)
				{
					System.out.println("Unable to convert \"" + line.trim() + "\" to valid data type. Please try again.");
					continue;
				}
				featureTypes[i] = line;
				break;
			}
		}
		
		cbr.newSet(featureNames, featureTypes);
		filename = null;
		dataDirty = true;
	}
	
	
	/**
	* Open a case set (a file) and read the data
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doOpenCaseSet()
	{
		String req;
		String newFile;
		
		System.out.println("Open case set");
		if (dataDirty)
		{
			while (true)
			{
				System.out.println("The data is changed since it was last saved. The changes will be lost. Continue (y/n)? [y]");
				req = readLine();
				if (req.trim().compareTo("") == 0 || req.equalsIgnoreCase("y"))
				{
					break;
				} else if (req.equalsIgnoreCase("n"))
				{
					return;
				}
			}
		}
		
		System.out.println("Print the filename to open. [" + filename + "]");
		req = readLine();
		if (req.trim().compareTo("") == 0)
		{
			newFile = filename;
		} else
		{
			newFile = req;
		}
		
		if (cbr == null)
		{
			try
			{
				cbr = new CBR(logfile, verbose, silent);
			} catch (Exception e)
			{
				cbr = null;
				System.out.println("Unable to open new data set, error message " + e.toString());
			}
		}
		
		try
		{
			cbr.loadSet(newFile);
		} catch (Exception e)
		{
			System.out.println("Error \"" + e.toString() + "\" when reading file \"" + newFile + "\". Old data retained.");
			return;
		}
		
		filename = newFile;
		dataDirty = false;
	}
	
	
	/**
	* Saves the current case set to file
	* 
	* @return true on success, false on error
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private boolean doSaveCaseSet()
	{
		String newFile;
		
		System.out.println("Save case set");
		
		if (this.filename == null)
		{
			System.out.println("Print the filename to save as.");
			newFile = readLine();
			try
			{
				cbr.saveSet(newFile, true);
				filename = newFile;
			} catch (Exception e)
			{
				System.out.println("Error when saving file - data is not saved!");
				e.printStackTrace();
				return false;
			}
		} else
		{
			try
			{
				cbr.saveSet(null, false);
			} catch (Exception e)
			{
				System.out.println("Error when saving file - data is not saved!");
				e.printStackTrace();
				return false;
			}
		}
		dataDirty = false;
		System.out.println("Case set saved");
		return true;
	}
	
	
	/**
	* Exits application
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doQuit()
	{
		String req;
		
		System.out.println("Quit application");
		if (this.dataDirty)
		{
			while (true)
			{
				System.out.println("The data is changed since it was last saved. The changes will be lost. Continue (y/n)? [y]");
				req = readLine();
				if (req.trim().compareTo("") == 0 || req.equalsIgnoreCase("y"))
				{
					break;
				} else if (req.equalsIgnoreCase("n"))
				{
					return;
				}
			}
		}
		System.out.println("Good Bye...");
		System.exit(0);
	}
	
	
	/**
	* Finds the closest cases
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doFindCase()
	{
		java.util.Vector searchFeatures = new java.util.Vector();
		java.util.Vector searchStrings = new java.util.Vector();
		java.util.Vector weights = new java.util.Vector();
		String searchFeatureStrings[];
		String searchStringStrings[];
		int weightInts[];
		String line;
		CBRResult result[];
		
		System.out.println("Find case");
		while (true)
		{
			boolean endInput = false;
			int featureNum = 0;
			String featureName;
			
			// Get the feature
			while (true)
			{
				try
				{
					System.out.println("Print the feature number to look for, end input with empty line");
					for (int i = 0 ; i < cbr.getNumFeatures() ; i++ )
					{
						System.out.println(i + ": " + cbr.getFeatureName(i));
					}
					line = readLine();
					if (line.compareTo("\r") == 0)	// Empty line so exit
					{
						endInput = true;
						break;
					}
					featureNum = Integer.parseInt(line);
					featureName = cbr.getFeatureName(featureNum);
					if (featureName == null)
					{
						continue;
					}
				} catch (Exception e)
				{
					continue;
				}
				searchFeatures.addElement(featureName);
				break;
			}
			if (endInput)
			{
				break;
			}
			
			// Get the value to search for
			while (true)
			{
				try
				{
					System.out.println("Print the value to search for (of type " + Feature.typeToString(cbr.getFeatureType(featureNum)) + ")");
					line = readLine();
					searchStrings.addElement(line);
					break;
				} catch (Exception e)
				{
					System.out.println("Error - " + e.toString());
				}
			}
			// Get the weight
			System.out.println("Print the weight of the search (>= 0) [5]");
			line = readLine();
			if (line.compareTo("\r") == 0)	// Empty line so 5
			{
				weights.addElement(new Integer(5));
			} else
			{
				weights.addElement(Integer.valueOf(line));
			}
		}
		
		// Find the result
		if (searchFeatures.size() > 0)
		{
			searchFeatureStrings = new String[searchFeatures.size()];
			searchStringStrings = new String[searchStrings.size()];
			weightInts = new int[weights.size()];
			
			for ( int i = 0 ; i < searchFeatures.size() ; i++ )
			{
				searchFeatureStrings[i] = ((String) searchFeatures.elementAt(i));
				searchStringStrings[i] = ((String) searchStrings.elementAt(i));
				weightInts[i] = ((Integer) weights.elementAt(i)).intValue();
			}
			result = cbr.search(searchFeatureStrings, 
								searchStringStrings, 
								weightInts, null, null, null);
			// Output the result
			if (result == null)
			{
				System.out.println("No result found.");
			} else
			{
				int showNum;
				
				if (result.length > 21)
				{
					showNum = 20;
					System.out.println("Too many hits (" + result.length + "), showing only first " + showNum + ".");
				} else
				{
					showNum = result.length;
				}
				for (int i = 0 ; i < showNum ; i++)
				{
					System.out.println("Case " + (result[i].caseNum + 1) + ", hit " + Math.round(result[i].matchPercent*100)/100.0 + "%");
				}
			}
		}
	}
	
	
	/**
	* Print out all cases in case set
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doListCases()
	{
		Feature props[];
		int caseNum;
		int propNum;
		
		System.out.println("List all cases");
		System.out.println();
		
		if (cbr == null)
		{
			System.out.println("No case set loaded.");
			return;
		}
		
		caseNum = cbr.getNumCases();
		propNum = cbr.getNumFeatures();
		
		// Feature names
		for ( int p = 0 ; p < propNum ; p++ )
		{
			System.out.print(setSize(cbr.getFeatureName(p), 8));
			if (p < propNum - 1)
			{
				System.out.print(" ");
			}
		}
		System.out.println();
		
		// Feature types
		for ( int p = 0 ; p < propNum ; p++ )
		{
			System.out.print(setSize("(" + Feature.typeToString(cbr.getFeatureType(p)) + ")", 8));
			if (p < propNum - 1)
			{
				System.out.print(" ");
			}
		}
		System.out.println();
		
		//Feature values for all cases
		for ( int c = 0 ; c < caseNum ; c++ )
		{
			props = cbr.getCase(c);
			for ( int p = 0 ; p < propNum ; p++ )
			{
				System.out.print(setSize(props[p].toString(), 8));
				if (p < propNum - 1)
				{
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("A total of " + caseNum + " cases in case set.");
	}
	
	
	/**
	* Print out one single case
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doViewCase()
	{
		Feature props[];
		int propNum;
		int caseNum;
		String cnumStr;
		int cnum;
		int maxSize;
		
		System.out.println("View single case");
		
		if (cbr == null)
		{
			System.out.println("No case set loaded.");
			return;
		}
		
		propNum = cbr.getNumFeatures();
		caseNum = cbr.getNumCases();
		
		if (caseNum < 1)
		{
			System.out.println("There are no cases in case set.");
			return;
		}
		
		// Which case to view? Valid numbers are 0 - cbr.getNumCases() - 1
		while (true)
		{
			System.out.println("Which case do you want to view (1 - " + caseNum + ")? [1]");
			try
			{
				cnumStr = readLine();
				if (cnumStr.trim().compareTo("") == 0)
				{
					cnum = 1;
				} else
				{
					cnum = Integer.parseInt(cnumStr);
				}
			} catch (Exception e)
			{
				cnum = -1;
			}
			// Must be between 1 and cbr.getNumCases()
			if (cnum < 1 || cnum > caseNum)
			{
				System.out.println("Sorry, only integers between 1 and " + caseNum + " are allowed.");
			} else
			{
				// Change to 0-based
				cnum--;
				break;
			}
		}
		
		// Display it
		// First find the maximum size of the feature names
		maxSize = 0;
		for ( int p = 0 ; p < propNum ; p++ )
		{
			maxSize = Math.max(cbr.getFeatureName(p).length(), maxSize);
		}
		// then do the output
		System.out.println();
		System.out.println("Case number " + (cnum + 1) + ":");
		props = cbr.getCase(cnum);
		for ( int p = 0 ; p < propNum ; p++ )
		{
			System.out.print(minSize(cbr.getFeatureName(p) + ":", maxSize + 2));
			//System.out.print("[" + Feature.typeToString(cbr.getFeatureType(p)) + "]\t");
			System.out.println(props[p].toString());
			//System.out.println();
		}
	}
	
	
	/**
	* Adds a case to the case set
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doAddCase()
	{
		Feature ft[];
		String line;
		
		System.out.println("Add case. Cancel by pressing \"Return\"");
		
		if (cbr == null)
		{
			System.out.println("No case set loaded. Trying to create new...");
			doNewCaseSet();
			if (cbr == null)
			{
				return;
			}
		}
		
		ft = new Feature[cbr.getNumFeatures()];
		for (int fnum = 0 ; fnum < cbr.getNumFeatures() ; fnum++ )
		{
			while (true)
			{
				System.out.println("Print new value for \"" + cbr.getFeatureName(fnum) + "\", data type " + Feature.typeToString(cbr.getFeatureType(fnum)));
				line = readLine();
				if (line.compareTo("\r") == 0)	// Empty line so exit
				{
					System.out.println("Empty line encountered, exiting add case...");
					return;
				}
				
				try
				{
					ft[fnum] = new Feature(line, cbr.getFeatureType(fnum));
				} catch (Exception e)
				{
					System.out.println("   Unable to convert line to data type " + Feature.typeToString(cbr.getFeatureType(fnum)) + ". Please try again.");
					continue;
				}
				break;
			}
		}
		cbr.addCase(ft);
		dataDirty = true;
	}
	
	
	/**
	* Edits an existing case in the case set
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doEditCase()
	{
		Feature props[];
		int propNum;
		int caseNum;
		String cnumStr;
		int cnum;
		String line;
		
		System.out.println("Edit case");
		
		if (cbr == null)
		{
			System.out.println("No case set loaded.");
			return;
		}
		
		propNum = cbr.getNumFeatures();
		caseNum = cbr.getNumCases();
		
		if (caseNum < 1)
		{
			System.out.println("There are no cases in case set.");
			return;
		}
		
		// Which case to edit? Valid numbers are 0 - cbr.getNumCases() - 1
		while (true)
		{
			System.out.println("Which case do you want to edit (1 - " + caseNum + ")?");
			try
			{
				cnumStr = readLine();
				cnum = Integer.parseInt(cnumStr);
			} catch (Exception e)
			{
				cnum = -1;
			}
			// Must be between 1 and cbr.getNumCases()
			if (cnum < 1 || cnum > caseNum)
			{
				System.out.println("Sorry, only integers between 1 and " + caseNum + " are allowed.");
			} else
			{
				// Change to 0-based
				cnum--;
				break;
			}
		}
		
		// Do the output/input
		System.out.println();
		System.out.println("Case number " + (cnum + 1) + ":");
		props = cbr.getCase(cnum);
		for ( int p = 0 ; p < propNum ; p++ )
		{
			while (true)
			{
				try
				{
					System.out.print(cbr.getFeatureName(p) + " (" + Feature.typeToString(cbr.getFeatureType(p)) + "): [" + props[p].toString() + "] ");
					line = readLine();
					if (line.compareTo("\r") != 0)	// Value changed
					{
						dataDirty = true;
						props[p] = new Feature(line, cbr.getFeatureType(p));
					}
					break;
				} catch (Exception e)
				{
					System.out.println("Error - " + e.toString());
				}
			}
		}
		if (dataDirty)
		{
			cbr.editCase(cnum, props);
		}
	}
	
	
	/**
	* Deletes a case from the case set
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doDeleteCase()
	{
		Feature props[];
		int propNum;
		int caseNum;
		String cnumStr;
		int cnum;
		int maxSize;
		String req;
		
		System.out.println("Delete case");
		if (cbr == null)
		{
			System.out.println("No case set loaded.");
			return;
		}
		
		propNum = cbr.getNumFeatures();
		caseNum = cbr.getNumCases();
		
		if (caseNum < 1)
		{
			System.out.println("There are no cases in case set.");
			return;
		}
		
		// Which case to delete? Valid numbers are 0 - cbr.getNumCases() - 1
		while (true)
		{
			System.out.println("Which case do you want to delete (1 - " + caseNum + ")?");
			try
			{
				cnumStr = readLine();
				cnum = Integer.parseInt(cnumStr);
			} catch (Exception e)
			{
				cnum = -1;
			}
			// Must be between 1 and cbr.getNumCases()
			if (cnum < 1 || cnum > caseNum)
			{
				System.out.println("Sorry, only integers between 1 and " + caseNum + " are allowed.");
			} else
			{
				// Change to 0-based
				cnum--;
				break;
			}
		}
		
		// Display it
		// First find the maximum size of the feature names
		maxSize = 0;
		for ( int p = 0 ; p < propNum ; p++ )
		{
			maxSize = Math.max(cbr.getFeatureName(p).length(), maxSize);
		}
		// then do the output
		System.out.println();
		System.out.println("Case number " + (cnum + 1) + ":");
		props = cbr.getCase(cnum);
		for ( int p = 0 ; p < propNum ; p++ )
		{
			System.out.print(minSize(cbr.getFeatureName(p) + ":", maxSize + 2));
			//System.out.print("[" + Feature.typeToString(cbr.getFeatureType(p)) + "]\t");
			System.out.println(props[p].toString());
			//System.out.println();
		}
		
		// Delete it (?)
		while (true)
		{
			System.out.println("Do you really want to delete this case (y/n)? [y]");
			req = readLine();
			if (req.trim().compareTo("") == 0 || req.equalsIgnoreCase("y"))
			{
				this.cbr.removeCase(cnum);
				this.dataDirty = true;
				break;
			} else if (req.equalsIgnoreCase("n"))
			{
				System.out.println("Case NOT deleted.");
				return;
			}
		}
	}
	
	
	/**
	* Prints some help info on screen
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static void doPrintHelp()
	{
		System.out.println("Display help");
		System.out.println("");
		System.out.println("This is a CBR utility made in Java. It contains a Java API and a command line user interface.");
		System.out.println("It is free to use and modify.");
		System.out.println("Made by Lars Johanson, lars.johanson@mail.com");
	}
	
	
	/**
	* Prints some info about the case set on screen
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void doPrintInfo()
	{
		System.out.println("Display info");
		System.out.println("");
		if (cbr == null)
		{
			System.out.println("No case set loaded");
		} else
		{
			if (filename == null || filename.compareTo("") == 0)
			{
				System.out.println("No filename associated with case set");
			} else
			{
				System.out.println("Filename used: " + filename);
			}
			System.out.println("Number of cases in set: " + cbr.getNumCases());
			System.out.println("Number of features in case set: " + cbr.getNumFeatures());
			System.out.println("Currently used constant for infinity: " + cbr.INFINITY_CONSTANT);
		}
	}
	
	
	/**
	* Makes the string the specified size, cutting or filling with spaces
	* 
	* @param str the string to modify
	* @param sz the length to set
	* @return the string of specified length
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static String setSize(String str, int sz)
	{
		return maxSize(minSize(str, sz), sz);
	}
	
	
	/**
	* Maximizes the length of the string to specified number
	* 
	* @param str the string to modify
	* @param sz the maximum length allowed
	* @return the string with maximum letters
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static String maxSize(String str, int sz)
	{
		if (str == null)
			return "";
		if (str.length() > sz)
			return str.substring(0, sz);
		else
			return str;
	}
	
	
	/**
	* Fills the string up with spaces until it is of specified length
	* 
	* @param str the string to modify
	* @param sz the minumum length allowed
	* @return the string filled with spaces
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static String minSize(String str, int sz)
	{
		StringBuffer sb;
		
		if (str.length() >= sz)
		{
			return str;
		}
		
		sb = new StringBuffer(str);
		while (sb.length() < sz)
		{
			sb.append(" ");
		}
		return sb.toString();
	}
	
	
	/**
	* Reads a line from stdin. Blocks until there is any input.
	* 
	* @return the input string or null on error
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private static String readLine()
	{
		int ch;
		int size;
		byte arr[];
		
		try
		{
			ch = System.in.read();
			if (ch == -1)
			{
				return null;
			}
			
			size = System.in.available();
			arr = new byte[size];
			System.in.read(arr);
			
			return (char) ch + new String(arr).trim();
		} catch (Exception e)
		{
			return null;
		}
	}
}


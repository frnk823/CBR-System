/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

import java.io.*;

/**
 * This class takes care of the file reading/writing and so on.
 * Almost a bean, should only declare setDatafile and getDatafile as public.
 *
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class FileHandler implements java.io.Serializable
{
	/**
	 * Path to datafile
	 * @since 1.0
	 */
	private String datafile;
	
	/**
	 * Logger
	 * @since 1.0
	 */
	private Logger log;
	
	/**
	 * Verbose output?
	 * @since 1.0
	 */
	private boolean verbose;
	
	
	/**
	* Constructor that initiates the file handler
	* 
	* @param datafile path to the datafile
	* @param log Logger
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected FileHandler(String datafile, Logger log, boolean verbose)
	{
		this.log = log;
		this.datafile = datafile;
		this.verbose = verbose;
	}
	/**
	* Constructor that initiates the file handler
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected FileHandler()
	{
		this.log = null;
		this.datafile = "";
		this.verbose = false;
	}
	
	
	/**
	* Sets the file to read from/save to
	* 
	* @param datafile path to the datafile
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void setDatafile(String datafile)
	{
		this.datafile = datafile;
	}
	
	/**
	* Returns the currently defined file to read from/save to
	* 	* @return path to the current datafile
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected String getDatafile()
	{
		return this.datafile;
	}
	
	
	/**
	* Sets the log file
	* 
	* @param log Logger
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void setLog(Logger log)
	{
		this.log = log;
	}
	
	
	/**
	* Reads the file and stores the content in memory
	* 
	* @return the data that is read
	* @throws java.io.IOException
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected CBRdata read()
		throws java.io.IOException
	{
		String line;
		long linenum = 0;
		CBRdata data = null;
		String featureNames[] =  null;
		String featureTypeNames[] = null;
		BufferedReader in;
		
		if (verbose)
		{
			log.write("Reading datafile \"" + this.datafile + "\".");
		}
		
		in = new BufferedReader(new FileReader(datafile));
		while (in.ready())
		{
			line = in.readLine();
			if (linenum == 0)
			{
				// First line in file - headings (or feature names)
				featureNames = FString.split(line, "\t");
			} else if (linenum == 1)
			{
				// Second line in file - data types
				featureTypeNames = FString.split(line, "\t");
				try
				{
					data = new CBRdata(featureNames, featureTypeNames);
				} catch (ArrayIndexOutOfBoundsException e)
				{
					throw new java.io.IOException("Error when reading file, not equal number of properties in names and types.");
				} catch (Exception e)
				{
					throw new java.io.IOException("Error when reading file, error message:" + e.toString());
				}
			} else	// linenum > 1
			{
				// Add the case described in this line to the dataset
				try
				{
					data.addCase(line);
				} catch (Exception e)
				{
					log.write("Unable to add case to set, case #" + (linenum - 1) + ", error message: " + e.toString());
				}
			}
			linenum++;
		}
		in.close();
		
		// Create an empty data set if the file was empty
		if (data == null)
		{
			data = new CBRdata();
		}
		
		return data;
	}
	
	
	/**
	* Saves the data to a file
	* 
	* @param data the data to save
	* @param filename the name of the file in which to store the data. 
	*		If null then store in current file
	* @return nothing
	* @throws java.io.IOException
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void save(CBRdata data, String filename)
		throws java.io.IOException
	{
		PrintWriter out;
	
		if (data == null)
		{
			if (verbose)
			{
				log.write("No data to save");
			}
			return;
		}
		
		if (filename != null)
		{
			this.datafile = filename;
		}
		
		if (verbose)
		{
			log.write("Saving to datafile \"" + this.datafile + "\".");
		}
		
		out = new PrintWriter(new BufferedWriter(new FileWriter(datafile)));
		
		// First print feature names
		for ( int propnum = 0 ; propnum < data.getNumFeatures() ; propnum++ )
		{
			out.print(data.getFeatureName(propnum));
			if (propnum < data.getNumFeatures() - 1)
			{
				out.print("\t");
			} else
			{
				out.println();
			}
		}
			
		// Then print feature types
		for ( int propnum = 0 ; propnum < data.getNumFeatures() ; propnum++ )
		{
			out.print(data.getFeatureTypeString(propnum));
			if (propnum < data.getNumFeatures() - 1)
			{
				out.print("\t");
			} else
			{
				out.println();
			}
		}
		
		// And at last the data
		for ( int casenum = 0 ; casenum < data.getNumCases() ; casenum++ )
		{
			for ( int propnum = 0 ; propnum < data.getNumFeatures() ; propnum++ )
			{
				out.print(data.getFeature(casenum, propnum).toString());
				if (propnum < data.getNumFeatures() - 1)
				{
					out.print("\t");
				} else
				{
					out.println();
				}
			}
		}
		
		out.close();
		out = null;
	}
}

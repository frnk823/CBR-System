package FreeCBR;

public class CBR implements java.io.Serializable
{
	/**
	 * Constant used in calculateFeatureDistance
	 * @since 1.0
	 */
	private final static double modE = Math.E - 1;
	
	/**
	 * Data file handler
	 * @since 1.0
	 */
	private FileHandler fileHandler;
	
	/**
	 * Logger - handles logging. If null then no logging.
	 * @since 1.0
	 */
	private Logger log;
	
	/**
	 * Should the logging be verbose?
	 * @since 1.0
	 */
	private boolean verbose;
	
	/**
	 * Values further away than this are considered infinity
	 * @since 1.0
	 */
	protected int INFINITY_CONSTANT = 2;
	
	/**
	 * The actual data set
	 * @since 1.0
	 */
	private CBRdata data;
	
	/**
	 * Default weight
	 * @since 1.0
	 */
	public static final int DEFAULT_WEIGHT = 5;
	
	/* Search terms.	*/
	/**
	 * Search for closest value. Default. Valid for all data types.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_EQUAL = 0;
	
	/**
	 * Search for non-equal values. Valid for all data types.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_NOT_EQUAL = 1;
	
	/**
	 * Search for greater or equal values. Valid for Int and float.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_GREATER_OR_EQUAL = 2;
	
	/**
	 * Search for greater values. Valid for Int and float.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_GREATER = 3;
	
	/**
	 * Search for smaller or equal values. Valid for Int and float.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_LESS_OR_EQUAL = 4;
	
	/**
	 * Search for smaller values. Valid for Int and float.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_LESS = 5;
	
	/**
	 * Search for maximum values, the higher the better. Valid for Int and float.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_MAX = 6;
	
	/**
	 * Search for minimum, the lower the better. Valid for Int and float.
	 * @since 1.0
	 */
	public static final short SEARCH_TERM_MIN = 7;
	
	/* Search scale.	*/
	/**
	 * Search with a linear scale. Default.
	 * @since 1.0
	 */
	public static final short SEARCH_SCALE_FUZZY_LINEAR = 0;
	
	/**
	 * Search with a logarithmic scale
	 * @since 1.0
	 */
	public static final short SEARCH_SCALE_FUZZY_LOGARITHMIC = 1;
	
	/**
	 * Search with a "flat" scale - if the hit is not exact it is treated as maximum distance
	 * @since 1.0
	 */
	public static final short SEARCH_SCALE_FLAT = 2;
	
	/**
	 * Search "strict" - if the hit is not exact the case is not included in the result at all
	 * @since 1.0
	 */
	public static final short SEARCH_SCALE_STRICT = 3;
	
	/**
	 * Should the search result be inverted?
	 * @since 1.0
	 */
	public static final int SEARCH_OPTION_INVERTED = 1;
	
	
	/**
	* Constructor that initiates the CBR with no data.
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBR()
	{
		this.setVerbose(false);
		this.setLogfile("");
		this.setDatafile("");
		data = new CBRdata();
	}
	/**
	* Constructor that initiates the CBR with no data
	* 
	* @param logfile path to the file to write log information to. May be set to "null" 
	*		which means no logging.
	* @param verbose if <code>true</code> then extra verbose information is added to the logfile
	* @param silent if <code>true</code> then no information is output to standard error
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBR(String logfile, boolean verbose, boolean silent)
	{
		this.setVerbose(verbose);
		this.setLogfile(logfile);
		this.setDatafile("");
		if (log != null)
		{
			log.setSilent(silent);
		}
		data = new CBRdata();
	}
	/**
	* Constructor that initiates the CBR with data
	* 
	* @param datafile path to the datafile
	* @param logfile path to the file to write log information to. May be set to "null" 
	*		which means no logging.
	* @param verbose if <code>true</code> then extra verbose information is added to the logfile
	* @param silent if <code>true</code> then no information is output to standard error
	* @throws java.io.IOException if unable to read file
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBR(String datafile, String logfile, boolean verbose, boolean silent)
		throws java.io.IOException
	{
		this.setVerbose(verbose);
		this.setLogfile(logfile);
		log.setSilent(silent);
		this.setDatafile(datafile);
		this.readData();
	}
	
	
	/**
	* Returns the verbose state
	* 
	* @return the verbose state (true/false)
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getVerbose()
	{
		return verbose;
	}
	
	/**
	* Sets the verbose state
	* 
	* @param verbose the verbose state to assume
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}
	
	
	/**
	* Returns the silence state
	* 
	* @return the silence state (true/false)
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getSilent()
	{
		return log.getSilent();
	}
	
	/**
	* Sets the silence state
	* 
	* @param silent the silence state to assume
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setSilent(boolean silent)
	{
		log.setSilent(silent);
	}
	
	
	/**
	* Returns the name of the current log file
	* 
	* @return the current log file name
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getLogfile()
	{
		if (log == null)
		{
			return null;
		}
		return log.getLogfile();
	}
	
	/**
	* Sets the log file to the specified path
	* 
	* @param logfile file to use as log file
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setLogfile(String logfile)
	{
		log = new Logger(logfile);
		if (fileHandler != null)
		{
			fileHandler.setLog(log);
		}
	}
	
	
	/**
	* Returns the name of the data file currently in use
	* 
	* @return the current data file
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getDatafile()
	{
		if (fileHandler == null)
		{
			return null;
		}
		return fileHandler.getDatafile();
	}
	
	/**
	* Sets the data file. To activate the change, call readData() after the 
	*		data file is set. You might also consider the method <code>initialize()</code>
	* 
	* @param datafile file to use as input data file
	* @see #readData
	* @see #initialize(String, String)
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setDatafile(String datafile)
	{
		fileHandler = new FileHandler(datafile, log, verbose);
	}
	
	
	/**
	* Reads the data from the datafile. If you want to read a new data file, 
	*		first call setDatafile() and then readData().<br />
	*		It is also possible to use the method <code>initialize()</code> instead.
	* 
	* @throws java.io.IOException if unable to read from the current data file
	* @throws NoDataException if no fileHandler previously specified
	* @see #setDatafile(String)
	* @see #initialize(String, String)
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void readData()
		throws java.io.IOException, NoDataException
	{
		if (this.fileHandler == null)
		{
			log.write("Error in readData - no fileHandler initialized - probably no data file was specified.");
			throw new NoDataException("No file handler specified - probably no data file was specified.");
		}
		
		try
		{
			data = fileHandler.read();
			data.finishInput();
		} catch (java.io.IOException e)
		{
			log.write(e.toString());
			throw e;
		}
	}
	
	
	/**
	* Initializes the CBR if not already done. Same as setLogfile() and 
	*	setDatafile() followed by readData(). It is safe to call this 
	*	method several times, if the arguments are the same nothing happens 
	*	and the data file is NOT reread
	* 
	* @param datafile file to use as input data file. If <code>null</code> then
	*	a new empty case set is created. If the datafile already was 
	*	specified with the same value nothing happens
	* @param logfile file to use for logging. If <code>null</code> or the log file 
	*	already was specified with the same value nothing happens.
	* @throws java.io.IOException if an error occurs when reading the data file
	* @see #setLogfile(String)
	* @see #setDatafile(String)
	* @see #readData
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void initialize(String datafile, String logfile)
		throws java.io.IOException
	{
		if (logfile == null ||
				log == null || 
				log.getLogfile() == null ||
				log.getLogfile().compareTo(logfile) != 0)
		{
			setLogfile(logfile);
		}
		
		if (this.fileHandler == null)
		{
			fileHandler = new FileHandler(datafile, log, verbose);
			data = fileHandler.read();
		} else if (datafile == null)
		{
			this.fileHandler = null;
			this.data = new CBRdata();
		} else if (this.fileHandler.getDatafile().compareTo(datafile) != 0)
		{
			fileHandler = new FileHandler(datafile, log, verbose);
			data = fileHandler.read();
		} else if (data == null)
		{
			data = fileHandler.read();
		}
	}
	
	
	/**
	* Returns the current infinity constant
	* 
	* @return the current infinity constant
	* @see #INFINITY_CONSTANT
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int getINFINITY_CONSTANT()
	{
		return this.INFINITY_CONSTANT;
	}
	
	/**
	* Sets the infinity constant
	* 
	* @param infinity integer to use as infinity
	* @see #INFINITY_CONSTANT
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setINFINITY_CONSTANT(int infinity)
	{
		this.INFINITY_CONSTANT = infinity;
	}
	
	
	/**
	* Returns the number of cases in current set
	* 
	* @return the number of cases in current set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int getNumCases()
	{
		if (data == null)
		{
			return 0;
		}
		return data.getNumCases();
	}
	
	/**
	* Returns the number of features that each case has. All cases always 
	*	have the same number of features.
	* 
	* @return the number of features of the cases
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int getNumFeatures()
	{
		if (data == null)
		{
			return 0;
		}
		return data.getNumFeatures();
	}
	
	
	/**
	* Adds a case to the set
	* 
	* @param caseString a string describing the case to add. Tab separated 
	*	string with the feature values in correct order. MultiString 
	*	values are separated by semicolons. An example might be 
	*	<code>"HP[tab]1000.5[tab]CD-RW;DVD;Scanner"</code>
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void addCase(String caseString)
	{
		data.addCase(caseString);
	}
	/**
	* Adds a case to the set
	* 
	* @param features an array of features for the case to add
	* @see Feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void addCase(Feature features[])
	{
		data.addCase(features);
	}
	
	/**
	* Returns the case at the specified position
	* 
	* @param caseNum the number of the case to retrieve (0-based)
	* @return the specified case
	* @throws NoDataException when no data is in case base
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature[] getCase(int caseNum) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		return data.getCase(caseNum);
	}
	
	
	/**
	* Replaces specified case with another
	* 
	* @param caseNum the number of the case to replace
	* @param features the features of the new case
	* @return the replaced case, null on error
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature[] editCase(int caseNum, Feature features[])
	{
		return data.editCase(caseNum, features);
	}
	
	
	/**
	* Removes the specified case from the set
	* 
	* @param caseNum the number of the case to delete
	* @return the deleted case
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature[] removeCase(int caseNum)
	{
		return data.deleteCase(caseNum);
	}
	
	
	/**
	* Adds a feature (column) to the set. Pretty "expensive" - takes a few clock cycles..
	* 
	* @param name the name of the new feature
	* @param type the type of the new feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void addFeature(String name, short type)
	{
		data.addFeature(name, type);
	}
	
	
	/**
	* Returns the specified feature of the specified case
	* 
	* @param caseNum the number of the case to retrieve
	* @param featureNum the number of the feature to retrieve
	* @return the specified feature
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature getFeatureValue(int caseNum, int featureNum) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		return data.getFeature(caseNum, featureNum);
	}
	/**
	* Returns the specified feature of the specified case. Primarily used when ActiveX component.
	* 
	* @param caseNum the number of the case to retrieve
	* @param featureNum the number of the feature to retrieve
	* @return the specified feature
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getFeatureValueAX(int caseNum, int featureNum) throws NoDataException
	{
		Feature feat;

		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		return data.getFeature(caseNum, featureNum).toString();
	}
	
	
	/**
	* Sets the specified feature of the specified case to the specified value
	* 
	* @param caseNum the number of the case to change
	* @param featureNum the number of the feature to change
	* @param value new value to use
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setFeatureValue(int caseNum, int featureNum, String value) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		data.setFeature(caseNum, featureNum, value);
	}
	
	
	/**
	* Returns the name of the specified feature
	* 
	* @param featureNum the number of the feature which name to retrieve
	* @return the name of the feature
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getFeatureName(int featureNum) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		return data.getFeatureName(featureNum);
	}
	
	
	/**
	* Sets the name of the specified feature to the specified value
	* 
	* @param featureNum the number of the feature which name to change
	* @param newName the new feature name to use
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setFeatureName(int featureNum, String newName) 
		throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		data.setFeatureName(featureNum, newName);
	}
	
	
	/**
	* Returns the number of the feature that carries the specified name
	* 
	* @param featureName the name of the feature
	* @return the number of the feature, -1 if the feature is not found
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int getFeatureNum(String featureName) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		return data.getFeatureNum(featureName);
	}
	
	
	/**
	* Returns the datatype of the specified feature
	* 
	* @param featureNum the number of the feature which type to retrieve
	* @return the feature type
	* @throws NoDataException when no data is read
	* @see Feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public short getFeatureType(int featureNum) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		return data.getFeatureTypeShort(featureNum);
	}
	
	
	/**
	* Sets the datatype of the specified feature
	* 
	* @param featureNum the number of the feature which type to change
	* @param newType the feature type
	* @throws NoDataException when no data is read
	* @see Feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void setFeatureType(int featureNum, short newType) throws NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		data.setFeatureType(featureNum, newType);
	}
	
	
	/**
	* Deletes a feature (column) from the set. Pretty "expensive".
	* 
	* @param featureNumber the number of the feature to delete
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void removeFeature(int featureNumber)
	{
		data.deleteFeature(featureNumber);
	}
	
	
	/**
	* Returns all of the string values used at specified feature, works for 
	*			String and MultiString features
	* 
	* @param featureNum the number of the feature which string values to retrieve
	* @return all string values in use, null if there are no strings
	* @throws NoDataException when no data is read
	* @throws IllegalTypeException if the feature type is not <code>String</code> or <code>MultiString</code>
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String[] getUsedStringValues(int featureNum) 
		throws IllegalTypeException, NoDataException
	{
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		if (getFeatureType(featureNum) != Feature.FEATURE_TYPE_STRING &&
				getFeatureType(featureNum) != Feature.FEATURE_TYPE_MULTISTRING)
		{
			throw new IllegalTypeException("Trying to find String values of non-String feature");
		}
		return data.getUsedStrings(featureNum);
	}
	/**
	* Returns all of the string values used at specified feature, works for 
	*			String and MultiString features. Used when ActiveX component
	* 
	* @param featureNum the number of the feature which string values to retrieve
	* @param separator the separator to use
	* @return all string values in use, separated by the separator, null if there are no strings
	* @throws NoDataException when no data is read
	* @throws IllegalTypeException if the feature type is not <code>String</code> or <code>MultiString</code>
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getUsedStringValuesAX(int featureNum, String separator) 
		throws IllegalTypeException, NoDataException
	{
		String str[];
		StringBuffer ret = new StringBuffer();
		
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		if (getFeatureType(featureNum) != Feature.FEATURE_TYPE_STRING &&
				getFeatureType(featureNum) != Feature.FEATURE_TYPE_MULTISTRING)
		{
			throw new IllegalTypeException("Trying to find String values of non-String feature");
		}
		str = data.getUsedStrings(featureNum);
		for (int i = 0; i < str.length; i++)
		{
			if (i > 0)
			{
				ret.append(separator);
			}
			ret.append(str[i]);
		}
		return ret.toString();
	}
	
	
	/**
	* Returns the minimum integer value of all cases for the specified feature
	* 
	* @param featureNum the number of the feature which minimum value is to retrieve
	* @return the minimum value
	* @throws IllegalTypeException if feature not of type <code>Int</code>
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public long getMinIntValue(int featureNum) 
		throws IllegalTypeException, NoDataException
	{
		Long val;
		
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		if (getFeatureType(featureNum) != Feature.FEATURE_TYPE_INT)
		{
			throw new IllegalTypeException("Trying to find Int value of non-Int feature");
		}
		val = (Long) data.getMinValue(featureNum);
		if (val == null)
		{
			throw new NoDataException("Min. value not found");
		}
		return val.intValue();
	}
	
	/**
	* Returns the maximum integer value of all cases for the specified feature
	* 
	* @param featureNum the number of the feature which maximum value is to retrieve
	* @return the maximum value
	* @throws IllegalTypeException if feature not of type <code>Int</code>
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public long getMaxIntValue(int featureNum)
		throws IllegalTypeException, NoDataException
	{
		Long val;
		
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		if (getFeatureType(featureNum) != Feature.FEATURE_TYPE_INT)
		{
			throw new IllegalTypeException("Trying to find Int value of non-Int feature");
		}
		val = (Long) data.getMaxValue(featureNum);
		if (val == null)
		{
			throw new NoDataException("Max. value not found");
		}
		return val.longValue();
	}
	
	/**
	* Returns the minimum floating point value of all cases for the specified feature
	* 
	* @param featureNum the number of the feature which minimum value is to retrieve
	* @return the minimum value
	* @throws IllegalTypeException if feature not of type <code>Float</code>
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public double getMinFloatValue(int featureNum)
		throws IllegalTypeException, NoDataException
	{
		Double val;
		
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		if (getFeatureType(featureNum) != Feature.FEATURE_TYPE_FLOAT)
		{
			throw new IllegalTypeException("Trying to find Int value of non-Int feature");
		}
		val = (Double) data.getMinValue(featureNum);
		if (val == null)
		{
			throw new NoDataException("Min. value not found");
		}
		return val.doubleValue();
	}
	
	/**
	* Returns the maximum floating point value of all cases for the specified feature
	* 
	* @param featureNum the number of the feature which maximum value is to retrieve
	* @return the minimum value
	* @throws IllegalTypeException if feature not of type <code>Float</code>
	* @throws NoDataException when no data is read
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public double getMaxFloatValue(int featureNum)
		throws IllegalTypeException, NoDataException
	{
		Double val;
		
		if (data == null)
		{
			throw new NoDataException("CBR has no data");
		}
		if (getFeatureType(featureNum) != Feature.FEATURE_TYPE_FLOAT)
		{
			throw new IllegalTypeException("Trying to find Int value of non-Int feature");
		}
		val = (Double) data.getMaxValue(featureNum);
		if (val == null)
		{
			throw new NoDataException("Min. value not found");
		}
		return val.doubleValue();
	}
	
	
	/**
	* Saves the entire case set
	* 
	* @param filename name of the file to save as. If <code>null</code> then save to current file.
	* @param setDefault sets the specified filename to default if true. Otherwise saves as the 
	*	specified file name this time only.
	* @return nothing
	* @throws java.io.IOException if an error occurs when saving the set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void saveSet(String filename, boolean setDefault)
		throws java.io.IOException
	{
		if (setDefault)
		{
			fileHandler.setDatafile(filename);
		}
		fileHandler.save(data, filename);
	}
	
	/**
	* Loads a case set to memory
	* 
	* @param filename name of the file to use.
	* @return nothing
	* @throws java.lang.Exception if an error occurs when loading the set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void loadSet(String filename)
		throws java.lang.Exception
	{
		FileHandler newFH;
		CBRdata newData;
		
		try
		{
			newFH = new FileHandler(filename, log, verbose);
			newData = newFH.read();
		} catch (Exception e)
		{
			log.write(e.toString());
			throw e;
		}
		// if everything worked then use the new values
		fileHandler = newFH;
		data = newData;
	}
	
	
	/**
	* Empties the memory - deletes the current set from memory and creates 
	*	a new empty set with the specified feature names and feature data types
	* 
	* @param featureNames an array of feature names to use
	* @param featureTypeNames an array of data type names to use (such as "String", "Float" and so on)
	* @return nothing
	* @see Feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public void newSet(String featureNames[], String featureTypeNames[])
	{
		fileHandler.setDatafile(null);
		this.data = new CBRdata(featureNames, featureTypeNames);
	}
	
	
	/**
	* Performs a search for the best match in a "web" way. Takes the servlet 
	*		request part of an URL and tries to make the best of it.<br />
	* The search parameters must be named 
	*		<code>featX</code>, <code>weightX</code>, <code>termX</code>, 
	*		<code>scaleX</code> and <code>optionX</code> where <code>X</code> is 
	*		the number of the feature corresponding to this value
	*	An example could be<br />
	*		<code>CBRBean.search(req)</code>
	*		where <code>req.getQueryString()</code> might look like
	*		<code>feat0=Compaq&scale0=0&feat3=1000&weight3=10</code>
	* 
	* @param req the servlet (or jsp) request. Must be <b><code>javax.servlet.http.HttpServletRequest</code></b>
	* @return WebResult specifying both the result and what was actually 
	*		searched for
	* @throws NoDataException when not enough data is present
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public WebResult search(Object req)
		throws NoDataException, Exception
	{
		return new WebResult(this, req);
	}
	/**
	* Performs a search for the best match, used primarily by ActiveX components. Principle example: <br />
	*	search(	{"Manufacurer",	"Speed",	"HDD",	"CD"}, 
	*		{"Compaq",	"1000",		"1.3",	"?"}, 
	*		{4,		6,		6,	5}, 
	*		null,
	*		{0,		0,		0,	0},
	*		{0,		0,		0,	0}, 
	*		":", ";")
	* 
	* @param searchFeatureNames array of names of the features. Must be an array of <code>Strings</code>.
	* @param searchValues array of strings describing the features to search for. Must be an array of <code>Strings</code>.
	* @param searchWeights array of weights for the search, valid values are >0 
	*		where 0 means don't care. May be set to 
	*		null which means alla features are equally important. Must be <code>Null</code> or an array of <code>Integers</code>.
	* @param searchTerms array of terms of the search. May be any of<br />
	*		CBR.SEARCH_TERM_EQUAL, CBR.SEARCH_TERM_GREATER, 
	*		CBR.SEARCH_TERM_GREATER_OR_EQUAL, CBR.SEARCH_TERM_LESS, CBR.SEARCH_TERM_LESS_OR_EQUAL, 
	*		CBR.SEARCH_TERM_MAX, CBR.SEARCH_TERM_MIN and 
	*		CBR.SEARCH_TERM_NOT_EQUAL. Default (when set to 0 or null) is 
	*		CBR.SEARCH_TYPE_EQUAL. Must be <code>Null</code> or an array of <code>Integers</code>.
	* @param searchScales array of the scale to use. May be any of 
	*		CBR.SEARCH_SCALE_FUZZY_LINEAR, CBR.SEARCH_SCALE_FUZZY_LOGARITHMIC, 
	*		CBR.SEARCH_SCALE_FLAT and CBR.SEARCH_SCALE_STRICT. Default (when set to 0 or null) is 
	*		CBR.SEARCH_SCALE_FUZZY_LINEAR. Must be <code>Null</code> or an array of <code>Integers</code>.
	* @param searchOptions array of options on how to perform the search. Default is no options. Must be <code>Null</code> or an array of <code>Integers</code>.
	* @param resultSeparator string to use to separate the case number and the match percentage in the result
	* @param caseSeparator string to use to separate the cases in the result
	* @return string containing the result. If the <code>resultSeparator</code> 
	*		is ":" and the <code>caseSeparator</code> is ";" the result might look like: 
	*		<code>"3:33.3;0:25;2:12.5;1:12.5;4:0"</code> which would mean that the best 
	*		match is case number 3 with a search hit of 33.3%, case number 0 has a hit 
	*		rate of 25% and case number 1 and 2 have a hit rate of 12.5% each. Case 
	*		number 4 has the lowest hit rate, 0%. The cases are always returned in 
	*		decreasing hit order.
	* @throws NoDataException when not enough data is present
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String searchAX(Object searchFeatureNames[], // Strings
							  Object searchValues[],	// Strings
							  Object searchWeights[],	// Integers
							  Object searchTerms[],		// Integers
							  Object searchScales[],	// Integers
							  Object searchOptions[],	// Integers
							  String resultSeparator,
							  String caseSeparator)
		throws NoDataException
	{
		StringBuffer ret = new StringBuffer();
		CBRResult res[];
		String sNames[], sValues[];
		int sWeights[], sTerms[], sScales[], sOptions[];
		if (searchFeatureNames != null)
		{
			sNames = new String[searchFeatureNames.length];
			for (int i = 0; i < searchFeatureNames.length; i++)
			{
				sNames[i] = (String) searchFeatureNames[i];
			}
		} else
		{
			sNames = null;
		}
		if (searchValues != null)
		{
			sValues = new String[searchValues.length];
			for (int i = 0; i < searchValues.length; i++)
			{
				sValues[i] = (String) searchValues[i];
			}
		} else
		{
			sValues = null;
		}
		if (searchWeights != null)
		{
			sWeights = new int[searchWeights.length];
			for (int i = 0; i < searchWeights.length; i++)
			{
				sWeights[i] = getInt(searchWeights[i]);
			}
		} else
		{
			sWeights = null;
		}
		if (searchTerms != null)
		{
			sTerms = new int[searchTerms.length];
			for (int i = 0; i < searchTerms.length; i++)
			{
				sTerms[i] = getInt(searchTerms[i]);
			}
		} else
		{
			sTerms = null;
		}
		if (searchScales != null)
		{
			sScales = new int[searchScales.length];
			for (int i = 0; i < searchScales.length; i++)
			{
				sScales[i] = getInt(searchScales[i]);
			}
		} else
		{
			sScales = null;
		}
		if (searchOptions != null)
		{
			sOptions = new int[searchOptions.length];
			for (int i = 0; i < searchOptions.length; i++)
			{
				sOptions[i] = getInt(searchOptions[i]);
			}
		} else
		{
			sOptions = null;
		}
		res = search(sNames, sValues, sWeights, sTerms, sScales, sOptions);
		for (int i = 0; i < res.length; i++)
		{
			if (i > 0)
			{
				ret.append(caseSeparator);
			}
			ret.append(res[i].caseNum + resultSeparator + Math.round(res[i].matchPercent*10)/10);
		}
		return ret.toString();
	}
	/**
	* Performs a search for the best match. Principle example: <br />
	*	search(	{"Manufacurer",	"Speed",	"HDD",	"CD"}, 
	*		{"Compaq",	"1000",		"1.3",	"?"}, 
	*		{4,		6,		6,	5}, 
	*		{0,		0,		0,	0},
	*		{0,		0,		0,	0},
	*		{0,		0,		0,	0})
	* 
	* @param searchFeatureNames array of names of the features
	* @param searchValues array of strings describing the features to search for
	* @param searchWeights array of weights for the search, valid values are >0 
	*		where 0 means don't care. May be set to 
	*		null which means alla features are equally important.
	* @param searchTerms array of terms of the search. May be any of<br />
	*		CBR.SEARCH_TERM_EQUAL, CBR.SEARCH_TERM_GREATER, 
	*		CBR.SEARCH_TERM_GREATER_OR_EQUAL, CBR.SEARCH_TERM_LESS, CBR.SEARCH_TERM_LESS_OR_EQUAL, 
	*		CBR.SEARCH_TERM_MAX, CBR.SEARCH_TERM_MIN and 
	*		CBR.SEARCH_TERM_NOT_EQUAL. Default (when set to 0 or null) is 
	*		CBR.SEARCH_TYPE_EQUAL
	* @param searchScales array of the scale to use. May be any of 
	*		CBR.SEARCH_SCALE_FUZZY_LINEAR, CBR.SEARCH_SCALE_FUZZY_LOGARITHMIC, 
	*		CBR.SEARCH_SCALE_FLAT and CBR.SEARCH_SCALE_STRICT. Default (when set to 0 or null) is 
	*		CBR.SEARCH_SCALE_FUZZY_LINEAR.
	* @param searchOptions array of options on how to perform the search. Default is no options.
	* @return array of result, ordered by match
	* @throws NoDataException when not enough data is present
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBRResult[] search(String searchFeatureNames[], 
							  String searchValues[], 
							  int searchWeights[], 
							  int searchTerms[], 
							  int searchScales[], 
							  int searchOptions[])
		throws NoDataException
	{
		int searchFeatureNumbers[];
		Feature searchData[];
		
		if (verbose)
		{
			log.writeNL();
			log.write("New search for");
			for (int i = 0 ; i < searchFeatureNames.length - 1 ; i++ )
			{
				log.writeShort(searchFeatureNames[i] + "=" + searchValues[i] + ", ");
			}
			log.writeShort(searchFeatureNames[searchFeatureNames.length - 1] + "=" + searchValues[searchFeatureNames.length - 1]);
			log.writeNL();
		}
		// Check for errors in input
		if (searchValues == null || searchValues.length == 0)
		{
			throw new NoDataException("Not enough data available");
		}
		
		// Find all features and feature numbers. Do it now of performance reasons
		searchData = new Feature[searchValues.length];
		searchFeatureNumbers = new int[searchFeatureNames.length];
		for ( int i = 0 ; i < searchValues.length ; i++ )
		{
			searchFeatureNumbers[i] = getFeatureNum(searchFeatureNames[i]);
			if (searchFeatureNumbers[i] < 0)
			{
				// An error occurred
				throw new IllegalTypeException("The feature type is not valid (\"" + searchFeatureNumbers[i] + "\"");
			}
			searchData[i] = new Feature(searchValues[i], getFeatureType(searchFeatureNumbers[i]));
		}
		
		// Do the search...
		return search(searchFeatureNumbers, searchData, searchWeights, searchTerms, searchScales, searchOptions);
	}
	/**
	* Performs a search for the best match.
	* 
	* @param searchFeatureNumbers array of types of the features
	* @param searchValues array of features to search for
	* @param searchWeights array of weights for the search, valid values are 0 to 10 
	*		where 0 means don't care and 10 means "must match". May be set to 
	*		null which means alla features are equally important.
	* @param searchTerms array of terms of the search. May be any of<br />
	*		CBR.SEARCH_TERM_EQUAL, CBR.SEARCH_TERM_GREATER, 
	*		CBR.SEARCH_TERM_GREATER_OR_EQUAL, CBR.SEARCH_TERM_LESS, CBR.SEARCH_TERM_LESS_OR_EQUAL, 
	*		CBR.SEARCH_TERM_MAX, CBR.SEARCH_TERM_MIN and 
	*		CBR.SEARCH_TERM_NOT_EQUAL. Default (when set to 0 or null) is 
	*		CBR.SEARCH_TYPE_EQUAL
	* @param searchScales array of the scale to use. May be any of 
	*		CBR.SEARCH_SCALE_FUZZY_LINEAR, CBR.SEARCH_SCALE_FUZZY_LOGARITHMIC, 
	*		CBR.SEARCH_SCALE_FLAT and CBR.SEARCH_SCALE_STRICT. Default (when set to 0 or null) is 
	*		CBR.SEARCH_SCALE_FUZZY_LINEAR.
	* @param searchOptions array of options on how to perform the search. Default is no options.
	* @return array of result, ordered by match
	* @see #search(String[], String[], int[], int[], int[], int[])
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBRResult[] search(int searchFeatureNumbers[], 
							  Feature searchValues[], 
							  int searchWeights[], 
							  int searchTerms[], 
							  int searchScales[], 
							  int searchOptions[])
	{
		FVector resultSet;
		CBRResult[] resultArr;
		double dist;
		double hit;
		long searchWeightsSum = 0;
		boolean disqualified;
		
		if (verbose)
		{
			log.writeNL();
			log.write("New search for");
			if (searchFeatureNumbers == null ||
				searchValues == null)
			{
				log.writeShort("Trying to search with \"null\" parameters, throwing exception...");
				throw new NoDataException("Not enough data available");
			}
			for (int i = 0 ; i < searchFeatureNumbers.length - 1 ; i++ )
			{
				log.writeShort(getFeatureName(searchFeatureNumbers[i]) + "=" + searchValues[i].toString() + ", ");
			}
			log.writeShort(getFeatureName(searchFeatureNumbers[searchFeatureNumbers.length - 1]) + "=" + searchValues[searchFeatureNumbers.length - 1].toString());
			log.writeNL();
		}
		
		// Check for errors in input
		if (searchFeatureNumbers == null || 
			searchValues == null || 
			searchFeatureNumbers.length != searchValues.length || 
			searchWeights != null && searchValues.length != searchWeights.length || 
			searchTerms != null && searchValues.length != searchTerms.length ||
			searchScales != null && searchValues.length != searchScales.length ||
			searchOptions != null && searchValues.length != searchOptions.length)
		{
			throw new NoDataException("Not enough data available");
		}
		
		// Make sure the weights and search types are consistent
		if (searchWeights == null)
		{
			searchWeights = new int[searchValues.length];
			for ( int i = 0 ; i < searchValues.length ; i++ )
			{
				searchWeights[i] = DEFAULT_WEIGHT;
			}
		}
		if (searchTerms == null)
		{
			searchTerms = new int[searchValues.length];
			for ( int i = 0 ; i < searchValues.length ; i++ )
			{
				searchTerms[i] = 0;
			}
		}
		if (searchScales == null)
		{
			searchScales = new int[searchValues.length];
			for ( int i = 0 ; i < searchValues.length ; i++ )
			{
				searchScales[i] = 0;
			}
		}
		if (searchOptions == null)
		{
			searchOptions	 = new int[searchValues.length];
			for ( int i = 0 ; i < searchValues.length ; i++ )
			{
				searchOptions[i] = 0;
			}
		}
		
		// Start calculating the results
		resultSet = new FVector(this.getNumCases());
		// Loop through all cases
		
		for ( int caseNum = 0 ; caseNum < data.getNumCases() ; caseNum++ )
		{
			disqualified = false;
			
			if (verbose)
			{
				log.writeNL();
				log.writeShort("caseNum: " + caseNum);
				log.writeNL();
			}
			searchWeightsSum = 0;
			hit = 0.0;
			// and loop through all searched features for each case
			for ( int searchNum = 0 ; searchNum < searchValues.length ; searchNum++ )
			{
				dist = calculateFeatureDistance(getFeatureValue(caseNum, searchFeatureNumbers[searchNum]), 
												searchValues[searchNum], 
												searchTerms[searchNum], 
												searchScales[searchNum], 
												searchFeatureNumbers[searchNum]);
				if (verbose)
				{
					log.writeShort("" + dist);
					log.writeNL();
				}
				if (dist < -1.5)
				{
					// Case disqualified, continue with the next one
					disqualified = true;
					break;
				} else if (dist < 0)
				{
					// Feature disqualified, continue with the next one
					continue;
				}
				if ((searchOptions[searchNum] & SEARCH_OPTION_INVERTED) != 0)
				{
					dist = 1 - dist;
				}
				searchWeightsSum += searchWeights[searchNum];
				hit += searchWeights[searchNum] * 
					   Math.pow(dist, 2);
			}
			if (disqualified)
			{
				continue;
			}
			if (verbose)
			{
				log.writeShort("hit: " + hit);
				log.writeNL();
			}
			if (searchWeightsSum != 0)	// Add case to result set only if at least one feature difference is found at all
			{
				resultSet.add(new CBRResult(caseNum, 100 * (1 - Math.sqrt(hit/searchWeightsSum))));
			}
		}
		// Transform the result set to array, sort it and return it
		resultSet.sort(new CBRResultComparator());
		resultArr = (CBRResult[]) resultSet.toArray(CBRResult.class);
		return resultArr;
	}
	
	/**
	 * Calculates the distance between two CBR cases
	 * 
	 * @param caseFeature feature from a case
	 * @param searchFeature searched feature
	 * @param searchTerm term of search to perform
	 * @param searchScale the scale of the search to perform
	 * @param featureNum the number of the feature (number in the case)
	 * @return the distance between the two case features, a decimal 
	 *		number between 0.0 and 1.0 where 1.0 is the maximum distance 
	 *		and 0 means exact hit.
	 *		May also return -1.0 which means that the feature 
	 *		should be disqualified or -2.0 which means the entire case should be disqualified
	 * @since 1.0
	 */
	private double calculateFeatureDistance(
			Feature caseFeature, 
			Feature searchFeature, 
			int searchTerm,
			int searchScale, 
			int featureNum)
	{
		double fDiff = 0;
		double fCorrRange;
		double tmpRes;
		
		if (verbose)
			log.writeShort("Distance between " + Feature.typeToString(getFeatureType(featureNum)) + " \"" + caseFeature.toString() + "\" and \"" + searchFeature.toString() + "\", search type " + searchTerm + ", " + searchScale + ": ");
		
		// This might not be optimal regarding performance, but simple...
		if (searchScale == SEARCH_SCALE_FUZZY_LOGARITHMIC)
		{
			// Return -1 or -2 the same way as fuzzy linear. Otherwise do it logarithmic
			tmpRes = calculateFeatureDistance(caseFeature, searchFeature, searchTerm, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
			if (tmpRes < 0)
				return tmpRes;
			return Math.log(tmpRes * modE + 1);
		}
		
		// If the search feature value is undefined and it is not a search for 
		// max or min, the feature should be be omitted
		if (searchFeature.isUndefined() &&
				searchTerm != CBR.SEARCH_TERM_MAX && 
				searchTerm != CBR.SEARCH_TERM_MIN)
		{
			return -1;
		}
		// Else if the case feature is undefined the distance is regarded as maximum
		if (caseFeature.isUndefined())
		{
			return 1;
		}
		
		if (!searchFeature.isUndefined())
		{
			fDiff = diff(searchFeature, caseFeature);
		}
		fCorrRange = range(featureNum) * INFINITY_CONSTANT;
		
		switch (searchTerm)
		{
		case CBR.SEARCH_TERM_EQUAL:
			if (fDiff == 0)
				return 0;
			if (searchScale == SEARCH_SCALE_FLAT)
			{
				return 1;
			} else if (searchScale == SEARCH_SCALE_STRICT)
			{
				return -2;
			} else if (searchScale == SEARCH_SCALE_FUZZY_LINEAR)
			{
				if (caseFeature.getFeatureType() != Feature.FEATURE_TYPE_FLOAT &&
					caseFeature.getFeatureType() != Feature.FEATURE_TYPE_INT)
				{
					return fDiff;
				}
				// It is a Float or Int!
				if (fCorrRange == 0)
				{
					// The search is not an exact hit and there is only one value among all cases
					return 1;
				}
				return Math.min(1, Math.abs(fDiff)/fCorrRange);
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_NOT_EQUAL:
			if (searchScale == SEARCH_SCALE_STRICT)
			{
				if (fDiff == 0)
					return -2;
				else
					return 0;
			}
			if (fDiff == 0)
				return 1;
			if (searchScale == SEARCH_SCALE_FLAT)
			{
				return 0;
			} else if (searchScale == SEARCH_SCALE_FUZZY_LINEAR)
			{
				if (caseFeature.getFeatureType() != Feature.FEATURE_TYPE_FLOAT &&
					caseFeature.getFeatureType() != Feature.FEATURE_TYPE_INT)
				{
					return 1 - fDiff;
				}
				// It is a Float or Int!
				if (fCorrRange == 0)
				{
					// The search is not an exact hit and there is only one value among all cases
					return 0;
				}
				return 1 - Math.min(1, Math.abs(fDiff)/range(featureNum));
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_GREATER_OR_EQUAL:
			if (searchScale == SEARCH_SCALE_STRICT)
			{
				if (fDiff >= 0)
					return 0;
				else
					return -2;
			}
			if (fDiff >= 0)
				return 0;
			if (searchScale == SEARCH_SCALE_FLAT)
			{
				return 1;
			} else if (searchScale	== SEARCH_SCALE_FUZZY_LINEAR)
			{
				return calculateFeatureDistance(caseFeature, searchFeature, SEARCH_TERM_EQUAL, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_GREATER:
			if (searchScale == SEARCH_SCALE_STRICT)
			{
				if (fDiff > 0)
					return 0;
				else
					return -2;
			}
			if (fDiff > 0)
				return 0;
			if (searchScale == SEARCH_SCALE_FLAT)
			{
				return 1;
			} else if (searchScale	== SEARCH_SCALE_FUZZY_LINEAR)
			{
				if (caseFeature.getFeatureType() == Feature.FEATURE_TYPE_INT)
				{
					// Fuzzy Int search for ">x" should be the same as ">=(x+1)"
					return calculateFeatureDistance(caseFeature, new Feature(searchFeature.getIntValue() + 1), SEARCH_TERM_EQUAL, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
				} else
				{
					return calculateFeatureDistance(caseFeature, searchFeature, SEARCH_TERM_EQUAL, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
				}
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_LESS_OR_EQUAL:
			if (searchScale == SEARCH_SCALE_STRICT)
			{
				if (fDiff <= 0)
					return 0;
				else
					return -2;
			}
			if (fDiff <= 0)
				return 0;
			if (searchScale == SEARCH_SCALE_FLAT)
			{
				return 1;
			} else if (searchScale	== SEARCH_SCALE_FUZZY_LINEAR)
			{
				return calculateFeatureDistance(caseFeature, searchFeature, SEARCH_TERM_EQUAL, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_LESS:
			if (searchScale == SEARCH_SCALE_STRICT)
			{
				if (fDiff < 0)
					return 0;
				else
					return -2;
			}
			if (fDiff < 0)
				return 0;
			if (searchScale == SEARCH_SCALE_FLAT)
			{
				return 1;
			} else if (searchScale	== SEARCH_SCALE_FUZZY_LINEAR)
			{
				if (caseFeature.getFeatureType() == Feature.FEATURE_TYPE_INT)
				{
					// Fuzzy Int search for "<x" should be the same as "<=(x+1)"
					return calculateFeatureDistance(caseFeature, new Feature(searchFeature.getIntValue() - 1), SEARCH_TERM_EQUAL, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
				} else
				{
					return calculateFeatureDistance(caseFeature, searchFeature, SEARCH_TERM_EQUAL, SEARCH_SCALE_FUZZY_LINEAR, featureNum);
				}
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_MAX:
			if (caseFeature.getFeatureType() == Feature.FEATURE_TYPE_FLOAT)
			{
				double floatDiff = getMaxFloatValue(featureNum) - caseFeature.getFloatValue();
				if (searchScale == SEARCH_SCALE_STRICT)
				{
					if (floatDiff == 0)
						return 0;
					else
						return -2;
				} else if (searchScale == SEARCH_SCALE_FLAT)
				{
					if (floatDiff == 0)
						return 0;
					else
						return 1;
				} else if (searchScale == SEARCH_SCALE_FUZZY_LINEAR)
				{
					if (fCorrRange == 0)
						return floatDiff == 0 ? 0 : 1;
					else
						return floatDiff/fCorrRange;
				}
			} else if (caseFeature.getFeatureType() == Feature.FEATURE_TYPE_INT)
			{
				long longDiff = getMaxIntValue(featureNum) - caseFeature.getIntValue();
				if (searchScale == SEARCH_SCALE_STRICT)
				{
					if (longDiff == 0)
						return 0;
					else
						return -2;
				} else if (searchScale == SEARCH_SCALE_FLAT)
				{
					if (longDiff == 0)
						return 0;
					else
						return 1;
				} else if (searchScale == SEARCH_SCALE_FUZZY_LINEAR)
				{
					if (fCorrRange == 0)
						return longDiff == 0 ? 0 : 1;
					else
						return longDiff/fCorrRange;
				}
			} else
			{
				// Neither FLOAT nor INT
				return -1;
			}
			return -1;	// Unknown search
			
		case CBR.SEARCH_TERM_MIN:
			if (caseFeature.getFeatureType() == Feature.FEATURE_TYPE_FLOAT)
			{
				double floatDiff = caseFeature.getFloatValue() - getMinFloatValue(featureNum);
				if (searchScale == SEARCH_SCALE_STRICT)
				{
					if (floatDiff == 0)
						return 0;
					else
						return -2;
				} else if (searchScale == SEARCH_SCALE_FLAT)
				{
					if (floatDiff == 0)
						return 0;
					else
						return 1;
				} else if (searchScale == SEARCH_SCALE_FUZZY_LINEAR)
				{
					if (fCorrRange == 0)
						return floatDiff == 0 ? 0 : 1;
					else
						return floatDiff/fCorrRange;
				}
			} else if (caseFeature.getFeatureType() == Feature.FEATURE_TYPE_INT)
			{
				long longDiff = caseFeature.getIntValue() - getMinIntValue(featureNum);
				if (searchScale == SEARCH_SCALE_STRICT)
				{
					if (longDiff == 0)
						return 0;
					else
						return -2;
				} else if (searchScale == SEARCH_SCALE_FLAT)
				{
					if (longDiff == 0)
						return 0;
					else
						return 1;
				} else if (searchScale == SEARCH_SCALE_FUZZY_LINEAR)
				{
					if (fCorrRange == 0)
						return longDiff == 0 ? 0 : 1;
					else
						return longDiff/fCorrRange;
				}
			} else
			{
				// Neither FLOAT nor INT
				return -1;
			}
			return -1;	// Unknown search
		}
		return -1;	// Unknown search
	}
	
	/**
	 * Calculates the difference between two features (-infinity - +infinity)
	 * 
	 * @param searchFeature the searched feature
	 * @param caseFeature a database feature
	 * @return the difference between the two features. A difference of 0.0 
	 *		means they are equal. If the feature type is Int or Float then 
	 *		the difference is the mathematical difference. If the type is 
	 *		Bool or String and the values are not equal the result is 
	 *		1.0. If the type is MultiString the return value is 
	 *		1 - (found features in the database feature / the number of features 
	 *		in the searched feature)
	 * @throws IllegalTypeException if the two features are not of the same type
	 * @since 1.0
	 */
	static private double diff(Feature searchFeature, Feature caseFeature)
		throws IllegalTypeException
	{
		short featureType;
		
		featureType = searchFeature.getFeatureType();
		if (featureType != caseFeature.getFeatureType())
		{
			throw new IllegalTypeException("Impossible to find the difference between two features of different types");
		}
		switch (featureType)
		{
		case Feature.FEATURE_TYPE_BOOL:
			if (searchFeature.getBoolValue() == caseFeature.getBoolValue())
				return 0;
			else
				return 1;
			
		case Feature.FEATURE_TYPE_FLOAT:
			return caseFeature.getFloatValue() - searchFeature.getFloatValue();
			
		case Feature.FEATURE_TYPE_INT:
			return caseFeature.getIntValue() - searchFeature.getIntValue();
			
		case Feature.FEATURE_TYPE_MULTISTRING:
			String m1[], m2[];
			int found, total;
			
			m1 = searchFeature.getMultiStringValue();
			m2 = caseFeature.getMultiStringValue();
			if (m1.length == 0)
				return 1;
			found = 0;
			total = m1.length;
			for (int i1 = 0; i1 < m1.length; i1++ )
			{
				for (int i2 = 0; i2 < m2.length; i2++ )
				{
					if (m1[i1].compareTo(m2[i2]) == 0)
					{
						found++;
						break;
					}
				}
			}
			return 1 - ((double)found)/total;
			
		case Feature.FEATURE_TYPE_STRING:
			if (caseFeature.getStringValue().compareTo(searchFeature.getStringValue()) == 0)
				return 0;
			else
				return 1;
		}
		return 1;
	}
	
	
	/**
	 * Calculates the fraction between the two features
	 * 
	 * @param f1 a feature
	 * @param f2 another feature
	 * @return the fraction between the two features. A fraction of 1
	 *		means they are equal. If the feature type is Int or Float then 
	 *		the fraction is the mathematical fraction. If the type is 
	 *		Bool or String and the values are not equal the result is 
	 *		0. If the 
	 *		type is MultiString and all values are the same the fraction is 
	 *		1. If at least one but not all of the values are the same the result 
	 *		is 0.5.
	 * @throws IllegalTypeException if the two features are not of the same type
	 * @since 1.0
	 */
	private double fraction(Feature f1, Feature f2)
		throws IllegalTypeException
	{
		short featureType;
		
		featureType = f1.getFeatureType();
		if (featureType != f2.getFeatureType())
		{
			throw new IllegalTypeException("Impossible to find the fraction between two features of different types");
		}
		switch (featureType)
		{
		case Feature.FEATURE_TYPE_BOOL:
			if (f1.getBoolValue() == f2.getBoolValue())
				return 1;
			else
				return 0;
			
		case Feature.FEATURE_TYPE_FLOAT:
			if (f1.getFloatValue() == f2.getFloatValue())	// May for example be 0.0
			{
				return 1;
			} else if (f1.getFloatValue() == 0)
			{
				return Math.exp(INFINITY_CONSTANT);
			}
			return f2.getFloatValue() / f1.getFloatValue();
			
		case Feature.FEATURE_TYPE_INT:
			if (f1.getIntValue() == f2.getIntValue())	// May for example be 0
			{
				return 1;
			} else if (f1.getIntValue() == 0)
			{
				return Math.exp(INFINITY_CONSTANT);
			}
			return f2.getIntValue() / f1.getIntValue();
			
		case Feature.FEATURE_TYPE_MULTISTRING:
			String m1[], m2[];
			boolean firstFound = false;
			boolean exactHitChance = true;
			
			m1 = f1.getMultiStringValue();
			m2 = f2.getMultiStringValue();
			if (m1.length != m2.length)
			{
				exactHitChance = false;
			}
			for (int i1 = 0 ; i1 < m1.length ; i1++ )
			{
				for (int i2 = 0 ; i2 < m2.length ; i2++ )
					if (m1[i1].compareTo(m2[i2]) == 0)
					{
						firstFound = true;
						if (!exactHitChance)
						{
							return 0.5;
						}
						break;
					} else
					{
						exactHitChance = false;
					}
			}
			if (firstFound)
			{
				if (exactHitChance)
					return 1;
				else
					return 0.5;
			} else
			{
				return 0;
			}
			
		case Feature.FEATURE_TYPE_STRING:
			if (f2.getStringValue().compareTo(f1.getStringValue()) == 0)
				return 1;
			else
				return 0;
		}
		return 0;
	}
	
	
	/**
	 * Calculates the range of values for the specified feature
	 * 
	 * @param featureNum the feature number to calculate the range for
	 * @return the range of values for the specified feature. If not 
	 *		Feature.FEATURE_TYPE_FLOAT or Feature.FEATURE_TYPE_INT 
	 *		then returns 1.0.
	 * @since 1.0
	 */
	private double range(int featureNum)
	{
		if (getFeatureType(featureNum) == Feature.FEATURE_TYPE_FLOAT)
		{
			return getMaxFloatValue(featureNum) - getMinFloatValue(featureNum);
		} else if (getFeatureType(featureNum) == Feature.FEATURE_TYPE_INT)
		{
			return getMaxIntValue(featureNum) - getMinIntValue(featureNum);
		}
		return 1;
	}
	
	
	/**
	 * Tries (desperately) to get an integer value from the specified Object
	 * 
	 * @param o the object to retrieve the integer value from
	 * @return an integer value retrieved from the object
	 * @since 1.0
	 */
	private int getInt(Object o)
	{
		try
		{
			return ((Number) o).intValue();
		} catch (Exception e)
		{
			return Integer.parseInt(o.toString());
		}
	}
}


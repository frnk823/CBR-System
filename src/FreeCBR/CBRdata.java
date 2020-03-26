/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

/**
 * This class stores the data for CBR
 *
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class CBRdata implements java.io.Serializable
{
	/**
	 * Is the data changed since finding min- and max-values? Only used internally.
	 * @since 1.0
	 */
	private boolean dirty = false;
	
	/**
	 * An array of min-values
	 * @since 1.0
	 */
	private Object minValues[];
	
	/**
	 * An array of max-values
	 * @since 1.0
	 */
	private Object maxValues[];
	
	/**
	 * A matrix of used string values
	 * @since 1.0
	 */
	private String usedStrings[][];
	
	/**
	 * Headings....
	 * @since 1.0
	 */
	private String featureNames[];
	
	/**
	 * Data types in String format
	 * @since 1.0
	 */
	private String featureTypesString[];
	
	/**
	 * Data types in short format
	 * @since 1.0
	 */
	private short featureTypesShort[];
	
	/**
	 * The actual data. To retrieve a case do 
	 * (Feature[]) cases.elementAt(caseNum). To retrieve a feature of a case do 
	 * ((Feature[]) cases.elementAt(caseNum))[featureNum]
	 * 
	 * Stored as cases[case] => Feature[]
	 * @since 1.0
	 */
	private FVector cases;
	
	
	/**
	* Empty constructor
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBRdata()
	{
		this.featureNames = null;
		this.featureTypesString = null;
		this.featureTypesShort = null;
		
		// Initiate the data set
		cases = new FVector();
		dirty = false;
	}
	/**
	* Initiates the data and sets the appropriate feature names
	* 
	* @param featureNames array of names of the features
	* @param featureTypeNames array of strings describing the datatypes of 
	*		the features
	* @throws java.lang.ArrayIndexOutOfBoundsException if the featureNames 
	*		and featureTypeNames don't have the same length
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBRdata(String featureNames[], String featureTypeNames[])
		throws java.lang.ArrayIndexOutOfBoundsException
	{
		// Feature names and types must be specified when creating the data set
		if (featureNames.length != featureTypeNames.length)
		{
			throw new java.lang.ArrayIndexOutOfBoundsException("Feature names and feature types not of the same cardinality");
		}
		this.featureNames = featureNames;
		this.featureTypesString = featureTypeNames;
		this.featureTypesShort = new short[featureTypeNames.length];
		for ( int i = 0 ; i < featureTypeNames.length ; i++ )
		{
			featureTypesShort[i] = Feature.stringToType(featureTypeNames[i]);
		}
		
		// Initiate the data set
		cases = new FVector();
		dirty = true;
	}
	
	
	/**
	* Adds a CBR case to the dataset
	* 
	* @param caseString a tab-separated string describing the case to add
	* @throws java.lang.ArrayIndexOutOfBoundsException if case to add is 
	*		not of the same length as the number of features
	*		previously defined
	* @throws ClassCastException if unable to interpret the described 
	*		feature as specified type
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void addCase(String caseString)
		throws java.lang.ArrayIndexOutOfBoundsException, ClassCastException
	{
		Feature feats[];
		
		dirty = true;
		feats = stringToCaseFeatures(caseString);
		addCase(feats);
	}
	/**
	* Adds a CBR case to the dataset
	* 
	* @param caseObjs an array of Feature:s to add, representing a case
	* @throws java.lang.ArrayIndexOutOfBoundsException if case to add is 
	*		not of the same length as the number of features
	*		previously defined
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void addCase(Feature feats[])
		throws java.lang.ArrayIndexOutOfBoundsException, ClassCastException
	{
		if (feats.length != getNumFeatures())
		{
			throw new java.lang.ArrayIndexOutOfBoundsException("Feature names and feature values not of the same cardinality");
		}
		
		for ( int i = 0 ; i < feats.length ; i++ )
		{
			if (this.featureTypesShort[i] != feats[i].getFeatureType())
			{
				throw new FreeCBR.IllegalTypeException("Trying to add value of incorrect type");
			}
		}
		
		dirty = true;
		cases.addElement(feats);
	}
	
	
	/**
	* Returns the specified case
	* 
	* @param caseNum number of the case to retrieve
	* @return the case specified
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Feature[] getCase(int caseNum)
	{
		return (Feature[]) cases.elementAt(caseNum);
	}
	
	
	/**
	* Replaces a case with another
	* 
	* @param caseNum number of case to edit in case database
	* @param caseString a tab-separated string describing the new case
	* @return string describing the deleted case or null on error
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected String editCase(int caseNum, String caseString)
	{
		dirty = true;
		return caseFeaturesToString((Feature[]) cases.set(caseNum, stringToCaseFeatures(caseString)));
	}
	/**
	* Replaces a case with another
	* 
	* @param caseNum number of case to edit in case database
	* @param feats the features of the new case
	* @return the features of the replaced case
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Feature[] editCase(int caseNum, Feature feats[])
	{
		dirty = true;
		return (Feature[]) cases.set(caseNum, feats);
	}
	
	
	/**
	* Deletes a CBR case from the dataset
	* 
	* @param caseNum number of case to delete from case database
	* @return the deleted case or null on error
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Feature[] deleteCase(int caseNum)
	{
		Feature feats[];
		String caseStr;
		
		if (caseNum < 0 || caseNum >= getNumCases())
		{
			return null;
		}
		
		feats = (Feature[]) cases.elementAt(caseNum);
		dirty = true;
		cases.remove(caseNum);
		return feats;
	}
	
	
	/**
	* Adds a Feature (column) to the dataset. Pretty "expensive".
	* 
	* @param name the name of the new feature
	* @param type the type of the new feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void addFeature(String name, short type)
	{
		Feature newFeat = new Feature(null, type);
		Feature oldCase[];
		Feature newCase[];
		int numFeats = getNumFeatures();
		int numCases = getNumCases();
		String newNames[];
		short newTypesShort[];
		String newTypesString[];
		
		if (numFeats == 0)
		{
			newNames = new String[] {name};
			newTypesShort = new short[] {type};
			newTypesString = new String[] {Feature.typeToString(type)};
		} else
		{
			newNames = new String[numFeats + 1];
			newTypesShort = new short[numFeats + 1];
			newTypesString = new String[numFeats + 1];
			for (int i = 0; i < numFeats; i++)
			{
				newNames[i] = featureNames[i];
				newTypesShort[i] = featureTypesShort[i];
				newTypesString[i] = featureTypesString[i];
			}
			newNames[numFeats] = name;
			newTypesShort[numFeats] = type;
			newTypesString[numFeats] = Feature.typeToString(type);
		}
		
		for (int i = 0; i < numCases; i++)
		{
			newCase = new Feature[numFeats + 1];
			newCase[numFeats] = newFeat;
			oldCase = getCase(i);
			for (int f = 0; f < numFeats; f++)
			{
				newCase[f] = oldCase[f];
			}
			editCase(i, newCase);
		}
		this.featureNames = newNames;
		this.featureTypesShort = newTypesShort;
		this.featureTypesString = newTypesString;
		
		dirty = true;
	}
	
	
	/**
	* Returns the name of the specified feature
	* 
	* @param featureNum the number of the feature whose name to retrieve
	* @return the name of the specified feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected String getFeatureName(int featureNum)
	{
		return featureNames[featureNum];
	}
	
	
	/**
	* Sets the name of the specified feature
	* 
	* @param featureNum the number of the feature whose name to set
	* @param newName name to use
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void setFeatureName(int featureNum, String newName)
	{
		if (featureNum < 0 || featureNum >= getNumFeatures())
		{
			return;
		}
		
		this.featureNames[featureNum] = newName;
	}
	
	
	/**
	* Returns the number of the feature with the specified name
	* 
	* @param featureName the name of the feature whose number to retrieve
	* @return the number of the specified feature
	* @throws NoDataException if the feature is not found
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected int getFeatureNum(String featureName)
	{
		for ( int f = 0 ; f < getNumFeatures() ; f++ )
		{
			if (featureName.compareTo(getFeatureName(f)) == 0)
			{
				return f;
			}
		}
		throw new NoDataException("Feature with the specified name \"" + featureName + "\" was not found");
	}
	
	
	/**
	* Returns the type of the specified feature
	* 
	* @param featureNum the number of the feature whose type to retrieve
	* @return the type of the specified feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected String getFeatureTypeString(int featureNum)
	{
		return featureTypesString[featureNum];
	}
	
	
	/**
	* Returns the type of the specified feature as short
	* 
	* @param featureNum the number of the feature whose type to retrieve
	* @return the type of the specified feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected short getFeatureTypeShort(int featureNum)
	{
		return featureTypesShort[featureNum];
	}
	
	
	/**
	* Change the data type of the specified feature
	* 
	* @param featureNumber the number of the feature to change
	* @param newType the new data type
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void setFeatureType(int featureNumber, short newType)
	{
		Feature feat;
		
		if (featureNumber < 0 || featureNumber >= getNumFeatures())
		{
			return;
		}
		
		featureTypesShort[featureNumber] = newType;
		featureTypesString[featureNumber] = Feature.typeToString(newType);
		
		for (int i = 0; i < getNumCases(); i++)
		{
			try
			{
				setFeature(i, featureNumber, getFeature(i, featureNumber).toString());
			} catch (Exception e)
			{
				setFeature(i, featureNumber, Feature.FEATURE_VALUE_UNDEFINED);
			}
		}
		dirty = true;
	}
	
	
	/**
	* Returns the specified feature
	* 
	* @param caseNum number of the case to retrieve
	* @param featureNum number of the feature to retrieve
	* @return the feature specified
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Feature getFeature(int caseNum, int featureNum)
	{
		return ((Feature[]) cases.elementAt(caseNum))[featureNum];
	}
	
	
	/**
	* Sets the specified feature to the specified value
	* 
	* @param caseNum number of the case to set
	* @param featureNum number of the feature to set
	* @param value the value to use
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void setFeature(int caseNum, int featureNum, String value)
	{
		Feature feats[];
		
		feats = getCase(caseNum);
		feats[featureNum] = new Feature(value, getFeatureTypeShort(featureNum));
		editCase(caseNum, feats);
	}
	
	
	/**
	* Deletes a Feature (column) from the dataset. Pretty "expensive".
	* 
	* @param featureNumber the number of the feature to remove
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void deleteFeature(int featureNumber)
	{
		int numFeats = getNumFeatures();
		int numCases = getNumCases();
		String newNames[];
		short newTypesShort[];
		String newTypesString[];
		Feature oldCase[];
		Feature newCase[];
		
		if (featureNumber < 0 || featureNumber >= numFeats)
		{
			return;
		}
		
		if (numFeats <= 1)
		{
			newNames = null;
			newTypesShort = null;
			newTypesString = null;
		} else
		{
			newNames = new String[numFeats - 1];
			newTypesShort = new short[numFeats - 1];
			newTypesString = new String[numFeats - 1];
			for (int ol=0, n=0; ol < numFeats; ol++)
			{
				if (ol == featureNumber)
					continue;
				newNames[n] = featureNames[ol];
				newTypesShort[n] = featureTypesShort[ol];
				newTypesString[n] = featureTypesString[ol];
				n++;
			}
		}
			
		for (int i = 0; i < numCases; i++)
		{
			newCase = new Feature[numFeats - 1];
			oldCase = getCase(i);
			for (int ol=0, n=0; ol < numFeats; ol++)
			{
				if (ol == featureNumber)
					continue;
				newCase[n++] = oldCase[ol];
			}
			editCase(i, newCase);
		}
		this.featureNames = newNames;
		this.featureTypesShort = newTypesShort;
		this.featureTypesString = newTypesString;
		
		dirty = true;
	}
	
	
	/**
	* Finds the min- and max-values and all used string values for each feature
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected void finishInput()
	{
		int caseNum = cases.size();
		int featureNum;
		Object tmpObject, minObject, maxObject;
		Feature feats[];
		FVector set = new FVector();
		
		if (featureNames == null)
		{
			featureNum = 0;
		} else
		{
			featureNum = featureNames.length;
		}
		if (dirty)
		{
			minValues = new Object[featureNum];
			maxValues = new Object[featureNum];
			usedStrings = new String[featureNum][];
			
			// Loop through all features and find minimum and maximum value
			for (int f = 0 ; f < featureNum ; f++ )
			{
				minObject = null;
				maxObject = null;
				set.clear();
				// Loop through all cases and find minimum and maximum value 
				// for numerical features and find all used string values for 
				// each feature
				for (int c = 0 ; c < caseNum ; c++ )
				{
					if (cases.elementAt(c) == null ||
						((Feature[]) cases.elementAt(c))[f] == null  ||
						(((Feature[]) cases.elementAt(c))[f]).isUndefined() ||
						(((Feature[]) cases.elementAt(c))[f]).value == null)
					{
						continue;
					}
					tmpObject = ((Feature[]) cases.elementAt(c))[f].value;
					
					switch (featureTypesShort[f])
					{
					case Feature.FEATURE_TYPE_FLOAT:
						if (minObject == null ||
							((Double) tmpObject).doubleValue() < ((Double) minObject).doubleValue())
						{
							minObject = tmpObject;
						}
						if (maxObject == null ||
							((Double) tmpObject).doubleValue() > ((Double) maxObject).doubleValue())
						{
							maxObject = tmpObject;
						}
						break;
					
					case Feature.FEATURE_TYPE_INT:
						if (minObject == null ||
							((Long) tmpObject).longValue() < ((Long) minObject).longValue())
						{
							minObject = tmpObject;
						}
						if (maxObject == null ||
							((Long) tmpObject).longValue() > ((Long) maxObject).longValue())
						{
							maxObject = tmpObject;
						}
						break;
						
					case Feature.FEATURE_TYPE_STRING:
						set.add((String) tmpObject);
						break;
						
					case Feature.FEATURE_TYPE_MULTISTRING:
						for ( int j = 0 ; j < ((String[]) tmpObject).length ; j++ )
						{
							set.add(((String[]) tmpObject)[j]);
						}
						break;
					}
				}
				minValues[f] = minObject;
				maxValues[f] = maxObject;
				set.sortAndRemoveDuplicates(new StringComparator());
				usedStrings[f] = new String[set.size()];
				for (int i = 0; i < set.size() ; i++ )
				{
					usedStrings[f][i] = (String) set.elementAt(i);
				}
			}
			dirty = false;
		}
		System.gc();
	}
	
	
	/**
	* Returns number of cases in data set
	* 
	* @return the number of cases in data set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected int getNumCases()
	{
		return cases.size();
	}
	
	
	/**
	* Returns number of features in data set
	* 
	* @return the number of features in data set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected int getNumFeatures()
	{
		if (featureNames == null)
			return 0;
		
		return featureNames.length;
	}
	
	
	/**
	* Returns all strings used in specified feature
	* 
	* @param featureNum number of the feature whose strings to retrieve
	* @return an array with all string values used in this feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected String[] getUsedStrings(int featureNum)
	{
		if (dirty)
		{
			finishInput();
		}
		return usedStrings[featureNum];
	}
	
	
	/**
	* Returns the minimum (numerical) value in specified feature
	* 
	* @param featureNum number of the feature whose minimum value to retrieve
	* @return the minimum value used or null if not available
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Object getMinValue(int featureNum)
	{
		if (dirty)
		{
			finishInput();
		}
		if (minValues == null || 
			minValues[featureNum] == null ||
			minValues.length < featureNum)
		{
			return null;
		}
		return minValues[featureNum];
	}
	
	
	/**
	* Returns the maximum (numerical) value in specified feature
	* 
	* @param featureNum number of the feature whose maximum value to retrieve
	* @return the maximum value used or null if not available
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Object getMaxValue(int featureNum)
	{
		if (dirty)
		{
			finishInput();
		}
		if (maxValues == null || 
			maxValues[featureNum] == null ||
			maxValues.length < featureNum)
		{
			return null;
		}
		return maxValues[featureNum];
	}
	
	
	/**
	* Converts a string to array of Feature:s
	* 
	* @param caseString the string to convert
	* @return array of Feature:s retrieved from the string
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected Feature[] stringToCaseFeatures(String caseString)
	{
		String featureStrings[];
		Feature feats[];
		
		// Transform the string to an array of strings
		featureStrings = FString.split(caseString, "\t");
		
		feats = new Feature[featureStrings.length];
		
		// Take care of all data types
		for ( int i = 0 ; i < featureStrings.length ; i++ )
		{
			feats[i] = new Feature(featureStrings[i], featureTypesShort[i]);
		}
		return feats;
	}
	
	
	/**
	* Converts an array of Feature:s to a string
	* 
	* @param feats the Feature:s to convert to a tab-separated string
	* @return tab-separated string representing the Feature:s
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected static String caseFeaturesToString(Feature feats[])
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0 ; i < feats.length ; i++ )
		{
			if (i > 0)
			{
				sb.append("\t");
			}
			
			switch (feats[i].getFeatureType())
			{
			case Feature.FEATURE_TYPE_BOOL:
				if (feats[i].getBoolValue())
				{
					sb.append("1");
				} else
				{
					sb.append("0");
				}
				break;
				
			case Feature.FEATURE_TYPE_FLOAT:
				sb.append(feats[i].getFloatValue());
				break;
				
			case Feature.FEATURE_TYPE_INT:
				sb.append(feats[i].getIntValue());
				break;
				
			case Feature.FEATURE_TYPE_MULTISTRING:
				sb.append(Feature.multiStringJoin(feats[i].getMultiStringValue()));
				break;
				
			case Feature.FEATURE_TYPE_STRING:
				sb.append(feats[i].getStringValue());
				break;
			}
		}
		return sb.toString();
	}
}


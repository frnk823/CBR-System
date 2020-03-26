/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

/**
 * This class represents a feature defined by a feature type and a value.
 *
 * @since 1.0
 *
 *
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
public class Feature implements java.io.Serializable
{
	/**
	 * Contains the feature value
	 */
	protected Object value;
	
	/**
	 * Contains the feature type
	 */
	private short featureType;
	
	/**
	 * Is this feature undefined? Default no.
	 */
	private boolean undefined=false;
	
	
	/**
	 * The feature is undefined or not applicable
	 * @since 1.0
	 */
	public static final String FEATURE_VALUE_UNDEFINED = "?";
	
	
	/**
	 * The feature is of type String
	 * @since 1.0
	 */
	public static final short FEATURE_TYPE_STRING = 0;
	
	/**
	 * The feature is of type MultiString
	 * @since 1.0
	 */
	public static final short FEATURE_TYPE_MULTISTRING = 1;
	
	/**
	 * The feature is of type Int
	 * @since 1.0
	 */
	public static final short FEATURE_TYPE_INT = 2;
	
	/**
	 * The feature is of type Float
	 * @since 1.0
	 */
	public static final short FEATURE_TYPE_FLOAT = 3;
	
	/**
	 * The feature is of type Bool
	 * @since 1.0
	 */
	public static final short FEATURE_TYPE_BOOL = 4;
	
	
	
	/**
	* Creates a feature of type String and initiates it
	* 
	* @param value the String to initiate the feature with
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature(String value)
	{
		if (value == null || value.equals(this.FEATURE_VALUE_UNDEFINED))
		{
			this.value = null;
			this.undefined = true;
		} else
		{
			this.value = value;
		}
		this.featureType = this.FEATURE_TYPE_STRING;
	}
	/**
	* Creates a feature of type MultiString and initiates it
	* 
	* @param value the String array to initiate the feature with
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature(String value[])
	{
		if (value == null || 
				value.length == 0 ||
				value.length == 1 && value[0].equals(this.FEATURE_VALUE_UNDEFINED))
		{
			this.value = null;
			this.undefined = true;
		} else
		{
			this.value = value;
		}
		this.featureType = this.FEATURE_TYPE_MULTISTRING;
	}
	/**
	* Creates a feature of type Int and initiates it
	* 
	* @param value the integer to initiate the feature with
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature(long value)
	{
		this.value = new Long(value);
		this.featureType = this.FEATURE_TYPE_INT;
	}
	/**
	* Creates a feature of type Float and initiates it
	* 
	* @param value the floating point to initiate the feature with
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature(double value)
	{
		this.value = new Double(value);
		this.featureType = this.FEATURE_TYPE_FLOAT;
	}
	/**
	* Creates a feature of type Bool and initiates it
	* 
	* @param value the boolean to initiate the feature with
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature(boolean value)
	{
		this.value = new Boolean(value);
		this.featureType = this.FEATURE_TYPE_BOOL;
	}
	/**
	* Creates a feature of specified type from a String
	* 
	* @param value the value of the feature. If empty string then feature
	*		will be of type FEATURE_VALUE_UNDEFINED
	* @param type the datatype of the feature
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Feature(String value, short type)
	{
		String mstr[];
		
		this.featureType = type;
		
		// First look for special values
		if (value == null ||
				value.length() == 0 || 
				value.length() == 1 &&
				value.equalsIgnoreCase(FEATURE_VALUE_UNDEFINED))
		{
			this.value = null;
			this.undefined = true;
		} else
		{
			// then look for "normal" values
			switch (type)
			{
			case FEATURE_TYPE_BOOL:
				if (value.equalsIgnoreCase("0") || 
					value.equalsIgnoreCase("false"))
				{
					this.value = new Boolean(false);
				} else if (value.equalsIgnoreCase("1") || 
						   value.equalsIgnoreCase("true"))
				{
					this.value = new Boolean(true);
				} else
				{
					throw new ClassCastException("Unable to interpret string \"" + value + "\" as boolean");
				}
				break;
						
			case Feature.FEATURE_TYPE_FLOAT:
				// If decimal comma is used, replace it with decimal point
				this.value = new Double(value.replace(',', '.'));
				break;
						
			case Feature.FEATURE_TYPE_INT:
				this.value = new Long(value);
				break;
						
			case Feature.FEATURE_TYPE_MULTISTRING:
				mstr = multiStringSplit(value);
				if (mstr == null || 
						mstr.length == 0 || 
						mstr.length == 1 && mstr[0].equals(this.FEATURE_VALUE_UNDEFINED))
				{
					this.undefined = true;
					this.value = null;
				} else
				{
					this.value = mstr;
				}
				break;
						
			case Feature.FEATURE_TYPE_STRING:
				if (value.equals(this.FEATURE_VALUE_UNDEFINED))
				{
					this.undefined = true;
					this.value = null;
				} else
				{
					this.value = value;
				}
				break;
			}
		}
	}
	
	
	/**
	* Tests if two Features are equal. Overrides the default Object implementation
	* 
	* @param obj the reference object with which to compare
	* @return true if this object is the same as the obj argument; false otherwise
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		
		if (obj.getClass() != Feature.class)
		{
			return false;
		}
		
		if (featureType != ((Feature) obj).getFeatureType())
		{
			return false;
		}
		
		// I am not sure if this is neccessary
		if (featureType == FEATURE_TYPE_MULTISTRING)
		{
			// Should perhaps compare the contents instead - if the values are not stored in the same order
			return FArrays.equals((String[]) value, (String[])((Feature) obj).getValue());
		} else
		{
			return value.equals(((Feature) obj).getValue());
		}
	}
	
	
	/**
	* Is this feature undefined?
	* 
	* @return true if the feature is undefined, otherwise false
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean isUndefined()
	{
		return undefined;
	}
	
	
	/**
	* Returns the type of this feature
	* 
	* @return the feature type
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public short getFeatureType()
	{
		return featureType;
	}
	
	
	/**
	* Returns the feature value if the type is String. Otherwise throws an exception
	* 
	* @return the String value
	* @throws IllegalTypeException if the feature is not a String
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String getStringValue()
	{
		if (this.featureType != this.FEATURE_TYPE_STRING)
		{
			throw new IllegalTypeException("Trying to retrieve String value from non-String feature.");
		}
		if (isUndefined())
		{
			return this.FEATURE_VALUE_UNDEFINED;
		}
		return (String) value;
	}


	/**
	* Returns the feature value if the type is MultiString. Otherwise throws an exception
	* 
	* @return the MultiString value
	* @throws IllegalTypeException if the feature is not a MultiString
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String[] getMultiStringValue()
	{
		if (this.featureType != this.FEATURE_TYPE_MULTISTRING)
		{
			throw new IllegalTypeException("Trying to retrieve MultiString value from non-MultiString feature.");
		}
		return (String[]) value;
	}
	
	
	/**
	* Returns the feature value if the type is Int. Otherwise throws an exception
	* 
	* @return the Int value
	* @throws IllegalTypeException if the feature is not an Int
	* @throws NoDataException if the value is undefined
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public long getIntValue()
	{
		if (this.featureType != this.FEATURE_TYPE_INT)
		{
			throw new IllegalTypeException("Trying to retrieve Int value from non-Int feature.");
		}
		if (this.undefined)
		{
			throw new NoDataException("Value is undefined");
		}
		return ((Long) value).longValue();
	}
	
	
	/**
	* Returns the feature value if the type is Float. Otherwise throws an exception
	* 
	* @return the Float value
	* @throws IllegalTypeException if the feature is not a Float
	* @throws NoDataException if the value is undefined
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public double getFloatValue()
	{
		if (this.featureType != this.FEATURE_TYPE_FLOAT)
		{
			throw new IllegalTypeException("Trying to retrieve Float value from non-Float feature.");
		}
		if (this.undefined)
		{
			throw new NoDataException("Value is undefined");
		}
		return ((Double) value).doubleValue();
	}
	
	
	/**
	* Returns the feature value if the type is Bool. Otherwise throws an exception
	* 
	* @return the Bool value
	* @throws IllegalTypeException if the feature is not a Bool
	* @throws NoDataException if the value is undefined
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public boolean getBoolValue()
	{
		if (this.featureType != this.FEATURE_TYPE_BOOL)
		{
			throw new IllegalTypeException("Trying to retrieve Bool value from non-Bool feature.");
		}
		if (this.undefined)
		{
			throw new NoDataException("Value is undefined");
		}
		return ((Boolean) value).booleanValue();
	}
	
	
	/**
	* Returns the feature value casted to Object whatever the actual type is
	* 
	* @return the feature value
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public Object getValue()
	{
		return value;
	}
	
	
	/**
	* Returns a string representing the instance
	* 
	* @return a string
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public String toString()
	{
		if (featureType == FEATURE_TYPE_MULTISTRING)
		{
			return undefined ? "?" : multiStringJoin((String[]) value);
		} else
		{
			return undefined ? "?" : getValue().toString();
		}
	}
	
	
	/**
	* Converts a string representing a type to the type
	* 
	* @param typeString the string that will be translated to a type, 
	*		for example "String"
	* @return the type that the parameter represents (in the example 
	*		Feature.STRING_FEATURE)
	* @throws IllegalTypeException if no valid type is found
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public static short stringToType(String typeString)
	{
		if (typeString.equalsIgnoreCase("String"))
			return FEATURE_TYPE_STRING;
		else if (typeString.equalsIgnoreCase("MultiString"))
			return FEATURE_TYPE_MULTISTRING;
		else if (typeString.equalsIgnoreCase("Int"))
			return FEATURE_TYPE_INT;
		else if (typeString.equalsIgnoreCase("Float"))
			return FEATURE_TYPE_FLOAT;
		else if (typeString.equalsIgnoreCase("Bool"))
			return FEATURE_TYPE_BOOL;
		else
			throw new IllegalTypeException("No valid type found in string \"" + typeString + "\"");
	}
	
	
	/**
	* Converts a type to a string representing the type
	* 
	* @param type the type that shall be converted, for example 
	*		Feature.STRING_FEATURE
	* @return a string representing the type, for example "String"
	* @throws IllegalTypeException if no valid type is found
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public static String typeToString(short type)
	{
		switch (type)
		{
		case FEATURE_TYPE_STRING:
			return "String";
			
		case FEATURE_TYPE_MULTISTRING:
			return "MultiString";
			
		case FEATURE_TYPE_INT:
			return "Int";
			
		case FEATURE_TYPE_FLOAT:
			return "Float";
			
		case FEATURE_TYPE_BOOL:
			return "Bool";
			
		default:
			throw new IllegalTypeException("This type does is unvalid: \"" + type + "\"");
		}
	}
	
	
	/**
	* Splits a "MultiString" to an array of String:s
	* 
	* @param mstring the MultiString
	* @return array of strings retrieved from the mstring
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected static String[] multiStringSplit(String mstring)
	{
		boolean doubleSemi = false;
		String tmpMultiString[];
		Object tmpObjs[];
		FVector multiStringVec;
		
		// Convert an empty string to "?"
		if (mstring == null || mstring.length() == 0)
		{
			tmpMultiString = new String[1];
			tmpMultiString[0] = Feature.FEATURE_VALUE_UNDEFINED;
			return tmpMultiString;
		}
		// Check if the string is surrounded by quotation marks ("). Excel inserts these 
		// when saving as tab separated values if there are semi colons in the cell
		if (mstring.indexOf(';') > 0 && mstring.startsWith("\"") && mstring.endsWith("\"")) {
			if (mstring.length() <= 2 || mstring.charAt(1) == '\"' || mstring.charAt(mstring.indexOf(';') - 1) != '\"')
				mstring = mstring.substring(1, mstring.length() - 1);
		}
		
		tmpMultiString = FString.split(mstring, ";");
		multiStringVec = new FVector(tmpMultiString.length);
		
		// There might be ";;"s, in that case there are empty 
		// strings in the array. These strings means there was a ";;"
		for ( int i = 0 ; i < tmpMultiString.length ; i++ )
		{
			if (tmpMultiString[i].compareTo("") != 0)
			{
				// No ";;"
				// If it starts and ends with double quotation marks ("") then it is probably an Excel string that should have only only quotation mark
				if (tmpMultiString[i].length() > 4 && tmpMultiString[i].startsWith("\"\"") && tmpMultiString[i].endsWith("\"\""))
					tmpMultiString[i] = tmpMultiString[i].substring(1, tmpMultiString[i].length() - 1);
				// Was the previous string a double semicolon?
				if (doubleSemi)
				{
					// then add this string
					((String) multiStringVec.lastElement()).concat(tmpMultiString[i]);
					doubleSemi = false;
				} else
				{
					multiStringVec.addElement(tmpMultiString[i]);
				}
			} else
			{
				// A ";;" is found
				doubleSemi = true;
				if (multiStringVec.size() > 0)
				{
					// Add ";" to last string
					((String) multiStringVec.lastElement()).concat(";");
				} else
				{
					// This is the first string and it begins with a ";"
					multiStringVec.addElement(new String(";"));
				}
			}
		}
		
		tmpObjs = multiStringVec.toArray();
		tmpMultiString = new String[tmpObjs.length];
		for (int i = 0 ; i < tmpObjs.length ; i++ )
		{
			tmpMultiString[i] = (String) tmpObjs[i];
		}
		return tmpMultiString;
	}
	
	
	/**
	* Creates a "MultiString" from an array of strings
	* 
	* @param strings array of strings to convert
	* @return the resulting MultiString
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	protected static String multiStringJoin(String strings[])
	{
		StringBuffer sb = new StringBuffer();
		
		if (strings == null)
		{
			return "";
		}
		for (int i = 0 ; i < strings.length ; i++ )
		{
			sb.append(FString.replaceAll(strings[i], ";", ";;"));
			if (i < strings.length - 1)
				sb.append(";");
		}
		return sb.toString();
	}
}


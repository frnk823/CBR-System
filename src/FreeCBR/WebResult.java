/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

/**
 * This class contains results from CBR web queries. WebResult.result.length 
 *	tells the number of result cases of the search. Typically the 
 *	following loops through all results:<br />
 *	<code>WebResult wr = cbr.search(req);<br />
 *	for (int i = 0; i < wr.result.length; i++)<br />
 *	{<br />
 *	 	System.out.println(wr.result[i].toString());<br />
 *	}</code>
 *
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
public class WebResult
{
	/**
	 * The getParameterValues function
	 */
	private static java.lang.reflect.Method functionGetParameterValues = null;
	
	/**
	 * The getParameter function
	 */
	private static java.lang.reflect.Method functionGetParameter = null;
	
	/**
	 * Parameter type (always the same)
	 */
	private static Class paramTypes[] = {String.class};
	
	/**
	 * Parameter array (always the same size, 1)
	 */
	private static Object args[] = {null};
	
	/**
	 * The result of the search
	 */
	public CBRResult result[];
	
	/**
	 * The searched values (in String format)
	 */
	public Feature searchValues[];
	
	/**
	 * The searched weights
	 */
	public int searchWeights[];
	
	/**
	 * The types of the search
	 */
	public int searchTerms[];
	
	/**
	 * The scale of the search
	 */
	public int searchScales[];
	
	/**
	 * The distances of the search
	 */
	public int searchOptions[];
	
	/**
	 * The feature search string prefix
	 */
	public final static String FEATURE_PREFIX = "feat";
	
	/**
	 * The search weight string prefix
	 */
	public final static String WEIGHT_PREFIX = "weight";
	
	/**
	 * The search type string prefix
	 */
	public final static String TERM_PREFIX = "term";
	
	/**
	 * The search scale string prefix
	 */
	public final static String SCALE_PREFIX = "scale";
	
	/**
	 * The search distance string prefix
	 */
	public final static String OPTION_PREFIX = "option";
	
	
	/**
	* Creates a Result and initiates it
	* 
	* @param cbr the CBR that contains the data and can perform the actual search
	* @param req the query in web style. MultiString values may use 
	*			<code>feat<i>i</i>=<i>A</i>&feat<i>i</i>=<i>B</i>...</code> or
	*			<code>feat<i>i</i>=<i>A</i>;<i>B</i>...</code>.
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public WebResult(CBR cbr, Object req) // Really javax.servlet.http.HttpServletRequest
		throws Exception
	{
		int numParams;
		short featType;
		String param, value;
		String mvalue[];
		String tmpArr[];
		int searchNumbers[];
		FVector strvec = new FVector();
		
		this.initialize();
		
		// If no query, then no result
		if (req == null)
		{
			return;
		}
		
		try
		{
			// Find methods
			if (functionGetParameterValues == null)
			{
				functionGetParameterValues = req.getClass().getMethod("getParameterValues", paramTypes);
			}
			if (functionGetParameter == null)
			{
				functionGetParameter = req.getClass().getMethod("getParameter", paramTypes);
			}
			// Initiate all parameters
			numParams = cbr.getNumFeatures();
			searchNumbers = new int[numParams];
			for (int i = 0; i < numParams; i++ )
			{
				searchNumbers[i] = i;
			}
			this.searchValues = new Feature[numParams];
			FArrays.fill(searchValues, null);
			this.searchWeights = new int[numParams];
			FArrays.fill(searchWeights, 5);
			this.searchTerms = new int[numParams];
			FArrays.fill(searchTerms, 0);
			this.searchScales = new int[numParams];
			FArrays.fill(searchScales, 0);
			this.searchOptions = new int[numParams];
			FArrays.fill(searchOptions, 0);
			
			// Go get parameter values
			for (int i = 0; i < numParams; i++ )
			{
				// Search value
				featType = cbr.getFeatureType(i);
				if (featType == Feature.FEATURE_TYPE_MULTISTRING)
				{
					// MultiString
					strvec.clear();
					args[0] = this.FEATURE_PREFIX + i;
					mvalue = (String[]) functionGetParameterValues.invoke(req, args);
					if (mvalue != null)
					{
						for (int j = 0; j < mvalue.length; j++)
						{
							if (mvalue[j].compareTo(Feature.FEATURE_VALUE_UNDEFINED) != 0)
							{
								tmpArr = Feature.multiStringSplit(mvalue[j]);
								for (int k = 0; k < tmpArr.length; k++)
								{
									strvec.addElement(tmpArr[k]);
								}
							}
						}
					}
					if (strvec.size() == 0)
					{
						searchValues[i] = new Feature(null, featType);	// Undefined
					} else
					{
						searchValues[i] = new Feature((String[]) strvec.toArray(String.class));
					}
				} else
				{
					// Not MultiString
					args[0] = this.FEATURE_PREFIX + i;
					value = (String) functionGetParameter.invoke(req, args);
					searchValues[i] = new Feature(value, featType);
				}
				// Search weight
				args[0] = this.WEIGHT_PREFIX + i;
				value = (String) functionGetParameter.invoke(req, args);
				if (value != null)
					searchWeights[i] = Integer.parseInt(value);
				// Search term
				args[0] = this.TERM_PREFIX + i;
				value = (String) functionGetParameter.invoke(req, args);
				if (value != null)
					searchTerms[i] = Integer.parseInt(value);
				// Search scale
				args[0] = this.SCALE_PREFIX + i;
				value = (String) functionGetParameter.invoke(req, args);
				if (value != null)
					searchScales[i] = Integer.parseInt(value);
				// Search options
				args[0] = this.OPTION_PREFIX + i;
				value = (String) functionGetParameter.invoke(req, args);
				if (value != null)
					searchOptions[i] = searchOptions[i] | Integer.parseInt(value);
			}
			result = cbr.search(searchNumbers, searchValues, searchWeights, searchTerms, searchScales, searchOptions);
		} catch (Exception e)
		{
			throw new NoSuchMethodException("Servlet.jar probably not in classpath, or wrong version");
		}
	}
	
	
	/**
	* Initializes the member properties
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	private void initialize()
	{
		this.result = null;
		this.searchOptions = null;
		this.searchScales = null;
		this.searchTerms = null;
		this.searchValues = null;
		this.searchWeights = null;
	}
}


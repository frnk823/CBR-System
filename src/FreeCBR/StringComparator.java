/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

/**
 * This class contains a comparator for results from CBR queries
 *
 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class StringComparator implements FComparator
{
	/**
	* Empty constructor
	* 
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public StringComparator()
	{
	}
	
	
	/**
	* compares two Strings
	* 
	* @param o1 the first object to be compared.
	* @param o2 the second object to be compared.
	* @return a negative integer, zero, or a positive integer as 
	*		the first argument is less than, equal to, or greater than the second.
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public int compare(Object o1, Object o2)
	{
		return ((String) o1).compareTo((String) o2);
	}
}

/*
	Placed in public domain by Lars Johanson, 2005. Share and enjoy!
*/

package FreeCBR;

/**
 * This class is more or less a copy of java.util.Comparator that exists in Java 1.2
 *
 * @since 1.1.2
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
interface FComparator
{
    public int compare(Object o1, Object o2);
	
	public boolean equals(Object obj);
}

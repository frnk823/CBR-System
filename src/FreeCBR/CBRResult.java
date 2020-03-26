/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

/**
 * This class contains results from CBR queries
 *

 * @since 1.0
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
public class CBRResult
{
	/**
	 * Case number
	 * @since 1.0
	 */
	public final int caseNum;
	
	/**
	 * Percent match
	 * @since 1.0
	 */
	public final double matchPercent;
	
	
	/**
	* Creates a Result and initiates it
	* 
	* @param caseNum the case number to set
	* @param matchPercent the percentage to set
	* @since 1.0
	*/
	/* History: Date		Name	Explanation (possibly multi row)
	*/
	public CBRResult(int caseNum, double matchPercent)
	{
		this.caseNum = caseNum;
		this.matchPercent = matchPercent;
	}
}

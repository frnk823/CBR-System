/*
	Placed in public domain by Lars Johanson, 2003. Share and enjoy!
*/

package FreeCBR;

/**
 * This exception is throwed when trying to retrieve values of incorrect 
 * type from a property
 */
class NoDataException extends java.lang.RuntimeException
{
	public NoDataException(String message)
	{
		super(message);
	}
}

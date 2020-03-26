/*
	Placed in public domain by Lars Johanson, 2005. Share and enjoy!
*/

package FreeCBR;

/**
 * This class is an extension to Java 1.1 String, adding a couple of functions available in Java 1.2
 *
 * @since 1.1.2
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class FString
{
	/**
	 * Method for String.split(regex) if available, otherwise null
	 */
	private static java.lang.reflect.Method methodSplit = null;
	
	/**
	 * Method for String.replaceAll(regex, replacement) if available, otherwise null
	 */
	private static java.lang.reflect.Method methodReplaceAll = null;
	
	/**
	 * Just for initializing
	 */
	private FString fs = new FString();
	
	/**
	 * Constructor
	 */
	FString()
	{
		if (true) throw new RuntimeException("FString constructor");////
		Class paramTypes[];
		
		// Get split method
/*///		try
		{
			paramTypes = new Class[1];
			paramTypes[0] = String.class;
			methodSplit =  String.class.getMethod("split", paramTypes);
		} catch (Exception e) *///
		{
			methodSplit = null;
		}
		// Get replaceAll method
/*///		try
		{
			paramTypes = new Class[2];
			paramTypes[0] = String.class;
			paramTypes[1] = String.class;
			methodReplaceAll =  String.class.getMethod("replaceAll", paramTypes);
		} catch (Exception e) *///
		{
			methodReplaceAll = null;
		}
	}
	
	/**
	 * Split
	 */
	public static String[] split(String str, String s)
	{
		int pos1, pos2;
		int sz = s.length();
		FVector v = new FVector();
		String[] ret;
		Object args[] = {null};
		
		try
		{
			if (methodSplit == null)
			{
				pos1 = 0;
				pos2 = str.indexOf(s);
				while (pos2 > 0)
				{
					v.addElement(str.substring(pos1, pos2));
					pos1 = pos2 + sz;
					pos2 = str.indexOf(s, pos1);
				}
				v.addElement(str.substring(pos1));
				ret = (String[]) v.toArray(String.class);
				return ret;
			} else
			{
				args[0] = s;
				return (String[]) methodSplit.invoke(str, args);
			}
		} catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * replaceAll
	 */
	public static String replaceAll(String str, String a, String b)
	{
		int pos1, pos2;
		int sz = a.length();
		StringBuffer sb = new StringBuffer();
		Object args[] = {null, null};
		
		try
		{
			if (methodReplaceAll == null)
			{
				pos1 = 0;
				pos2 = str.indexOf(a);
				while (pos2 > 0)
				{
					sb.append(str.substring(pos1, pos2)).append(b);
					pos1 = pos2 + sz;
					pos2 = str.indexOf(a, pos1);
				}
				sb.append(str.substring(pos1));
				return sb.toString();
			} else
			{
				args[0] = a;
				args[1] = b;
				return (String) methodReplaceAll.invoke(str, args);
			}
		} catch (Exception e)
		{
			return null;
		}
	}
}

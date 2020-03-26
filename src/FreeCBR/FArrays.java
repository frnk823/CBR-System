/*
	Placed in public domain by Lars Johanson, 2005. Share and enjoy!
*/

package FreeCBR;

/**
 * This class contains some functions regarding arrays that exist in Java 1.2
 *
 * @since 1.1.2
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class FArrays
{
    public static void sort(Object aobj[])
    {
		sort(aobj, new OComparator());
    }
	
    public static void sort(Object obj[], FComparator comparator)
    {
        Object obj1[] = (Object[])obj.clone();
        if(comparator != null)
            mergeSort(obj1, obj, 0, obj.length, 0, comparator);
    }
	
    private static void mergeSort(Object obj1[], Object obj2[], int i, int j, int k, FComparator comparator)
    {
        int l = j - i;
        if(l < 7)
        {
            for(int i1 = i; i1 < j; i1++)
            {
                for(int k1 = i1; k1 > i && comparator.compare(obj2[k1 - 1], obj2[k1]) > 0; k1--)
				{
                    swap(obj2, k1, k1 - 1);
				}
            }
            return;
        }
        int j1 = i;
        int l1 = j;
        i += k;
        j += k;
        int i2 = i + j >> 1;
        mergeSort(obj2, obj1, i, i2, -k, comparator);
        mergeSort(obj2, obj1, i2, j, -k, comparator);
        if(comparator.compare(obj1[i2 - 1], obj1[i2]) <= 0)
        {
            System.arraycopy(((Object) (obj1)), i, ((Object) (obj2)), j1, l);
            return;
        }
        int j2 = j1;
        int k2 = i;
        int l2 = i2;
        for(; j2 < l1; j2++)
            if(l2 >= j || k2 < i2 && comparator.compare(obj1[k2], obj1[l2]) <= 0)
                obj2[j2] = obj1[k2++];
            else
                obj2[j2] = obj1[l2++];

    }
	
    private static void swap(Object aobj[], int i, int j)
    {
        Object obj = aobj[i];
        aobj[i] = aobj[j];
        aobj[j] = obj;
    }
	
    public static boolean equals(Object abyte0[], Object abyte1[])
    {
        if(abyte0 == abyte1)
            return true;
        if(abyte0 == null || abyte1 == null)
            return false;
        int i = abyte0.length;
        if(abyte1.length != i)
            return false;
        for(int j = 0; j < i; j++)
            if(abyte0[j] != abyte1[j])
                return false;

        return true;
    }
	
    public static void fill(Object aobj[], Object obj)
    {
        fill(aobj, 0, aobj.length, obj);
    }
	
    public static void fill(Object aobj[], int i, int j, Object obj)
    {
        rangeCheck(aobj.length, i, j);
        for(int k = i; k < j; k++)
            aobj[k] = obj;

    }
	
    public static void fill(int ai[], int i)
    {
        fill(ai, 0, ai.length, i);
    }

    public static void fill(int ai[], int i, int j, int k)
    {
        rangeCheck(ai.length, i, j);
        for(int l = i; l < j; l++)
            ai[l] = k;

    }
	
    private static void rangeCheck(int i, int j, int k)
    {
        if(j > k)
            throw new IllegalArgumentException("fromIndex(" + j + ") > toIndex(" + k + ")");
        if(j < 0)
            throw new ArrayIndexOutOfBoundsException(j);
        if(k > i)
            throw new ArrayIndexOutOfBoundsException(k);
        else
            return;
    }
}

class OComparator implements FComparator
{
    public int compare(Object o1, Object o2)
	{
		return o1.hashCode() - o2.hashCode();
	}
}
/*
	Placed in public domain by Lars Johanson, 2005. Share and enjoy!
*/

package FreeCBR;

/**
 * This class is an extension to Java 1.1 Vector, adding a couple of functions available in Java 1.2
 * Also added function "sort(FComparator)" and "sortRemoveDuplicates()"
 *
 * @since 1.1.2
 * 
 */
/* History: Date		Name	Explanation (possibly multi row)
 */
class FVector extends java.util.Vector
{
    public FVector()
    {
		super();
    }
	
    public FVector(int i)
    {
		super(i, 0);
    }
	
    public void clear()
    {
        removeAllElements();
    }
	
	/**
	 * Allows the caller to specify the type of the elements in the returned array
	 */
    public synchronized Object[] toArray(Class targetType)
    {
		Object aobj = java.lang.reflect.Array.newInstance( targetType, elementCount );
        System.arraycopy(((Object) (elementData)), 0, ((Object) (aobj)), 0, elementCount);
        return (Object[]) aobj;
    }
	
    public synchronized Object[] toArray()
    {
        Object aobj[] = new Object[elementCount];
        System.arraycopy(((Object) (elementData)), 0, ((Object) (aobj)), 0, elementCount);
        return aobj;
    }
	
    public synchronized Object set(int i, Object obj)
    {
        if(i >= elementCount)
        {
            throw new ArrayIndexOutOfBoundsException(i);
        } else
        {
            Object obj1 = elementData[i];
            elementData[i] = obj;
            return obj1;
        }
    }
	
    public synchronized boolean add(Object obj)
    {
///        modCount++;
        ensureCapacityHelper(elementCount + 1);
        elementData[elementCount++] = obj;
        return true;
    }
	
    public synchronized Object remove(int i)
    {
		Object obj;
		
		obj = elementAt(i);
		removeElementAt(i);
		return obj;
    }
	
    private void ensureCapacityHelper(int i)
    {
        int j = elementData.length;
        if(i > j)
        {
            Object aobj[] = elementData;
            int k = capacityIncrement <= 0 ? j * 2 : j + capacityIncrement;
            if(k < i)
                k = i;
            elementData = new Object[k];
            System.arraycopy(((Object) (aobj)), 0, ((Object) (elementData)), 0, elementCount);
        }
    }
	
	/*                     */
	/* Added functionality */
	/*                     */
    public FVector sort()
    {
		trimToSize();
		FArrays.sort(elementData);
		return this;
    }
	
    public FVector sort(FComparator comparator)
    {
		trimToSize();
		FArrays.sort(elementData, comparator);
		return this;
    }
	
    public FVector sortAndRemoveDuplicates()
    {
		sort();
		for (int i = size() - 1; i > 0; i--)
		{
			if (elementAt(i).equals(elementAt(i - 1)))
			{
				removeElementAt(i);
			}
		}
		return this;
    }
    public FVector sortAndRemoveDuplicates(FComparator comparator)
    {
		sort(comparator);
		for (int i = size() - 1; i > 0; i--)
		{
			if (comparator.compare(elementAt(i), elementAt(i - 1)) == 0)
			{
				removeElementAt(i);
			}
		}
		return this;
    }
}

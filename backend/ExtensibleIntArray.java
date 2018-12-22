package backend;

public class ExtensibleIntArray
{
	private int[] array;
	
	public ExtensibleIntArray()
	{
		array = new int[0];
	}
	
	//Add an int
	public void add(int toAdd)
	{
		//Create a new int array that has one more index than the original array
		int[] newArray = new int[array.length + 1];
		
		//Complete the new array up to and including the new value
		int i = 0;
		boolean intAdded = false;
		for( ; i < array.length; i++)
		{
			if(array[i] < toAdd)
				newArray[i] = array[i];
			else if(array[i] == toAdd)
			{
				System.out.println("Value to add to ExtensibleIntArray was already present: " + toAdd);
				return;
			}
			else
			{
				newArray[i] = toAdd;
				intAdded = true;
				break;
			}
		}
		//For each remaining value in the array...
		for( ; i < array.length; i++)
		{
			newArray[i+1] = array[i];
		}
		//If the int has still not been added...
		if(!intAdded)
			//Add it to the end of the array
			newArray[i] = toAdd;
			
		//Set the old array's pointer to point at the new array
		array = newArray;
	}
	
	//Remove all instances of a single int
	public void remove(int toRemove)
	{
		int newArrayLength = 0;
		
		//For each int in the old array...
		for(int key : array)
		{
			if(key != toRemove)
				newArrayLength++;
		}
		
		int[] tempArray = new int[newArrayLength];
		int lastIndex = 0;
		
		//For each int in the old array...
		for(int key : array)
		{
			if(key != toRemove)
			{
				tempArray[lastIndex] = key;
				lastIndex++;
			}
		}
		
		//Set the old array's pointer to point at the new array
		array = tempArray;
	}
	
	public int[] getArray()
	{
		return array;
	}
}
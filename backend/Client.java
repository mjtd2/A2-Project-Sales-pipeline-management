package backend;

public class Client
{
	private int iD;
	private ExtensibleIntArray currentLeadIDs;
	private ExtensibleIntArray historicalLeadIDs;
	private String mName;
	private String mPhoneNumber;
	private String mEmailAddress;
	ExtensibleIntArray mNewCurrentLeadIDs;
	ExtensibleIntArray mNewHistoricalLeadIDs;
	
	//Constructor for loading from file to memory
	public Client(int iD, String name, String phoneNumber, String emailAddress, Memory memory)
	{
		//Initialise variables
		this.iD = iD;
		mName = name;
		mPhoneNumber = phoneNumber;
		mEmailAddress = emailAddress;
		currentLeadIDs = memory.findCurrentLeadIDs(iD);
		mNewCurrentLeadIDs = new ExtensibleIntArray();
		historicalLeadIDs = memory.findHistoricalLeadIDs(iD);
		mNewHistoricalLeadIDs = new ExtensibleIntArray();
	}
	
	//Normal constructor
	public Client(int iD, String name, String phoneNumber, String emailAddress)
	{
		//Initialise variables
		this.iD = iD;
		mName = name;
		mPhoneNumber = phoneNumber;
		mEmailAddress = emailAddress;
		currentLeadIDs = new ExtensibleIntArray();
		mNewCurrentLeadIDs = new ExtensibleIntArray();
		historicalLeadIDs = new ExtensibleIntArray();
		mNewHistoricalLeadIDs = new ExtensibleIntArray();
	}
	
	public boolean hasCurrentLeadKey(int key)
	{
		for(int currentKey : currentLeadIDs.getArray())
		{
			if(currentKey == key)
				return true;
		}
		return false;
	}
	
	public void removeCurrentLeadKey(int key)
	{
		currentLeadIDs.remove(key);
	}
	
	public void prepareToAddCurrentLeadKey(int key)
	{
		mNewCurrentLeadIDs.add(key);
	}
	
	public void addCurrentLeadKeys()
	{
		for(int key : mNewCurrentLeadIDs.getArray())
			currentLeadIDs.add(key);
			
		mNewCurrentLeadIDs = new ExtensibleIntArray();
	}
	
	public void immediatelyAddCurrentLeadKey(int key)
	{
		currentLeadIDs.add(key);
	}
	
	public void removeHistoricalLeadKey(int key)
	{
		historicalLeadIDs.remove(key);
	}
	
	public void prepareToAddHistoricalLeadKey(int key)
	{
		mNewHistoricalLeadIDs.add(key);
	}
	
	public boolean hasHistoricalLeadKey(int key)
	{
		for(int currentKey : historicalLeadIDs.getArray())
		{
			if(currentKey == key)
				return true;
		}
		return false;
	}
	
	public void addHistoricalLeadKeys()
	{
		for(int key : mNewHistoricalLeadIDs.getArray())
			historicalLeadIDs.add(key);
			
		mNewHistoricalLeadIDs = new ExtensibleIntArray();
	}
	
	public void immediatelyAddHistoricalLeadKey(int key)
	{
		historicalLeadIDs.add(key);
	}
	
	//(Normal) setters
	public void setID(int iD)
	{
		this.iD = iD;
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	
	public void setPhoneNumber(String phoneNumber)
	{
		mPhoneNumber = phoneNumber;
	}
	
	public void setEmailAddress(String emailAddress)
	{
		mEmailAddress = emailAddress;
	}
	
	//Getters
	public int getID()
	{
		return iD;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public String getPhoneNumber()
	{
		return mPhoneNumber;
	}
	
	public String getEmailAddress()
	{
		return mEmailAddress;
	}
	
	public int[] getCurrentLeadIDs()
	{
		return currentLeadIDs.getArray();
	}
	
	public int[] getHistoricalLeadIDs()
	{
		return historicalLeadIDs.getArray();
	}
}
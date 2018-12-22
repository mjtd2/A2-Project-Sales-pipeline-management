package backend;

public class StageOfNegotiation
{
	private int iD;
	private String mNameOfStage;
	private int mDefaultProbability;
	private boolean leadShouldBeMoved;
	boolean mAssumeSuccessfulIfMoved;
	
	public StageOfNegotiation(int iD, String nameOfStage, int defaultProbability, boolean leadShouldBeMoved, boolean assumeSuccessfulIfMoved)
	{
		this.iD = iD;
		mNameOfStage = nameOfStage;
		mDefaultProbability = defaultProbability;
		this.leadShouldBeMoved = leadShouldBeMoved;
		mAssumeSuccessfulIfMoved = assumeSuccessfulIfMoved;
	}
	
	public void setID(int iD)
	{
		this.iD = iD;
	}
	
	public void setNameOfStage(String nameOfStage)
	{
		mNameOfStage = nameOfStage;
	}
	
	public void setDefaultProbability(int defaultProbability)
	{
		mDefaultProbability = defaultProbability;
	}
	
	public void setMoveLeads(boolean moveLeads)
	{
		leadShouldBeMoved = moveLeads;
	}
	
	public void setAssumingSuccessful(boolean assumingSuccessful)
	{
		mAssumeSuccessfulIfMoved = assumingSuccessful;
	}
	
	public int getID()
	{
		return iD;
	}
	
	public String getNameOfStage()
	{
		return mNameOfStage;
	}
	
	public int getDefaultProbability()
	{
		return mDefaultProbability;
	}
	
	public boolean leadShouldBeMoved()
	{
		return leadShouldBeMoved;
	}
	
	public boolean assumingSuccessfulIfMoved()
	{
		return mAssumeSuccessfulIfMoved;
	}
}
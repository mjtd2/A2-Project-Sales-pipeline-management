package backend;

import java.util.GregorianCalendar;
import java.util.Date;

public class Lead
{
	protected int iD;
	protected String dealDay;
	protected String dealMonth;
	protected int dealYear;
	protected int value;
	int clientID;
	int mNewClientID = -1;
	int stageOfNegotiationsID;
	int mNewStageofNegotiationsID = -1;
	static String mCurrencySymbol = "\u00a3";
	
	public static void setCurrencySymbol(String currencySymbol)
	{
		mCurrencySymbol = currencySymbol;
	}
	
	public void prepareToSetStageOfNegotiationsID(int stageID)
	{
		mNewStageofNegotiationsID = stageID;
	}
	
	public void setStageOfNegotiationsID()
	{
		if(mNewStageofNegotiationsID >= 0)
		{
			stageOfNegotiationsID = mNewStageofNegotiationsID;
			mNewStageofNegotiationsID = -1;//Rogue value
		}
	}
	
	public void prepareToSetClientID(int clientID)
	{
		mNewClientID = clientID;
	}
	
	public void setClientID()
	{
		if(mNewClientID >= 0)
		{
			clientID = mNewClientID;
			mNewClientID = -1;//Rogue value
		}
	}
	
	public void immediatelySetClientID(int newID)
	{
		clientID = newID;
	}
	
	public void setID(int iD)
	{
		this.iD = iD;
	}
	
	public int getID()
	{
		return iD;
	}
	
	public String getDealDay()
	{
		return dealDay;
	}
	
	public String getDealMonth()
	{
		return dealMonth;
	}
	
	public String getDealYear()
	{
		return Integer.toString(dealYear);
	}
	
	public String getDealDate()
	{
		return(dealDay + "/" + dealMonth + "/" + Integer.toString(dealYear));
	}
	
	public Date getDealDateAsDate()
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(getDealYear()), Integer.parseInt(getDealMonth()) - 1, Integer.parseInt(getDealDay()));
		return calendar.getTime();
	}
	
	public int getValue()
	{
		return value;
	}
	
	public String getValueString()
	{
		return mCurrencySymbol + value;
	}
	
	public int getClientID()
	{
		return clientID;
	}
	
	public int getStageOfNegotiationsID()
	{
		return stageOfNegotiationsID;
	}
}
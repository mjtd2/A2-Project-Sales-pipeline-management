package backend;

import java.util.GregorianCalendar;
import java.util.Date;

public class ActionReminder
{
	int mID;
	int mCurrentLeadID;
	int mNewCurrentLeadID;
	String mActionDay;
	String mActionMonth;
	int mActionYear;
	String mMessage;
	
	public ActionReminder(int iD, int currentLeadID, String actionDay, String actionMonth, int actionYear, String message)
	{
		mID = iD;
		mCurrentLeadID = currentLeadID;
		mActionDay = actionDay;
		mActionMonth = actionMonth;
		mActionYear = actionYear;
		mMessage = message;
		mNewCurrentLeadID = -1;//Rogue value
	}
	
	public void prepareToSetCurrentLeadID(int currentLeadID)
	{
		mNewCurrentLeadID = currentLeadID;
	}
	
	public void setCurrentLeadID()
	{
		if(mNewCurrentLeadID >= 0)
		{
			mCurrentLeadID = mNewCurrentLeadID;
			mNewCurrentLeadID = -1;//Rogue value
		}
	}
	
	public void immediatelySetCurrentLeadID(int currentLeadID)
	{
		mCurrentLeadID = currentLeadID;
	}
	
	public void setID(int iD)
	{
		mID = iD;
	}
	
	public int getID()
	{
		return mID;
	}
	
	public int getCurrentLeadID()
	{
		return mCurrentLeadID;
	}
	
	public String getActionDay()
	{
		return mActionDay;
	}
	
	public String getActionMonth()
	{
		return mActionMonth;
	}
	
	public String getActionYear()
	{
		return Integer.toString(mActionYear);
	}
	
	public String getActionDate()
	{
		return mActionDay + "/" + mActionMonth + "/" + Integer.toString(mActionYear);
	}
	
	public Date getActionDateAsDate()
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(getActionYear()), Integer.parseInt(getActionMonth()) - 1, Integer.parseInt(getActionDay()));
		return calendar.getTime();
	}
	
	public String getMessage()
	{
		return mMessage;
	}
}
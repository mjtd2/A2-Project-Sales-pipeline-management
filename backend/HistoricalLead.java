package backend;

import java.util.List;
import java.util.ArrayList;

public class HistoricalLead extends Lead
{
	boolean mSuccessful;
	
	public HistoricalLead(int iD, int clientID, String dealDay, String dealMonth, int dealYear, int value, int stageOfNegotiationsID, boolean successful)
	{
		this.iD = iD;
		this.clientID = clientID;//Uses -1 as a rogue value.
		this.stageOfNegotiationsID = stageOfNegotiationsID;//Uses -1 as a rogue value.
		this.dealDay = dealDay;
		this.dealMonth = dealMonth;
		this.dealYear = dealYear;
		this.value = value;
		mSuccessful = successful;
	}
	
	public void removeClient()
	{
		clientID = -1;
	}
	
	public boolean wasSuccessful()
	{
		return mSuccessful;
	}
}
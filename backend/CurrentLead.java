package backend;

import java.util.List;
import java.util.ArrayList;

public class CurrentLead extends Lead
{
	private int probabilityInPercent;
	private ExtensibleIntArray actionReminderIDs;
	ExtensibleIntArray mNewActionReminderIDs;
	
	//Constructor for loading from file to memory
	public CurrentLead(int iD, int clientID, String dealDay, String dealMonth, int dealYear, int value, int stageOfNegotiationsID, int probability, Memory memory)
	{
		doConstruction(iD, clientID, dealDay, dealMonth, dealYear, value, stageOfNegotiationsID, probability);
		actionReminderIDs = memory.findActionReminderIDs(iD);
	}
	
	//Normal constructor
	public CurrentLead(int iD, int clientID, String dealDay, String dealMonth, int dealYear, int value, int stageOfNegotiationsID, int probability)
	{
		doConstruction(iD, clientID, dealDay, dealMonth, dealYear, value, stageOfNegotiationsID, probability);
		actionReminderIDs = new ExtensibleIntArray();
	}
	
	private void doConstruction(int iD, int clientID, String dealDay, String dealMonth, int dealYear, int value, int stageOfNegotiationsID, int probability)
	{
		//Initialise variables
		this.iD = iD;
		this.clientID = clientID;
		this.stageOfNegotiationsID = stageOfNegotiationsID;
		this.dealDay = dealDay;
		this.dealMonth = dealMonth;
		this.dealYear = dealYear;
		this.value = value;
		this.probabilityInPercent = probability;
		mNewActionReminderIDs = new ExtensibleIntArray();
	}
	
	public boolean hasActionKey(int key)
	{
		for(int currentKey : actionReminderIDs.getArray())
		{
			if(currentKey == key)
				return true;
		}
		return false;
	}
	
	public void removeActionReminderKey(int key)
	{
		actionReminderIDs.remove(key);
	}
	
	public void prepareToAddActionReminderKey(int key)
	{
		mNewActionReminderIDs.add(key);
	}
	
	public void addActionReminderKeys()
	{
		for(int key : mNewActionReminderIDs.getArray())
			actionReminderIDs.add(key);
		
		mNewActionReminderIDs = new ExtensibleIntArray();
	}
	
	public void immediatelyAddActionReminderKey(int key)
	{
		actionReminderIDs.add(key);
	}
	
	public void removeActionReminderKeys()
	{
		actionReminderIDs = new ExtensibleIntArray();
	}
	
	//Getters
	public int getProbability()
	{
		return probabilityInPercent;
	}
	
	public String getProbabilityString()
	{
		return probabilityInPercent + "%";
	}
	
	public int[] getActionReminderIDs()
	{
		return actionReminderIDs.getArray();
	}
}
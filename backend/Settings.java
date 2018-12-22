package backend;

import java.util.Properties;

public class Settings
{
	Properties mProperties;
	boolean lockHistoricalData;
	boolean automaticallyDeleteActionReminders;
	boolean automaticallyDeleteLeads;
	boolean sendLeadsToHistorical;
	boolean automaticallyDeleteHistoricalLeads;
	boolean deleteAllButNewestNHistoricalLeads;
	int numberOfNewestHistoricalLeadsToKeep;
	int maximumAgeOfHistoricalLeadsInDays;
	String currencySymbol;
	
	public Settings(Properties properties)
	{
		mProperties = properties;
		//Set the variables to the values in settings.properties, or the defaults shown
		lockHistoricalData = Boolean.parseBoolean(properties.getProperty("lockHistoricalData", "false"));
		automaticallyDeleteActionReminders = Boolean.parseBoolean(properties.getProperty("automaticallyDeleteActionReminders", "false"));
		automaticallyDeleteLeads = Boolean.parseBoolean(properties.getProperty("automaticallyDeleteLeads", "false"));
		sendLeadsToHistorical = Boolean.parseBoolean(properties.getProperty("sendLeadsToHistorical", "false"));
		automaticallyDeleteHistoricalLeads = Boolean.parseBoolean(properties.getProperty("automaticallyDeleteHistoricalLeads", "false"));
		deleteAllButNewestNHistoricalLeads = Boolean.parseBoolean(properties.getProperty("deleteAllButNewestNHistoricalLeads", "false"));
		numberOfNewestHistoricalLeadsToKeep = Integer.parseInt(properties.getProperty("numberOfNewestHistoricalLeadsToKeep", "50"));
		maximumAgeOfHistoricalLeadsInDays = Integer.parseInt(properties.getProperty("maximumAgeOfHistoricalLeadsInDays", "1826"));
		currencySymbol = properties.getProperty("currencySymbol", "\u00a3");
	}
	
	//Setters
	public void setLockHistoricalData(boolean newValue)
	{
		lockHistoricalData = newValue;
		Object o = mProperties.setProperty("lockHistoricalData", String.valueOf(newValue));
	}
	
	public void setAutomaticallyDeleteActionReminders(boolean newValue)
	{
		automaticallyDeleteActionReminders = newValue;
		Object o = mProperties.setProperty("automaticallyDeleteActionReminders", String.valueOf(newValue));
	}
	
	public void setAutomaticallyDeleteLeads(boolean newValue)
	{
		automaticallyDeleteLeads = newValue;
		Object o = mProperties.setProperty("automaticallyDeleteLeads", String.valueOf(newValue));
	}
	
	public void setSendLeadsToHistorical(boolean newValue)
	{
		sendLeadsToHistorical = newValue;
		Object o = mProperties.setProperty("sendLeadsToHistorical", String.valueOf(newValue));
	}
	
	public void setAutomaticallyDeleteHistoricalLeads(boolean newValue)
	{
		automaticallyDeleteHistoricalLeads = newValue;
		Object o = mProperties.setProperty("automaticallyDeleteHistoricalLeads", String.valueOf(newValue));
	}
	
	public void setDeleteAllButNewestNHistoricalLeads(boolean newValue)
	{
		deleteAllButNewestNHistoricalLeads = newValue;
		Object o = mProperties.setProperty("deleteAllButNewestNHistoricalLeads", String.valueOf(newValue));
	}
	
	public void setNumberOfNewestHistoricalLeadsToKeep(int newValue)
	{
		numberOfNewestHistoricalLeadsToKeep = newValue;
		Object o = mProperties.setProperty("numberOfNewestHistoricalLeadsToKeep", String.valueOf(newValue));
	}
	
	public void setMaximumAgeOfHistoricalLeadsInDays(int newValue)
	{
		maximumAgeOfHistoricalLeadsInDays = newValue;
		Object o = mProperties.setProperty("maximumAgeOfHistoricalLeadsInDays", String.valueOf(newValue));
	}
	
	public void setCurrencySymbol(String newValue)
	{
		currencySymbol = newValue;
		Object o = mProperties.setProperty("currencySymbol", newValue);
	}
	
	//Getters
	public boolean getLockHistoricalData()
	{
		return lockHistoricalData;
	}
	
	public boolean getAutomaticallyDeleteActionReminders()
	{
		return automaticallyDeleteActionReminders;
	}
	
	public boolean getAutomaticallyDeleteLeads()
	{
		return automaticallyDeleteLeads;
	}
	
	public boolean getSendLeadsToHistorical()
	{
		return sendLeadsToHistorical;
	}
	
	public boolean getAutomaticallyDeleteHistoricalLeads()
	{
		return automaticallyDeleteHistoricalLeads;
	}
	
	public boolean getDeleteAllButNewestNHistoricalLeads()
	{
		return deleteAllButNewestNHistoricalLeads;
	}
	
	public int getNumberOfNewestHistoricalLeadsToKeep()
	{
		return numberOfNewestHistoricalLeadsToKeep;
	}
	
	public int getMaximumAgeOfHistoricalLeadsInDays()
	{
		return maximumAgeOfHistoricalLeadsInDays;
	}
	
	public String getCurrencySymbol()
	{
		return currencySymbol;
	}
}
package backend;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Memory
{
	BackendManager mBackendManager;
	private String[] listOfDays;
	private String[] listOfMonths;
	private String[] listOfFutureYears;
	String[] listOfYears;
	private Integer[] zeroToOneHundred;
	private List<StageOfNegotiation> stagesOfNegotiation;
	private List<Client> clients;
	private List<Client> filteredClients;
	private List<CurrentLead> currentLeads;
	private List<CurrentLead> filteredCurrentLeads;
	private List<ActionReminder> actionReminders;
	private List<ActionReminder> filteredActionReminders;
	List<HistoricalLead> historicalLeads;
	List<HistoricalLead> filteredHistoricalLeads;
	Settings settings;
	Properties properties;
	int mCurrentYear;
	int mCurrentMonth;
	int mCurrentDay;
	
	public Memory(BackendManager backendManager)
	{
		mBackendManager = backendManager;
		//Load settings from file
		properties = new Properties();
		try
		{
			properties.load(new BufferedReader(new FileReader("Data/settings.properties")));
		}
		catch(IOException e)
		{
			System.err.println("Failed to read from settings.properties.");
		}
		settings = new Settings(properties);
		
		//Set the currency symbol for leads
		Lead.setCurrencySymbol(settings.getCurrencySymbol());
		
		//Load the list of action reminders from file
		actionReminders = new ArrayList<ActionReminder>();
		filteredActionReminders = new ArrayList<ActionReminder>();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Data/Actions.csv"));
			String line = "";
			//While a new line exists...
			while((line = bufferedReader.readLine() ) != null)
			{
				String[] actionDetails = convertFromCSVFormat(line, 4);
				String[] reminderDate = actionDetails[2].split("/");
				//int iD, int currentLeadID, String actionDay, String actionMonth, int actionYear, String message
				actionReminders.add(new ActionReminder(Integer.parseInt(actionDetails[0]), Integer.parseInt(actionDetails[1]), reminderDate[0], reminderDate[1], Integer.parseInt(reminderDate[2]), actionDetails[3]));
				filteredActionReminders.add(actionReminders.get(actionReminders.size() - 1));
			}
			bufferedReader.close();
		}
		catch(IOException e)
		{
			System.err.println("Failed to read Actions file.");
		}
		
		//Load the list of stages of negotiation from file
		stagesOfNegotiation = new ArrayList<StageOfNegotiation>();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Data/StagesOfNegotiation.csv"));
			String line = "";
			//While a new line exists...
			while((line = bufferedReader.readLine() ) != null)
			{
				String[] stagesOfNegotiationDetails = convertFromCSVFormat(line, 5);
				stagesOfNegotiation.add(new StageOfNegotiation(Integer.parseInt(stagesOfNegotiationDetails[0]), stagesOfNegotiationDetails[1], Integer.parseInt(stagesOfNegotiationDetails[2]), Boolean.parseBoolean(stagesOfNegotiationDetails[3]), Boolean.parseBoolean(stagesOfNegotiationDetails[4])));
			}
			bufferedReader.close();
		}
		catch(IOException e)
		{
			System.err.println("Failed to read StagesOfNegotiation file.");
		}
		
		//Load the list of current leads from file
		currentLeads = new ArrayList<CurrentLead>();
		filteredCurrentLeads = new ArrayList<CurrentLead>();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Data/CurrentLeads.csv"));
			String line = "";
			//While a new line exists...
			while((line = bufferedReader.readLine() ) != null)
			{
				String[] leadDetails = convertFromCSVFormat(line, 6);
				String[] dealDate = leadDetails[2].split("/");
				currentLeads.add(new CurrentLead(Integer.parseInt(leadDetails[0]), Integer.parseInt(leadDetails[1]), dealDate[0], dealDate[1], Integer.parseInt(dealDate[2]), Integer.parseInt(leadDetails[3]), Integer.parseInt(leadDetails[4]), Integer.parseInt(leadDetails[5]), this));
				filteredCurrentLeads.add(currentLeads.get(currentLeads.size() - 1));
			}
			bufferedReader.close();
		}
		catch(IOException e)
		{
			System.err.println("Failed to read CurrentLeads file.");
		}
		
		//Load the list of historical leads from file
		historicalLeads = new ArrayList<HistoricalLead>();
		filteredHistoricalLeads = new ArrayList<HistoricalLead>();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Data/HistoricalLeads.csv"));
			String line = "";
			//While a new line exists...
			while((line = bufferedReader.readLine() ) != null)
			{
				String[] leadDetails = convertFromCSVFormat(line, 6);
				String[] dealDate = leadDetails[2].split("/");
				historicalLeads.add(new HistoricalLead(Integer.parseInt(leadDetails[0]), Integer.parseInt(leadDetails[1]), dealDate[0], dealDate[1], Integer.parseInt(dealDate[2]), Integer.parseInt(leadDetails[3]), Integer.parseInt(leadDetails[4]), Boolean.parseBoolean(leadDetails[5])));
				filteredHistoricalLeads.add(historicalLeads.get(historicalLeads.size() - 1));
			}
			bufferedReader.close();
		}
		catch(IOException e)
		{
			System.err.println("Failed to read CurrentLeads file.");
		}
		
		//Load the list of clients from file
		clients = new ArrayList<Client>();
		filteredClients = new ArrayList<Client>();
		try
		{
			BufferedReader bufferedReader = new BufferedReader(new FileReader("Data/Clients.csv"));
			String line = "";
			//While a new line exists...
			while((line = bufferedReader.readLine() ) != null)
			{
				String[] clientDetails = convertFromCSVFormat(line, 4);
				clients.add(new Client(Integer.parseInt(clientDetails[0]), clientDetails[1], clientDetails[2], clientDetails[3], this));
				filteredClients.add(clients.get(clients.size() - 1));
			}
			bufferedReader.close();
		}
		catch(IOException e)
		{
			System.err.println("Failed to read Clients file.");
		}
		
		//Generate calculable data that may be used often
		mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
		mCurrentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		mCurrentDay = Calendar.getInstance().get(Calendar.DATE);
		listOfDays = new String[31];
		listOfMonths = new String[12];
		listOfFutureYears = new String[9];
		listOfYears = new String[19];
		zeroToOneHundred = new Integer[101];
		
		for(int i = 0; i < 9; i++)
		{
			listOfDays[i] = "0" + Integer.toString(i+1);
			listOfMonths[i] = "0" + Integer.toString(i+1);
			listOfFutureYears[i] = Integer.toString(mCurrentYear + i);
			listOfYears[i] = Integer.toString(mCurrentYear + i - 9);
			zeroToOneHundred[i] = new Integer(i);
		}
		for(int i = 9; i < 12; i++)
		{
			listOfDays[i] = Integer.toString(i+1);
			listOfMonths[i] = Integer.toString(i+1);
			listOfYears[i] = Integer.toString(mCurrentYear + i - 9);
			zeroToOneHundred[i] = new Integer(i);
		}
		for(int i = 12; i < 19; i++)
		{
			listOfDays[i] = Integer.toString(i+1);
			listOfYears[i] = Integer.toString(mCurrentYear + i - 9);
			zeroToOneHundred[i] = new Integer(i);
		}
		for (int i = 19; i < 31; i++)
		{
			listOfDays[i] = Integer.toString(i+1);
			zeroToOneHundred[i] = new Integer(i);
		}
		for(int i = 31; i < 101; i++)
		{
			zeroToOneHundred[i] = new Integer(i);
		}
	}
	
	public void add(CurrentLead lead)
	{
		lead.setID(currentLeads.size());
		currentLeads.add(lead);
		//Add reference to this lead to its client
		clients.get(lead.getClientID()).immediatelyAddCurrentLeadKey(lead.getID());
		sortCurrentLeads();
	}
	
	public void add(ActionReminder reminderToAdd)
	{
		reminderToAdd.setID(actionReminders.size());
		actionReminders.add(reminderToAdd);
		//Add reference to this reminder to its lead
		currentLeads.get(reminderToAdd.getCurrentLeadID()).immediatelyAddActionReminderKey(reminderToAdd.getID());
		sortActionReminders();
	}
	
	public void add(Client client)
	{
		client.setID(clients.size());
		clients.add(client);
		reassignClientPrimaryKeys();
	}
	
	public void add(HistoricalLead lead)
	{
		lead.setID(historicalLeads.size());
		historicalLeads.add(lead);
		
		//Add reference to this lead to its client
		if(lead.getClientID() >= 0)
			clients.get(lead.getClientID()).immediatelyAddHistoricalLeadKey(lead.getID());
		
		sortHistoricalLeads();
	}
	
	public void add(StageOfNegotiation stage)
	{
		stage.setID(stagesOfNegotiation.size());
		stagesOfNegotiation.add(stage);
		sortStagesOfNegotiation();
	}
	
	public void removeActionReminder(int reminderIndex)
	{
		//Remove reference to this reminder from its lead
		currentLeads.get(actionReminders.get(reminderIndex).getCurrentLeadID()).removeActionReminderKey(reminderIndex);
		actionReminders.remove(reminderIndex);
		sortActionReminders();
		mBackendManager.actionReminderRemoved();
	}
	
	public void removeCurrentLead(int currentLeadIndex)
	{
		//Remove all the lead's action reminders
		while(currentLeads.get(currentLeadIndex).getActionReminderIDs().length != 0)
			removeActionReminder(currentLeads.get(currentLeadIndex).getActionReminderIDs()[0]);
		//Remove reference to this lead from its client
		clients.get(currentLeads.get(currentLeadIndex).getClientID()).removeCurrentLeadKey(currentLeadIndex);
		currentLeads.remove(currentLeadIndex);
		sortCurrentLeads();
		mBackendManager.currentLeadRemoved();
	}
	
	public void removeHistoricalLead(int historicalLeadIndex)
	{
		//Remove reference to this lead from its client
		if(historicalLeads.get(historicalLeadIndex).getClientID() >= 0)
			clients.get(historicalLeads.get(historicalLeadIndex).getClientID()).removeHistoricalLeadKey(historicalLeadIndex);
		historicalLeads.remove(historicalLeadIndex);
		sortHistoricalLeads();
		mBackendManager.historicalLeadRemoved();
	}
	
	public void removeClient(int clientIndex, boolean deletingHistoricalLeads)
	{
		if(deletingHistoricalLeads)
		{
			//Remove all the client's historical leads
			while(clients.get(clientIndex).getHistoricalLeadIDs().length != 0)
				removeHistoricalLead(clients.get(clientIndex).getHistoricalLeadIDs()[0]);
		}
		//If anonymising historical leads...
		else
		{
			for(int leadID : clients.get(clientIndex).getHistoricalLeadIDs())
				historicalLeads.get(leadID).removeClient();
		}
		clients.remove(clientIndex);
		reassignClientPrimaryKeys();
	}
	
	public void removeStageOfNegotiation(int stageIndex)
	{
		stagesOfNegotiation.remove(stageIndex);
		sortStagesOfNegotiation();
	}
	
	//Sorts action reminders by date, then re-assigns primary keys
	private void sortActionReminders()
	{
		//Insertion sort
		for(int i = 0; i < actionReminders.size() - 1; i++)
		{
			ActionReminder reminder = actionReminders.get(i+1);
			while(i >= 0 && Validator.firstDateAfterSecond(Integer.parseInt(actionReminders.get(i).getActionDay()), Integer.parseInt(actionReminders.get(i).getActionMonth()), Integer.parseInt(actionReminders.get(i).getActionYear()), Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear())))
			{
				actionReminders.set(i+1, actionReminders.get(i));
				i--;
			}
			actionReminders.set(i+1, reminder);
		}
		//Reassign primary & reset foreign keys
		for(int i = 0; i < actionReminders.size(); i++)
		{
			if(actionReminders.get(i).getID() != i)
			{
				for(CurrentLead lead : currentLeads)
				{
					if(lead.hasActionKey(actionReminders.get(i).getID()))
					{
						lead.removeActionReminderKey(actionReminders.get(i).getID());
						lead.prepareToAddActionReminderKey(i);
						break;
					}
				}
				actionReminders.get(i).setID(i);
			}
		}
		
		for(CurrentLead lead : currentLeads)
			lead.addActionReminderKeys();
		
		//Update the filtered list
		setFilteredActionReminders(new ArrayList<ActionReminder>());
		for(ActionReminder reminder : actionReminders)
			filteredActionReminders.add(reminder);
	}
	
	//Sorts current leads by date, then re-assigns primary keys
	private void sortCurrentLeads()
	{
		//Insertion sort
		for(int i = 0; i < currentLeads.size() - 1; i++)
		{
			CurrentLead lead = currentLeads.get(i+1);
			while(i >= 0 &&
					(Integer.parseInt(currentLeads.get(i).getDealYear()) > Integer.parseInt(lead.getDealYear())
					|| Integer.parseInt(currentLeads.get(i).getDealYear()) == Integer.parseInt(lead.getDealYear())
					&& (Integer.parseInt(currentLeads.get(i).getDealMonth()) > Integer.parseInt(lead.getDealMonth())
					|| (Integer.parseInt(currentLeads.get(i).getDealMonth()) == Integer.parseInt(lead.getDealMonth())
					&& Integer.parseInt(currentLeads.get(i).getDealDay()) > Integer.parseInt(lead.getDealDay())))))
			{
				currentLeads.set(i+1, currentLeads.get(i));
				i--;
			}
			currentLeads.set(i+1, lead);
		}
		//Reassign primary & reset foreign keys
		for(int i = 0; i < currentLeads.size(); i++)
		{
			if(currentLeads.get(i).getID() != i)
			{
				for(Client client : clients)
				{
					if(client.hasCurrentLeadKey(currentLeads.get(i).getID()))
					{
						client.removeCurrentLeadKey(currentLeads.get(i).getID());
						client.prepareToAddCurrentLeadKey(i);
						break;
					}
				}
				//For each action reminder...
				for(ActionReminder reminder : actionReminders)
				{
					//If the reminder relates to this lead...
					if(reminder.getCurrentLeadID() == currentLeads.get(i).getID())
						reminder.prepareToSetCurrentLeadID(i);
				}
				currentLeads.get(i).setID(i);
			}
		}
		
		//For each client...
		for(Client client : clients)
			client.addCurrentLeadKeys();
		//For each action reminder...
		for(ActionReminder reminder : actionReminders)
			reminder.setCurrentLeadID();
		
		//Update the filtered list
		setFilteredCurrentLeads(new ArrayList<CurrentLead>());
		for(CurrentLead lead : currentLeads)
			filteredCurrentLeads.add(lead);
	}
	
	//Sorts historical leads by date, then re-assigns primary keys
	public void sortHistoricalLeads()
	{
		//Insertion sort
		for(int i = 0; i < historicalLeads.size() - 1; i++)
		{
			HistoricalLead lead = historicalLeads.get(i+1);
			while(i >= 0 &&
					(Integer.parseInt(historicalLeads.get(i).getDealYear()) < Integer.parseInt(lead.getDealYear())
					|| Integer.parseInt(historicalLeads.get(i).getDealYear()) == Integer.parseInt(lead.getDealYear())
					&& (Integer.parseInt(historicalLeads.get(i).getDealMonth()) < Integer.parseInt(lead.getDealMonth())
					|| (Integer.parseInt(historicalLeads.get(i).getDealMonth()) == Integer.parseInt(lead.getDealMonth())
					&& Integer.parseInt(historicalLeads.get(i).getDealDay()) < Integer.parseInt(lead.getDealDay())))))
			{
				historicalLeads.set(i+1, historicalLeads.get(i));
				i--;
			}
			historicalLeads.set(i+1, lead);
		}
		//Reassign primary & reset foreign keys
		for(int i = 0; i < historicalLeads.size(); i++)
		{
			if(historicalLeads.get(i).getID() != i)
			{
				if(historicalLeads.get(i).getClientID() >= 0)
				{
					for(Client client : clients)
					{
						if(client.hasHistoricalLeadKey(historicalLeads.get(i).getID()))
						{
							client.removeHistoricalLeadKey(historicalLeads.get(i).getID());
							client.prepareToAddHistoricalLeadKey(i);
						}
					}
				}
				historicalLeads.get(i).setID(i);
			}
		}
		//For each client...
		for(Client client : clients)
			client.addHistoricalLeadKeys();
		
		//Update the filtered list
		setFilteredHistoricalLeads(new ArrayList<HistoricalLead>());
		for(HistoricalLead lead : historicalLeads)
			filteredHistoricalLeads.add(lead);
	}
	
	//Resets clients' primary keys so they are in sequential order, and chnages foreign keys as necessary.
	private void reassignClientPrimaryKeys()
	{
		//For each client...
		for(int i = 0; i < clients.size(); i++)
		{
			//For each current lead...
			for(CurrentLead lead : currentLeads)
			{
				//If the lead relates to this client...
				if(lead.getClientID() == clients.get(i).getID())
					lead.prepareToSetClientID(i);
			}
			//For each historical lead...
			for(HistoricalLead lead : historicalLeads)
			{
				//If the lead relates to this client...
				if(lead.getClientID() == clients.get(i).getID())
					lead.prepareToSetClientID(i);
			}
			clients.get(i).setID(i);
		}
		//For each current lead...
		for(CurrentLead lead : currentLeads)
			lead.setClientID();
		//For each historical lead...
		for(HistoricalLead lead : historicalLeads)
			lead.setClientID();
		
		//Update the filtered list
		setFilteredClients(new ArrayList<Client>());
		for(Client client : clients)
			filteredClients.add(client);
	}
	
	//Sorts stages of negotiation by probability, then re-assigns primary keys
	public void sortStagesOfNegotiation()
	{
		//Insertion sort
		for(int i = 0; i < stagesOfNegotiation.size() - 1; i++)
		{
			StageOfNegotiation stage = stagesOfNegotiation.get(i+1);
			while(i >= 0 && stagesOfNegotiation.get(i).getDefaultProbability() > stage.getDefaultProbability())
			{
				stagesOfNegotiation.set(i+1, stagesOfNegotiation.get(i));
				i--;
			}
			stagesOfNegotiation.set(i+1, stage);
		}
		//Reassign primary & reset foreign keys
		for(int i = 0; i < stagesOfNegotiation.size(); i++)
		{
			//If the stage is in the wrong index...
			if(stagesOfNegotiation.get(i).getID() != i)
			{
				//For each current lead...
				for(CurrentLead lead : currentLeads)
				{
					if(lead.getStageOfNegotiationsID() == stagesOfNegotiation.get(i).getID())
						lead.prepareToSetStageOfNegotiationsID(i);
				}
				
				//For each historical lead...
				for(HistoricalLead lead : historicalLeads)
				{
					if(lead.getStageOfNegotiationsID() == stagesOfNegotiation.get(i).getID())
						lead.prepareToSetStageOfNegotiationsID(i);
				}
			}
			stagesOfNegotiation.get(i).setID(i);
		}
		//For each current lead...
		for(CurrentLead lead : currentLeads)
			lead.setStageOfNegotiationsID();
		//For each historical lead...
		for(HistoricalLead lead : historicalLeads)
			lead.setStageOfNegotiationsID();
	}
	
	public String[] convertFromCSVFormat(String line, int numberOfFields)
	{
		//line and numberOfFields are already declared & set
		String[] output = new String[numberOfFields];
		int fieldIndex = 0;
		int startAt = 0;
		boolean inQuotationMarks = false;
		boolean newField = true;
		
		//For each element in output...
		for(int i = 0; i < output.length; i++)
			//Set to an empty (not null) String (to avoid Strings which say "null" before their contents)
			output[i] = "";
		
		//If the line starts with a '"'...
		if(line.charAt(0) == '\"')
		{
			inQuotationMarks = true;
			startAt = 1;
		}
		
		//For each subsequent character...
		for(int i = startAt; i < line.length(); i++)
		{
			//If the character is a "...
			if(line.charAt(i) == '\"')
			{
				if(inQuotationMarks)
				{
					//If the character is the last in the line...
					if(i == line.length() - 1)
						continue;
					//If the next character is also a "...
					if(line.charAt(i+1) == '\"')
					{
						output[fieldIndex] += "\"";
						i++;
					}
					//If the next character is ","...
					else if(line.charAt(i+1) == ',')
						inQuotationMarks = false;
					else
						System.err.println("File was not in .CSV format.");
				}
				//If not in quotation marks && a new field...
				else if(output[fieldIndex].length() == 0)
					inQuotationMarks = true;
			}
			//If the character is a ","...
			else if(line.charAt(i) == ',')
			{
				//If it is literal...
				if(inQuotationMarks)
					output[fieldIndex] += ",";
				else
					fieldIndex = fieldIndex + 1;
			}
			//For normal characters...
			else
				output[fieldIndex] += line.charAt(i);
		}
		return output;
	}
	
	public ExtensibleIntArray findActionReminderIDs(int currentLeadID)
	{
		ExtensibleIntArray output = new ExtensibleIntArray();
		//For each action reminder...
		for(ActionReminder reminder : actionReminders)
		{
			if(reminder.getCurrentLeadID() == currentLeadID)
				output.add(reminder.getID());
		}
		return output;
	}
	
	public ExtensibleIntArray findCurrentLeadIDs(int clientID)
	{
		ExtensibleIntArray output = new ExtensibleIntArray();
		//For each current lead...
		for(CurrentLead lead : currentLeads)
		{
			if(lead.getClientID() == clientID)
				output.add(lead.getID());
		}
		return output;
	}
	
	public ExtensibleIntArray findHistoricalLeadIDs(int clientID)
	{
		ExtensibleIntArray output = new ExtensibleIntArray();
		//For each historical lead...
		for(HistoricalLead lead : historicalLeads)
		{
			if(lead.getClientID() == clientID)
				output.add(lead.getID());
		}
		return output;
	}
	
	public void setFilteredCurrentLeads(List<CurrentLead> leads)
	{
		filteredCurrentLeads = leads;
	}
	
	public void setFilteredActionReminders(List<ActionReminder> reminders)
	{
		filteredActionReminders = reminders;
	}
	
	public void setFilteredClients(List<Client> clients)
	{
		filteredClients = clients;
	}
	
	public void setFilteredHistoricalLeads(List<HistoricalLead> leads)
	{
		filteredHistoricalLeads = leads;
	}
	
	public List<CurrentLead> getCurrentLeads()
	{
		return currentLeads;
	}
	
	public List<CurrentLead> getFilteredCurrentLeads()
	{
		return filteredCurrentLeads;
	}
	
	public List<HistoricalLead> getHistoricalLeads()
	{
		return historicalLeads;
	}
	
	public List<HistoricalLead> getFilteredHistoricalLeads()
	{
		return filteredHistoricalLeads;
	}
	
	public int getCurrentDay()
	{
		return mCurrentDay;
	}
	
	public int getCurrentMonth()
	{
		return mCurrentMonth;
	}
	
	public int getCurrentYear()
	{
		return mCurrentYear;
	}
	
	public String[] getListOfDays()
	{
		return listOfDays;
	}
	
	public String[] getListOfMonths()
	{
		return listOfMonths;
	}
	
	public String[] getListOfYears()
	{
		return listOfYears;
	}
	
	//Includes the current year.
	public String[] getListOfFutureYears()
	{
		return listOfFutureYears;
	}
	
	public Integer[] getZeroToOneHundred()
	{
		return zeroToOneHundred;
	}
	
	public List<StageOfNegotiation> getStagesOfNegotiation()
	{
		return stagesOfNegotiation;
	}
	
	public List<Client> getClients()
	{
		return clients;
	}
	
	public List<Client> getFilteredClients()
	{
		return filteredClients;
	}
	
	public List<ActionReminder> getActionReminders()
	{
		return actionReminders;
	}
	
	public List<ActionReminder> getFilteredActionReminders()
	{
		return filteredActionReminders;
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public Properties getProperties()
	{
		return properties;
	}
}
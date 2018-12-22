package backend;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import gui.currentleads.*;
import gui.actionreminders.*;
import gui.clients.*;
import gui.historicaldata.*;
import gui.graphs.GraphsTab;
import gui.settings.*;
import gui.GUI;

public class BackendManager implements WindowListener
{
	private List<BackendListener> backendListeners;
	private Memory memory;
	private GUI mainGUI;
	private AddLeadFrame addLeadFrame;
	private EditLeadFrame editLeadFrame;
	private AdvancedSearchLeadsFrame advancedSearchLeadsFrame;
	DeleteCurrentLeadFrame deleteCurrentLeadFrame;
	AdvancedSearchRemindersFrame advancedSearchRemindersFrame;
	private ViewActionReminderFrame viewActionReminderFrame;
	private EditActionReminderFrame editActionReminderFrame;
	private AddActionReminderFrame addActionReminderFrame;
	private DeleteActionReminderFrame deleteActionReminderFrame;
	Validator validator;
	boolean mActionReminderRemoved;
	boolean mCurrentLeadRemoved;
	boolean mHistoricalLeadRemoved;
	
	//Upon construction...
	public BackendManager()
	{
		//Declare an extensible of BackendListeners
		backendListeners = new ArrayList<BackendListener>();
		//Load data from files to memory
		memory = new Memory(this);
		//Start the GUI
		mainGUI = new GUI(this);
		validator = new Validator(this);
		
		//Show action reminders
		mainGUI.getTabbedPane().setSelectedIndex(2);
		
		//Display today's action reminders
		for(ActionReminder reminder : memory.getActionReminders())
		{
			if(Integer.parseInt(reminder.getActionDay()) == memory.getCurrentDay() && Integer.parseInt(reminder.getActionMonth()) == memory.getCurrentMonth() && Integer.parseInt(reminder.getActionYear()) == memory.getCurrentYear())
				createViewActionReminderFrame(reminder.getID());
		}
		
		mActionReminderRemoved = false;
		
		//If set to do so, ask to delete old action reminders
		if(memory.getSettings().getAutomaticallyDeleteActionReminders())
		{
			for(int i = 0; i < memory.getActionReminders().size(); i++)
			{
				ActionReminder reminder = memory.getActionReminders().get(i);
				if(Validator.firstDateAfterSecond(memory.getCurrentDay(), memory.getCurrentMonth(), memory.getCurrentYear(), Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear())))
				{
					createDeleteActionReminderFrame(reminder.getID());
					if(mActionReminderRemoved)
					{
						i--;
						mActionReminderRemoved = false;
					}
				}
			}
		}
		
		mCurrentLeadRemoved = false;
		//If set to do so, ask to delete old current leads
		if(memory.getSettings().getAutomaticallyDeleteLeads())
		{
			//Show current leads
			mainGUI.getTabbedPane().setSelectedIndex(0);
			for(int i = 0; i < memory.getCurrentLeads().size(); i++)
			{
				CurrentLead lead = memory.getCurrentLeads().get(i);
				if(Validator.firstDateAfterSecond(memory.getCurrentDay(), memory.getCurrentMonth(), memory.getCurrentYear(), Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())))
				{
					createDeleteCurrentLeadFrame(lead.getID(), memory.getSettings().getSendLeadsToHistorical());
					if(mCurrentLeadRemoved)
					{
						i--;
						mCurrentLeadRemoved = false;
					}
				}
			}
		}
		
		mHistoricalLeadRemoved = false;
		//If deleting historical data...
		if(memory.getSettings().getAutomaticallyDeleteHistoricalLeads())
		{
			//Show historical data
			mainGUI.getTabbedPane().setSelectedIndex(4);
			//Sort leads (newest first)
			memory.sortHistoricalLeads();
			//If deleting based on number...
			if(memory.getSettings().getDeleteAllButNewestNHistoricalLeads())
			{
				int numberToKeep = memory.getSettings().getNumberOfNewestHistoricalLeadsToKeep();
				//While there are leads to delete...
				for(int i = 1; numberToKeep < memory.getHistoricalLeads().size(); i++)
				{
					//Ask to delete the oldest lead
					createDeleteHistoricalLeadFrame(memory.getHistoricalLeads().size() - i);
					//If the lead was deleted...
					if(mHistoricalLeadRemoved)
					{
						//Keep i the same for the next loop so as to not skip a lead
						i--;
						mHistoricalLeadRemoved = false;
					}
					else
						numberToKeep++;
				}
			}
			//If deleting based on age...
			else
			{
				int maximumAgeInDays = memory.getSettings().getMaximumAgeOfHistoricalLeadsInDays();
				//While there are leads to delete...
				for(int i = 1; true; i++)
				{
					//If the oldest historical lead is too old...
					if((int)Math.floorDiv((long)((new Date()).getTime() - memory.getHistoricalLeads().get(memory.getHistoricalLeads().size() - i).getDealDateAsDate().getTime()), (long)86400000) > maximumAgeInDays)
					{
						//Ask to delete the oldest lead
						createDeleteHistoricalLeadFrame(memory.getHistoricalLeads().size() - i);
						//If the lead was deleted...
						if(mHistoricalLeadRemoved)
						{
							//Keep i the same for the next loop so as to not skip a lead
							i--;
							mHistoricalLeadRemoved = false;
						}
					}
					else
						break;
				}
			}
		}
		
		//Show current leads
		mainGUI.getTabbedPane().setSelectedIndex(0);
	}
	
	public void addListener(BackendListener listener)
	{
		//Add the object to the extensible array of BackendListeners
		backendListeners.add(listener);
	}
	
	public String[] getClientNames(boolean includingDash)
	{
		String[] clientNames;
		if(includingDash)
		{
			//Make an array storing the names of clients, starting with "-"
			clientNames = new String[memory.getClients().size() + 1];
			clientNames[0] = "-";
			for(int i = 0; i < memory.getClients().size(); i++)
				clientNames[i+1] = memory.getClients().get(i).getName();
		}
		else
		{
			//Make an array storing the names of clients
			clientNames = new String[memory.getClients().size()];
			for(int i = 0; i < memory.getClients().size(); i++)
				clientNames[i] = memory.getClients().get(i).getName();
		}
		return clientNames;
	}
	
	public String[] getNamesOfStagesOfNegotiation(boolean includingDash)
	{
		String[] namesOfStages;
		if(includingDash)
		{
			//Make an array storing the names of stages of negotiation, starting with "-"
			namesOfStages = new String[memory.getStagesOfNegotiation().size() + 1];
			namesOfStages[0] = "-";
			for(int i = 1; i < memory.getStagesOfNegotiation().size() + 1; i++)
				namesOfStages[i] = memory.getStagesOfNegotiation().get(i-1).getNameOfStage();
		}
		else
		{
			namesOfStages = new String[memory.getStagesOfNegotiation().size()];
			for(int i = 0; i < memory.getStagesOfNegotiation().size(); i++)
				namesOfStages[i] = memory.getStagesOfNegotiation().get(i).getNameOfStage();
		}
		return namesOfStages;
	}
	
	public int[] getDefaultProbabilities()
	{
		int[] defaultProbabilities = new int[memory.getStagesOfNegotiation().size()];
		for(int i = 0; i < memory.getStagesOfNegotiation().size(); i++)
				defaultProbabilities[i] = memory.getStagesOfNegotiation().get(i).getDefaultProbability();
		return defaultProbabilities;
	}
	
	//Make an array of the next few years, starting with "-"
	private String[] getYearsWithDash()
	{
		String[] listOfYears = new String[memory.getListOfFutureYears().length + 1];
		listOfYears[0] = "-";
		for(int i = 1; i < memory.getListOfFutureYears().length + 1; i++)
			listOfYears[i] = memory.getListOfFutureYears()[i-1];
		return listOfYears;
	}
	
	//Make an array of months in the format "MM", starting with "-"
	private String[] getMonthsWithDash()
	{
		String[] listOfMonths = new String[13];
		listOfMonths[0] = "-";
		for(int i = 1; i < 13; i++)
			listOfMonths[i] = memory.getListOfMonths()[i-1];
		return listOfMonths;
	}
	
	//Make an array of days in the format "DD", starting with "-"
	private String[] getDaysWithDash()
	{
		String[] listOfDays = new String[32];
		listOfDays[0] = "-";
		for(int i = 1; i < 32; i++)
			listOfDays[i] = memory.getListOfDays()[i-1];
		return listOfDays;
	}
	
	//Make an array of the numbers 0 to 100, starting with "-"
	private String[] getProbabilitiesWithDash()
	{
		String[] probabilitiesInPercent = new String[102];
		probabilitiesInPercent[0] = "-";
		for(int i = 1; i < 102; i++)
			probabilitiesInPercent[i] = memory.getZeroToOneHundred()[i-1].toString();
		return probabilitiesInPercent;
	}
	
	//Returns true if successful
	public boolean addCurrentLead(int clientID, Object dealDay, Object dealMonth, Object dealYear, String dealValue, int stageOfNegotiationsID, Object probability, boolean addingReminder, Object reminderDay, Object reminderMonth, Object reminderYear, String reminderMessage, boolean makingNewClient)
	{	
		//If the deal value isn't an integer...
		if(!Validator.isAnInteger(dealValue))
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value must be an integer.");
			return false;
		}
		
		CurrentLead lead = new CurrentLead(memory.getCurrentLeads().size(), clientID, dealDay.toString(), dealMonth.toString(), Integer.parseInt(dealYear.toString()), Integer.parseInt(dealValue), stageOfNegotiationsID, Integer.parseInt(probability.toString()));
		
		//If the lead is not valid, don't save any data
		if(!validator.isValid(lead))
			return false;
		
		if(makingNewClient)
		{
			int numberOfClients = memory.getClients().size();
			createAddClientFrame();
			//If a new client has not been added...
			if(memory.getClients().size() <= numberOfClients)
				return false;
			lead.immediatelySetClientID(memory.getClients().size() - 1);
		}
		
		//Add the lead to memory
		memory.add(lead);
		
		if(addingReminder)
		{
			//If adding the reminder is not successful...
			if(!addActionReminder(reminderDay.toString(), reminderMonth.toString(), reminderYear.toString(), reminderMessage, lead.getID()))
			{
				memory.removeCurrentLead(lead.getID());
				
				if(makingNewClient)
				{
					memory.removeClient(memory.getClients().size() - 1, true);
					JOptionPane.showMessageDialog(mainGUI, "Client was not saved.");
				}
				return false;
			}
		}
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean addActionReminder(Object reminderDay, Object reminderMonth, Object reminderYear, String message, int leadIndex)
	{
		ActionReminder reminder = new ActionReminder(memory.getActionReminders().size(), leadIndex, reminderDay.toString(), reminderMonth.toString(), Integer.parseInt(reminderYear.toString()), message);
		
		if(!validator.isValid(reminder))
			return false;
		
		memory.add(reminder);
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean addClient(String clientName, String emailAddress, String phoneNumber)
	{
		Client client = new Client(memory.getClients().size(), clientName, phoneNumber, emailAddress);
		
		if(!validator.isValid(client, -1))
			return false;
		
		memory.add(client);
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean addHistoricalLead(boolean makingNewClient, int clientIndexAddOne, Object dealDay, Object dealMonth, Object dealYear, String dealValue, int stageOfNegotiationsIndexAddOne, boolean succeeded)
	{
		//If the deal value isn't an integer...
		if(!Validator.isAnInteger(dealValue))
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value must be an integer.");
			return false;
		}
		
		HistoricalLead lead = new HistoricalLead(memory.getHistoricalLeads().size(), clientIndexAddOne - 1, (String) dealDay, (String) dealMonth, Integer.parseInt((String) dealYear), Integer.parseInt(dealValue), stageOfNegotiationsIndexAddOne - 1, succeeded);
		
		if(!validator.isValid(lead))
			return false;
		
		if(makingNewClient)
		{
			int numberOfClients = memory.getClients().size();
			createAddClientFrame();
			//If a new client has not been added...
			if(memory.getClients().size() <= numberOfClients)
				return false;
			lead.immediatelySetClientID(memory.getClients().size() - 1);
		}
		
		memory.add(lead);
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean addStageOfNegotiation(String nameOfStage, Object probability, boolean moveLeads, boolean assumeSuccessful)
	{
		StageOfNegotiation stage = new StageOfNegotiation(memory.getStagesOfNegotiation().size(), nameOfStage, Integer.parseInt(probability.toString()), moveLeads, assumeSuccessful);
		
		if(!validator.isValid(stage, -1))
			return false;
		
		memory.add(stage);
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean restoreToCurrent(int clientID, Object dealDay, Object dealMonth, Object dealYear, String dealValue, int stageOfNegotiationsID, Object probability, int historicalLeadID, boolean makingNewClient)
	{
		//If the deal value isn't an integer...
		if(!Validator.isAnInteger(dealValue))
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value must be an integer.");
			return false;
		}
		
		CurrentLead lead = new CurrentLead(memory.getCurrentLeads().size(), clientID, dealDay.toString(), dealMonth.toString(), Integer.parseInt(dealYear.toString()), Integer.parseInt(dealValue), stageOfNegotiationsID, Integer.parseInt(probability.toString()));
		
		//If the lead is not valid, don't save any data
		if(!validator.isValid(lead))
			return false;
		
		if(makingNewClient)
		{
			int numberOfClients = memory.getClients().size();
			createAddClientFrame();
			//If a new client has not been added...
			if(memory.getClients().size() <= numberOfClients)
				return false;
			lead.immediatelySetClientID(memory.getClients().size() - 1);
		}
		
		//Add the lead to memory
		memory.add(lead);
		memory.removeHistoricalLead(historicalLeadID);
		notifyBackendListeners();
		//Go to the Current leads tab
		mainGUI.getTabbedPane().setSelectedIndex(0);
		return true;
	}
	
	//Returns true if successful
	public boolean editLead(int clientID, Object dealDay, Object dealMonth, Object dealYear, String dealValue, int stageOfNegotiationsID, Object probability, int leadID, boolean makingNewClient)
	{
		//If the deal value isn't an integer...
		if(!Validator.isAnInteger(dealValue))
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value must be an integer.");
			return false;
		}
		
		CurrentLead lead = new CurrentLead(memory.getCurrentLeads().size(), clientID, dealDay.toString(), dealMonth.toString(), Integer.parseInt(dealYear.toString()), Integer.parseInt(dealValue), stageOfNegotiationsID, Integer.parseInt(probability.toString()));
		
		//Create a list of this lead's action reminders
		ArrayList<ActionReminder> reminders = new ArrayList<ActionReminder>();
		
		//For each of the lead's action reminders...
		for(int reminderIndex : memory.getCurrentLeads().get(leadID).getActionReminderIDs())
		{
			ActionReminder reminder = memory.getActionReminders().get(reminderIndex);
			//If the reminder's date comes after the lead's new close date...
			if(Validator.firstDateAfterSecond(Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear()), Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())))
			{
				JOptionPane.showMessageDialog(mainGUI, "Lead close date is before an action reminder date.");
				return false;
			}
			
			//Add it to the list
			reminders.add(memory.getActionReminders().get(reminderIndex));
			
			lead.immediatelyAddActionReminderKey(reminderIndex);
		}
		
		//If the lead is not valid, don't save any data
		if(!validator.isValid(lead))
			return false;
		
		if(makingNewClient)
		{
			int numberOfClients = memory.getClients().size();
			createAddClientFrame();
			//If a new client has not been added...
			if(memory.getClients().size() <= numberOfClients)
				return false;
			lead.immediatelySetClientID(memory.getClients().size() - 1);
		}
		
		lead.removeActionReminderKeys();
		
		memory.removeCurrentLead(leadID);
		
		//If moving leads with this stage of negotiation...
		if(memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).leadShouldBeMoved())
		{
			memory.add(new HistoricalLead(memory.getHistoricalLeads().size(), lead.getClientID(), lead.getDealDay(), lead.getDealMonth(), Integer.parseInt(lead.getDealYear()), lead.getValue(), lead.getStageOfNegotiationsID(), (lead.getProbability() == 100 ? true : memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).assumingSuccessfulIfMoved()) /*whether to assume the lead was successful*/));
		}
		else
		{
			memory.add(lead);
			//For each of the  lead's reminders...
			for(ActionReminder reminder : reminders)
			{
				reminder.immediatelySetCurrentLeadID(lead.getID());
				memory.add(reminder);
			}
		}
		
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean editActionReminder(Object reminderDay, Object reminderMonth, Object reminderYear, String message, int reminderIndex)
	{
		ActionReminder reminder = new ActionReminder(memory.getActionReminders().size(), memory.getActionReminders().get(reminderIndex).getCurrentLeadID(), reminderDay.toString(), reminderMonth.toString(), Integer.parseInt((String) reminderYear), message);
		
		if(!validator.isValid(reminder))
			return false;
		
		memory.removeActionReminder(reminderIndex);
		memory.add(reminder);
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean editClient(String clientName, String emailAddress, String phoneNumber, int clientID)
	{
		if(!validator.isValid(new Client(memory.getClients().size(), clientName, phoneNumber, emailAddress), clientID))
			return false;
		
		memory.getClients().get(clientID).setName(clientName);
		memory.getClients().get(clientID).setEmailAddress(emailAddress);
		memory.getClients().get(clientID).setPhoneNumber(phoneNumber);
		
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful
	public boolean editHistoricalLead(boolean makingNewClient, int clientIndexAddOne, Object dealDay, Object dealMonth, Object dealYear, String dealValue, int stageOfNegotiationsIndexAddOne, boolean succeeded, int leadID)
	{
		//If the deal value isn't an integer...
		if(!Validator.isAnInteger(dealValue))
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value must be an integer.");
			return false;
		}
		
		HistoricalLead lead = new HistoricalLead(memory.getHistoricalLeads().size(), clientIndexAddOne - 1, (String) dealDay, (String) dealMonth, Integer.parseInt((String) dealYear), Integer.parseInt(dealValue), stageOfNegotiationsIndexAddOne - 1, succeeded);
		
		if(!validator.isValid(lead))
			return false;
		
		if(makingNewClient)
		{
			int numberOfClients = memory.getClients().size();
			createAddClientFrame();
			//If a new client has not been added...
			if(memory.getClients().size() <= numberOfClients)
				return false;
			lead.immediatelySetClientID(memory.getClients().size() - 1);
		}
		
		memory.removeHistoricalLead(leadID);
		memory.add(lead);
		notifyBackendListeners();
		return true;
	}
	
	//Returns true is successful
	public boolean editStageOfNegotiation(int stageID, String nameOfStage, Object probability, boolean moveLeads, boolean assumeSuccessful)
	{
		if(!validator.isValid(new StageOfNegotiation(memory.getStagesOfNegotiation().size(), nameOfStage, Integer.parseInt(probability.toString()), moveLeads, assumeSuccessful), stageID))
			return false;
		
		memory.getStagesOfNegotiation().get(stageID).setNameOfStage(nameOfStage);
		memory.getStagesOfNegotiation().get(stageID).setDefaultProbability(Integer.parseInt(probability.toString()));
		memory.getStagesOfNegotiation().get(stageID).setMoveLeads(moveLeads);
		memory.getStagesOfNegotiation().get(stageID).setAssumingSuccessful(assumeSuccessful);
		memory.sortStagesOfNegotiation();
		
		notifyBackendListeners();
		return true;
	}
	
	public void deleteCurrentLead(int leadID, boolean sendingToHistorical, boolean successful)
	{
		CurrentLead lead = memory.getCurrentLeads().get(leadID);
		memory.removeCurrentLead(leadID);
		
		if(sendingToHistorical)
			memory.add(new HistoricalLead(memory.getHistoricalLeads().size(), lead.getClientID(), lead.getDealDay(), lead.getDealMonth(), Integer.parseInt(lead.getDealYear()), lead.getValue(), lead.getStageOfNegotiationsID(), successful));
		
		notifyBackendListeners();
	}
	
	public void deleteActionReminder(int reminderIndex)
	{
		memory.removeActionReminder(reminderIndex);
		notifyBackendListeners();
	}
	
	public void deleteClient(int clientID, boolean deletingHistoricalLeads)
	{
		memory.removeClient(clientID, deletingHistoricalLeads);
		notifyBackendListeners();
	}
	
	public void deleteHistoricalLead(int leadID, boolean deletingClient)
	{
		if(deletingClient)
			createDeleteClientFrame(memory.getHistoricalLeads().get(leadID).getClientID(), true);
		memory.removeHistoricalLead(leadID);
		notifyBackendListeners();
	}
	
	public void deleteStageOfNegotiation(int stageIndex)
	{
		memory.removeStageOfNegotiation(stageIndex);
		notifyBackendListeners();
	}
	
	//Returns true if successful, as well as setting the list of filtered current leads
	public boolean advancedSearchCurrentLeads(int clientIndex, boolean notThisClient, String minimumDealValueString, String maximumDealValueString, String minimumProbabilityString, String maximumProbabilityString, int stageOfNegotiationsIndex, boolean notThisStageOfNegotiations, String dealEarliestDayString, String dealEarliestMonthString, String dealEarliestYearString, String dealLatestDayString, String dealLatestMonthString, String dealLatestYearString, String actionReminderEarliestDayString, String actionReminderEarliestMonthString, String actionReminderEarliestYearString, String actionReminderLatestDayString, String actionReminderLatestMonthString, String actionReminderLatestYearString)
	{
		int minimumDealValue = 0;
		int maximumDealValue = Validator.getMaximumDealValue();
		int minimumProbability = 0;
		int maximumProbability = 100;
		int dealEarliestYear = Integer.parseInt(memory.getListOfFutureYears()[0]);
		int actionReminderEarliestYear = dealEarliestYear;
		int dealEarliestMonth = 1;
		int actionReminderEarliestMonth = 1;
		int dealEarliestDay = 1;
		int actionReminderEarliestDay = 1;
		int dealLatestYear = Integer.parseInt(memory.getListOfFutureYears()[memory.getListOfFutureYears().length - 1]);
		int actionReminderLatestYear = dealLatestYear;
		int dealLatestMonth = 12;
		int actionReminderLatestMonth = 12;
		int dealLatestDay = 31;
		int actionReminderLatestDay = 31;
		
		//If a (minimum) deal value has been entered...
		if(!minimumDealValueString.isEmpty())
		{
			try
			{
				minimumDealValue = Integer.parseInt(minimumDealValueString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum deal value was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(minimumDealValue))
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum deal value was not an acceptable value.");
				return false;
			}
		}
		
		//If a maximum deal value has been entered...
		if(!maximumDealValueString.isEmpty())
		{
			try
			{
				maximumDealValue = Integer.parseInt(maximumDealValueString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum deal value was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(maximumDealValue))
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum deal value was not an acceptable value.");
				return false;
			}
		}
		
		if(minimumDealValue > maximumDealValue)
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value minimum is greater than maximum.");
			return false;
		}
		
		//If a minimum deal probability has been entered...
		if(!minimumProbabilityString.equals("-"))
		{
			try
			{
				minimumProbability = Integer.parseInt(minimumProbabilityString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "(Minimum) probability was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableProbability(minimumProbability))
			{
				JOptionPane.showMessageDialog(mainGUI, "(Minimum) probability was not an acceptable value.");
				return false;
			}
		}
		
		//If a maximum deal probability has been entered...
		if(!maximumProbabilityString.equals("-"))
		{
			try
			{
				maximumProbability = Integer.parseInt(maximumProbabilityString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum probability was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableProbability(maximumProbability))
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum probability was not an acceptable value.");
				return false;
			}
		}
		
		if(minimumProbability > maximumProbability)
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal probability minimum is greater than maximum.");
			return false;
		}
		
		
		//If an earliest deal year has been entered...
		if(!dealEarliestYearString.equals("-"))
		{
			try
			{
				dealEarliestYear = Integer.parseInt(dealEarliestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal month has been entered...
		if(!dealEarliestMonthString.equals("-"))
		{
			//If no earliest deal year has been entered...
			if(dealEarliestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestMonth = Integer.parseInt(dealEarliestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal day has been entered...
		if(!dealEarliestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealEarliestYearString.equals("-") || dealEarliestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestDay = Integer.parseInt(dealEarliestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealEarliestDay, dealEarliestMonth, dealEarliestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal month has been entered...
		if(!dealLatestMonthString.equals("-"))
		{
			//If no latest deal year has been entered...
			if(dealLatestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestMonth = Integer.parseInt(dealLatestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If a latest deal day has been entered...
		if(!dealLatestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealLatestYearString.equals("-") || dealLatestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				dealLatestDay = Integer.parseInt(dealLatestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealLatestDay, dealLatestMonth, dealLatestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal year has been entered...
		if(!dealLatestYearString.equals("-"))
		{
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If an earliest deal year has been entered...
			if(!dealEarliestYearString.equals("-"))
			{
				if(dealEarliestYear > dealLatestYear)
				{
					JOptionPane.showMessageDialog(mainGUI, "Earliest deal year is later than latest.");
					return false;
				}
				if(dealEarliestYear == dealLatestYear)
				{
					//If a latest and earliest deal month have both been entered...
					if(!dealLatestMonthString.equals("-") && !dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth > dealLatestMonth)
						{
							JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
							return false;
						}
						if(dealEarliestMonth == dealLatestMonth)
						{
							//If a latest and earliest deal day have both been entered...
							if(!dealLatestDayString.equals("-") && !dealEarliestDayString.equals("-"))
							{
								if(dealEarliestDay > dealLatestDay)
								{
									JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		
		//If an earliest action year has been entered...
		if(!actionReminderEarliestYearString.equals("-"))
		{
			try
			{
				actionReminderEarliestYear = Integer.parseInt(actionReminderEarliestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest action month has been entered...
		if(!actionReminderEarliestMonthString.equals("-"))
		{
			//If no earliest action year has been entered...
			if(actionReminderEarliestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderEarliestMonth = Integer.parseInt(actionReminderEarliestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest action day has been entered...
		if(!actionReminderEarliestDayString.equals("-"))
		{
			//If no earliest action year or action month has been entered...
			if(actionReminderEarliestYearString.equals("-") || actionReminderEarliestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderEarliestDay = Integer.parseInt(actionReminderEarliestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(actionReminderEarliestDay, actionReminderEarliestMonth, actionReminderEarliestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest action month has been entered...
		if(!actionReminderLatestMonthString.equals("-"))
		{
			//If no latest action year has been entered...
			if(actionReminderLatestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderLatestMonth = Integer.parseInt(actionReminderLatestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If a latest action day has been entered...
		if(!actionReminderLatestDayString.equals("-"))
		{
			//If no earliest action year or action month has been entered...
			if(actionReminderLatestYearString.equals("-") || actionReminderLatestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderLatestYear = Integer.parseInt(actionReminderLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				actionReminderLatestDay = Integer.parseInt(actionReminderLatestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(actionReminderLatestDay, actionReminderLatestMonth, actionReminderLatestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest action year has been entered...
		if(!actionReminderLatestYearString.equals("-"))
		{
			try
			{
				actionReminderLatestYear = Integer.parseInt(actionReminderLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If an earliest action year has been entered...
			if(!actionReminderEarliestYearString.equals("-"))
			{
				if(actionReminderEarliestYear > actionReminderLatestYear)
				{
					JOptionPane.showMessageDialog(mainGUI, "Earliest action year is later than latest.");
					return false;
				}
				if(actionReminderEarliestYear == actionReminderLatestYear)
				{
					//If a latest and earliest action month have both been entered...
					if(!actionReminderLatestMonthString.equals("-") && !actionReminderEarliestMonthString.equals("-"))
					{
						if(actionReminderEarliestMonth > actionReminderLatestMonth)
						{
							JOptionPane.showMessageDialog(mainGUI, "Earliest action date is later than latest.");
							return false;
						}
						if(actionReminderEarliestMonth == actionReminderLatestMonth)
						{
							//If a latest and earliest action day have both been entered...
							if(!actionReminderLatestDayString.equals("-") && !actionReminderEarliestDayString.equals("-"))
							{
								if(actionReminderEarliestDay > actionReminderLatestDay)
								{
									JOptionPane.showMessageDialog(mainGUI, "Earliest action date is later than latest.");
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		//Start the search
		memory.setFilteredCurrentLeads(new ArrayList<CurrentLead>());
		//For each current lead...
		for(CurrentLead lead : memory.getCurrentLeads())
		{
			//If a client has been chosen...
			if(clientIndex >= 0)
			{
				//If the client matches the selected one...
				if(lead.getClientID() == clientIndex)
				{
					//If matches are being rejected...
					if(notThisClient)
						//Go to the next lead
						continue;
				}
				//If the client does not match the selected one, and only matches are being accepted...
				else if(!notThisClient)
					//Go to the next lead
					continue;
			}
			
			//If a latest deal year has been entered...
			if(!dealLatestYearString.equals("-"))
			{
				//If the lead's date is outside the input range, don't include it
				if(Validator.firstDateAfterSecond(dealEarliestDay, dealEarliestMonth, dealEarliestYear, Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())) || Validator.firstDateAfterSecond(Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear()), dealLatestDay, dealLatestMonth, dealLatestYear))
					continue;
			}
			//If a latest deal year has not been entered...
			else
			{	
				//If an earliest deal year has been entered... 
				if(!dealEarliestYearString.equals("-"))
				{
					if(dealEarliestYear != Integer.parseInt(lead.getDealYear()))
						continue;
					//If an earliest deal month has been entered...
					if(!dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth != Integer.parseInt(lead.getDealMonth()))
							continue;
						//If an earliest deal day has been entered but doesn't match that of the lead...
						if(!dealEarliestDayString.equals("-") && dealEarliestDay != Integer.parseInt(lead.getDealDay()))
							continue;
					}
				}
			}
			
			//If the input deal value is outside the range...
			if(maximumDealValue < lead.getValue() || minimumDealValue > lead.getValue())
				continue;
			//If a maximum deal value has not been entered, but a minimum value has, and the lead's value does not match this...
			if(maximumDealValueString.isEmpty() && !minimumDealValueString.isEmpty() && minimumDealValue != lead.getValue())
				continue;
				
			//If a stage of negotiation has been chosen...
			if(stageOfNegotiationsIndex >= 0)
			{
				//If the stage of negotiation matches that selected...
				if(lead.getStageOfNegotiationsID() == stageOfNegotiationsIndex)
				{
					//If rejecting matches...
					if(notThisStageOfNegotiations)
						//Go to the next lead
						continue;
				}
				//If the stage of negotiation does not match that selected, and if only accepting matches...
				else if(!notThisStageOfNegotiations)
					//Go to the next lead
					continue;
			}
			
			//If the input probability is outside the range...
			if(maximumProbability < lead.getProbability() || minimumProbability > lead.getProbability())
				continue;
			//If a maximum probability has not been entered, but a minimum probability has, and the lead's probability does not match this...
			if(maximumProbabilityString.equals("-") && !minimumProbabilityString.equals("-") && minimumProbability != lead.getProbability())
				continue;
			
			//If any action reminder search values have been input...
			if(!(actionReminderEarliestYearString.equals("-") && actionReminderLatestYearString.equals("-")))
			{
				boolean leadHasMatchingReminder = false;
				
				//For each of the lead's reminders...
				for(int reminderIndex : lead.getActionReminderIDs())
				{
					ActionReminder reminder = memory.getActionReminders().get(reminderIndex);
					//If a latest action year has been entered...
					if(!actionReminderLatestYearString.equals("-"))
					{
						//If the reminder's date is outside the input range, go to the next reminder
						if(Validator.firstDateAfterSecond(actionReminderEarliestDay, actionReminderEarliestMonth, actionReminderEarliestYear, Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear())) || Validator.firstDateAfterSecond(Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear()), actionReminderLatestDay, actionReminderLatestMonth, actionReminderLatestYear))
							continue;
					}
					//If a latest action year has not been entered...
					else
					{	
						//If an earliest action year has been entered... 
						if(!actionReminderEarliestYearString.equals("-"))
						{
							if(actionReminderEarliestYear != Integer.parseInt(reminder.getActionYear()))
								continue;
							//If an earliest action month has been entered...
							if(!actionReminderEarliestMonthString.equals("-"))
							{
								if(actionReminderEarliestMonth != Integer.parseInt(reminder.getActionMonth()))
									continue;
								//If an earliest action day has been entered but doesn't match that of the reminder...
								if(!actionReminderEarliestDayString.equals("-") && actionReminderEarliestDay != Integer.parseInt(reminder.getActionDay()))
									continue;
							}
						}
					}
					//If the reminder matches...
					leadHasMatchingReminder = true;
					break;
				}
				
				//If the lead has no matching reminder, go to the next lead
				if(!leadHasMatchingReminder)
					continue;
			}
			
			memory.getFilteredCurrentLeads().add(lead);
		}
		
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful, as well as setting the list of filtered action reminders
	public boolean advancedSearchActionReminders(int clientIndex, boolean notThisClient, String minimumDealValueString, String maximumDealValueString, String minimumProbabilityString, String maximumProbabilityString, String message, boolean notThisMessage, String dealEarliestDayString, String dealEarliestMonthString, String dealEarliestYearString, String dealLatestDayString, String dealLatestMonthString, String dealLatestYearString, String actionReminderEarliestDayString, String actionReminderEarliestMonthString, String actionReminderEarliestYearString, String actionReminderLatestDayString, String actionReminderLatestMonthString, String actionReminderLatestYearString)
	{
		int minimumDealValue = 0;
		int maximumDealValue = Validator.getMaximumDealValue();
		int minimumProbability = 0;
		int maximumProbability = 100;
		int dealEarliestYear = Integer.parseInt(memory.getListOfFutureYears()[0]);
		int dealEarliestMonth = 1;
		int dealEarliestDay = 1;
		int dealLatestYear = Integer.parseInt(memory.getListOfFutureYears()[memory.getListOfFutureYears().length - 1]);
		int dealLatestMonth = 12;
		int dealLatestDay = 31;
		int actionReminderEarliestYear = Integer.parseInt(memory.getListOfFutureYears()[0]);
		int actionReminderEarliestMonth = 1;
		int actionReminderEarliestDay = 1;
		int actionReminderLatestYear = Integer.parseInt(memory.getListOfFutureYears()[memory.getListOfFutureYears().length - 1]);
		int actionReminderLatestMonth = 12;
		int actionReminderLatestDay = 31;
		
		//If a (minimum) deal value has been entered...
		if(!minimumDealValueString.isEmpty())
		{
			try
			{
				minimumDealValue = Integer.parseInt(minimumDealValueString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum deal value was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(minimumDealValue))
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum deal value was not an acceptable value.");
				return false;
			}
		}
		
		//If a maximum deal value has been entered...
		if(!maximumDealValueString.isEmpty())
		{
			try
			{
				maximumDealValue = Integer.parseInt(maximumDealValueString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum deal value was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(maximumDealValue))
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum deal value was not an acceptable value.");
				return false;
			}
		}
		
		if(minimumDealValue > maximumDealValue)
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value minimum is greater than maximum.");
			return false;
		}
		
		//If a minimum deal probability has been entered...
		if(!minimumProbabilityString.equals("-"))
		{
			try
			{
				minimumProbability = Integer.parseInt(minimumProbabilityString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "(Minimum) probability was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableProbability(minimumProbability))
			{
				JOptionPane.showMessageDialog(mainGUI, "(Minimum) probability was not an acceptable value.");
				return false;
			}
		}
		
		//If a maximum deal probability has been entered...
		if(!maximumProbabilityString.equals("-"))
		{
			try
			{
				maximumProbability = Integer.parseInt(maximumProbabilityString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum probability was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableProbability(maximumProbability))
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum probability was not an acceptable value.");
				return false;
			}
		}
		
		if(minimumProbability > maximumProbability)
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal probability minimum is greater than maximum.");
			return false;
		}
		
		
		//If an earliest deal year has been entered...
		if(!dealEarliestYearString.equals("-"))
		{
			try
			{
				dealEarliestYear = Integer.parseInt(dealEarliestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal month has been entered...
		if(!dealEarliestMonthString.equals("-"))
		{
			//If no earliest deal year has been entered...
			if(dealEarliestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestMonth = Integer.parseInt(dealEarliestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal day has been entered...
		if(!dealEarliestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealEarliestYearString.equals("-") || dealEarliestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestDay = Integer.parseInt(dealEarliestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealEarliestDay, dealEarliestMonth, dealEarliestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal month has been entered...
		if(!dealLatestMonthString.equals("-"))
		{
			//If no latest deal year has been entered...
			if(dealLatestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestMonth = Integer.parseInt(dealLatestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If a latest deal day has been entered...
		if(!dealLatestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealLatestYearString.equals("-") || dealLatestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				dealLatestDay = Integer.parseInt(dealLatestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealLatestDay, dealLatestMonth, dealLatestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal year has been entered...
		if(!dealLatestYearString.equals("-"))
		{
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If an earliest deal year has been entered...
			if(!dealEarliestYearString.equals("-"))
			{
				if(dealEarliestYear > dealLatestYear)
				{
					JOptionPane.showMessageDialog(mainGUI, "Earliest deal year is later than latest.");
					return false;
				}
				if(dealEarliestYear == dealLatestYear)
				{
					//If a latest and earliest deal month have both been entered...
					if(!dealLatestMonthString.equals("-") && !dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth > dealLatestMonth)
						{
							JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
							return false;
						}
						if(dealEarliestMonth == dealLatestMonth)
						{
							//If a latest and earliest deal day have both been entered...
							if(!dealLatestDayString.equals("-") && !dealEarliestDayString.equals("-"))
							{
								if(dealEarliestDay > dealLatestDay)
								{
									JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		//If an earliest action reminder year has been entered...
		if(!actionReminderEarliestYearString.equals("-"))
		{
			try
			{
				actionReminderEarliestYear = Integer.parseInt(actionReminderEarliestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest action reminder month has been entered...
		if(!actionReminderEarliestMonthString.equals("-"))
		{
			//If no earliest deal year has been entered...
			if(actionReminderEarliestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderEarliestMonth = Integer.parseInt(actionReminderEarliestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest action reminder day has been entered...
		if(!actionReminderEarliestDayString.equals("-"))
		{
			//If no earliest action reminder year or action reminder month has been entered...
			if(actionReminderEarliestYearString.equals("-") || actionReminderEarliestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderEarliestDay = Integer.parseInt(actionReminderEarliestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(actionReminderEarliestDay, actionReminderEarliestMonth, actionReminderEarliestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest action reminder month has been entered...
		if(!actionReminderLatestMonthString.equals("-"))
		{
			//If no latest action reminder year has been entered...
			if(actionReminderLatestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderLatestMonth = Integer.parseInt(actionReminderLatestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If a latest action reminder day has been entered...
		if(!actionReminderLatestDayString.equals("-"))
		{
			//If no earliest action reminder year or action reminder month has been entered...
			if(actionReminderLatestYearString.equals("-") || actionReminderLatestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				actionReminderLatestYear = Integer.parseInt(actionReminderLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				actionReminderLatestDay = Integer.parseInt(actionReminderLatestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(actionReminderLatestDay, actionReminderLatestMonth, actionReminderLatestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest action reminder year has been entered...
		if(!actionReminderLatestYearString.equals("-"))
		{
			try
			{
				actionReminderLatestYear = Integer.parseInt(actionReminderLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If an earliest action reminder year has been entered...
			if(!actionReminderEarliestYearString.equals("-"))
			{
				if(actionReminderEarliestYear > actionReminderLatestYear)
				{
					JOptionPane.showMessageDialog(mainGUI, "Earliest action reminder year is later than latest.");
					return false;
				}
				if(actionReminderEarliestYear == actionReminderLatestYear)
				{
					//If a latest and earliest action reminder month have both been entered...
					if(!actionReminderLatestMonthString.equals("-") && !actionReminderEarliestMonthString.equals("-"))
					{
						if(actionReminderEarliestMonth > actionReminderLatestMonth)
						{
							JOptionPane.showMessageDialog(mainGUI, "Earliest action reminder date is later than latest.");
							return false;
						}
						if(actionReminderEarliestMonth == actionReminderLatestMonth)
						{
							//If a latest and earliest action reminder day have both been entered...
							if(!actionReminderLatestDayString.equals("-") && !actionReminderEarliestDayString.equals("-"))
							{
								if(actionReminderEarliestDay > actionReminderLatestDay)
								{
									JOptionPane.showMessageDialog(mainGUI, "Earliest action reminder date is later than latest.");
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		//Start the search
		memory.setFilteredActionReminders(new ArrayList<ActionReminder>());
		//For each reminder...
		for(ActionReminder reminder : memory.getActionReminders())
		{
			CurrentLead lead = memory.getCurrentLeads().get(reminder.getCurrentLeadID());
			//If a client has been chosen...
			if(clientIndex >= 0)
			{
				//If the client matches the selected one...
				if(lead.getClientID() == clientIndex)
				{
					//If matches are being rejected...
					if(notThisClient)
						//Go to the next reminder
						continue;
				}
				//If the client does not match the selected one, and only matches are being accepted...
				else if(!notThisClient)
					//Go to the next reminder
					continue;
			}
			
			//If a latest reminder year has been entered...
			if(!actionReminderLatestYearString.equals("-"))
			{
				//If the reminder's date is outside the input range, go to the next reminder
				if(Validator.firstDateAfterSecond(actionReminderEarliestDay, actionReminderEarliestMonth, actionReminderEarliestYear, Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear())) || Validator.firstDateAfterSecond(Integer.parseInt(reminder.getActionDay()), Integer.parseInt(reminder.getActionMonth()), Integer.parseInt(reminder.getActionYear()), actionReminderLatestDay, actionReminderLatestMonth, actionReminderLatestYear))
					continue;
			}
			//If a latest action year has not been entered...
			else
			{	
				//If an earliest reminder year has been entered... 
				if(!actionReminderEarliestYearString.equals("-"))
				{
					if(actionReminderEarliestYear != Integer.parseInt(reminder.getActionYear()))
						continue;
					//If an earliest reminder month has been entered...
					if(!actionReminderEarliestMonthString.equals("-"))
					{
						if(actionReminderEarliestMonth != Integer.parseInt(reminder.getActionMonth()))
							continue;
						//If an earliest reminder day has been entered but doesn't match that of the action reminder...
						if(!actionReminderEarliestDayString.equals("-") && actionReminderEarliestDay != Integer.parseInt(reminder.getActionDay()))
							continue;
					}
				}
			}
			
			//If a latest deal year has been entered...
			if(!dealLatestYearString.equals("-"))
			{
				//If the lead's date is outside the input range, don't include the reminder
				if(Validator.firstDateAfterSecond(dealEarliestDay, dealEarliestMonth, dealEarliestYear, Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())) || Validator.firstDateAfterSecond(Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear()), dealLatestDay, dealLatestMonth, dealLatestYear))
					continue;
			}
			//If a latest deal year has not been entered...
			else
			{	
				//If an earliest deal year has been entered... 
				if(!dealEarliestYearString.equals("-"))
				{
					if(dealEarliestYear != Integer.parseInt(lead.getDealYear()))
						continue;
					//If an earliest deal month has been entered...
					if(!dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth != Integer.parseInt(lead.getDealMonth()))
							continue;
						//If an earliest deal day has been entered but doesn't match that of the lead...
						if(!dealEarliestDayString.equals("-") && dealEarliestDay != Integer.parseInt(lead.getDealDay()))
							continue;
					}
				}
			}
			
			//If the input deal value is outside the range...
			if(maximumDealValue < lead.getValue() || minimumDealValue > lead.getValue())
				continue;
			//If a maximum deal value has not been entered, but a minimum value has, and the lead's value does not match this...
			if(maximumDealValueString.isEmpty() && !minimumDealValueString.isEmpty() && minimumDealValue != lead.getValue())
				continue;
				
			//If a message has been entered...
			if(message.length() != 0)
			{
				//If the message contains matches the input...
				if(reminder.getMessage().contains(message))
				{
					//If rejecting matches...
					if(notThisMessage)
						//Go to the next reminder
						continue;
				}
				//If the message does not match that selected, and if only accepting matches...
				else if(!notThisMessage)
					//Go to the next lead
					continue;
			}
			
			//If the input probability is outside the range...
			if(maximumProbability < lead.getProbability() || minimumProbability > lead.getProbability())
				continue;
			//If a maximum probability has not been entered, but a minimum probability has, and the lead's probability does not match this...
			if(maximumProbabilityString.equals("-") && !minimumProbabilityString.equals("-") && minimumProbability != lead.getProbability())
				continue;
			
			memory.getFilteredActionReminders().add(reminder);
		}
		
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful, as well as setting the list of filtered clients
	public boolean advancedSearchClients(String clientName, String emailAddress, String phoneNumber, boolean notThisClientName, boolean notThisEmailAddress, boolean notThisPhoneNumber, String minimumExpectedIncomeString, String maximumExpectedIncomeString, String dealEarliestDayString, String dealEarliestMonthString, String dealEarliestYearString, String dealLatestDayString, String dealLatestMonthString, String dealLatestYearString)
	{
		int minimumExpectedIncome = 0;
		int maximumExpectedIncome = Validator.getMaximumDealValue() * 10;
		int dealEarliestYear = Integer.parseInt(memory.getListOfFutureYears()[0]);
		int dealEarliestMonth = 1;
		int dealEarliestDay = 1;
		int dealLatestYear = Integer.parseInt(memory.getListOfFutureYears()[memory.getListOfFutureYears().length - 1]);
		int dealLatestMonth = 12;
		int dealLatestDay = 31;
		
		//If a (minimum) expected income has been entered...
		if(!minimumExpectedIncomeString.isEmpty())
		{
			try
			{
				minimumExpectedIncome = Integer.parseInt(minimumExpectedIncomeString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum expected income was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(minimumExpectedIncome))
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum expected income was not an acceptable value.");
				return false;
			}
		}
		
		//If a maximum expected income has been entered...
		if(!maximumExpectedIncomeString.isEmpty())
		{
			try
			{
				maximumExpectedIncome = Integer.parseInt(maximumExpectedIncomeString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum expected income was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(maximumExpectedIncome))
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum expected income was not an acceptable value.");
				return false;
			}
		}
		
		if(minimumExpectedIncome > maximumExpectedIncome)
		{
			JOptionPane.showMessageDialog(mainGUI, "Expected income minimum is greater than maximum.");
			return false;
		}
		
		//If an earliest deal year has been entered...
		if(!dealEarliestYearString.equals("-"))
		{
			try
			{
				dealEarliestYear = Integer.parseInt(dealEarliestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal month has been entered...
		if(!dealEarliestMonthString.equals("-"))
		{
			//If no earliest deal year has been entered...
			if(dealEarliestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestMonth = Integer.parseInt(dealEarliestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal day has been entered...
		if(!dealEarliestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealEarliestYearString.equals("-") || dealEarliestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestDay = Integer.parseInt(dealEarliestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealEarliestDay, dealEarliestMonth, dealEarliestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal month has been entered...
		if(!dealLatestMonthString.equals("-"))
		{
			//If no latest deal year has been entered...
			if(dealLatestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestMonth = Integer.parseInt(dealLatestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If a latest deal day has been entered...
		if(!dealLatestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealLatestYearString.equals("-") || dealLatestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				dealLatestDay = Integer.parseInt(dealLatestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealLatestDay, dealLatestMonth, dealLatestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal year has been entered...
		if(!dealLatestYearString.equals("-"))
		{
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If an earliest deal year has been entered...
			if(!dealEarliestYearString.equals("-"))
			{
				if(dealEarliestYear > dealLatestYear)
				{
					JOptionPane.showMessageDialog(mainGUI, "Earliest deal year is later than latest.");
					return false;
				}
				if(dealEarliestYear == dealLatestYear)
				{
					//If a latest and earliest deal month have both been entered...
					if(!dealLatestMonthString.equals("-") && !dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth > dealLatestMonth)
						{
							JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
							return false;
						}
						if(dealEarliestMonth == dealLatestMonth)
						{
							//If a latest and earliest deal day have both been entered...
							if(!dealLatestDayString.equals("-") && !dealEarliestDayString.equals("-"))
							{
								if(dealEarliestDay > dealLatestDay)
								{
									JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		//Start the search
		memory.setFilteredClients(new ArrayList<Client>());
		//For each client...
		for(Client client : memory.getClients())
		{
			//If the client matches the selected one...
			if(client.getName().contains(clientName))
			{
				//If matches are being rejected...
				if(notThisClientName)
					//Go to the next lead
					continue;
			}
			//If the client does not match the selected one, and only matches are being accepted...
			else if(!notThisClientName)
				//Go to the next lead
				continue;
			
			//If the client matches the selected one...
			if(client.getEmailAddress().contains(emailAddress))
			{
				//If matches are being rejected...
				if(notThisEmailAddress)
					//Go to the next lead
					continue;
			}
			//If the client does not match the selected one, and only matches are being accepted...
			else if(!notThisEmailAddress)
				//Go to the next lead
				continue;
			
			//If the client matches the selected one...
			if(client.getPhoneNumber().contains(phoneNumber))
			{
				//If matches are being rejected...
				if(notThisPhoneNumber)
					//Go to the next lead
					continue;
			}
			//If the client does not match the selected one, and only matches are being accepted...
			else if(!notThisPhoneNumber)
				//Go to the next lead
				continue;
			
			//Calculate the client's expected income
			int expectedIncome = 0;
			for(int currentLeadID : client.getCurrentLeadIDs())
			{
				expectedIncome += (int) (memory.getCurrentLeads().get(currentLeadID).getValue() * (double) memory.getCurrentLeads().get(currentLeadID).getProbability() / 100 + 0.5);//Rounds for each lead.
			}
			
			//If the input expected income is outside the range...
			if(maximumExpectedIncome < expectedIncome || minimumExpectedIncome > expectedIncome)
				continue;
			//If a maximum expected income has not been entered, but a minimum value has, and the lead's value does not match this...
			if(maximumExpectedIncomeString.isEmpty() && !minimumExpectedIncomeString.isEmpty() && minimumExpectedIncome != expectedIncome)
				continue;
			
			//If the client has no current leads but a deal date has been entered, ignore this client
			if(client.getCurrentLeadIDs().length <= 0 && !(dealLatestYearString.equals("-") && dealEarliestYearString.equals("-")))
				continue;
			boolean clientHasLeadMatchingDate = true;
			//For each of the client's current leads...
			for(int currentLeadID : client.getCurrentLeadIDs())
			{
				clientHasLeadMatchingDate = false;
				CurrentLead lead = memory.getCurrentLeads().get(currentLeadID);
				//If a latest deal year has been entered...
				if(!dealLatestYearString.equals("-"))
				{
					//If the lead's date is outside the input range, go to the next lead
					if(Validator.firstDateAfterSecond(dealEarliestDay, dealEarliestMonth, dealEarliestYear, Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())) || Validator.firstDateAfterSecond(Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear()), dealLatestDay, dealLatestMonth, dealLatestYear))
						continue;	
				}
				//If a latest deal year has not been entered...
				else
				{	
					//If an earliest deal year has been entered... 
					if(!dealEarliestYearString.equals("-"))
					{
						if(dealEarliestYear != Integer.parseInt(lead.getDealYear()))
							continue;
						//If an earliest deal month has been entered...
						if(!dealEarliestMonthString.equals("-"))
						{
							if(dealEarliestMonth != Integer.parseInt(lead.getDealMonth()))
								continue;
							//If an earliest deal day has been entered but doesn't match that of the lead...
							if(!dealEarliestDayString.equals("-") && dealEarliestDay != Integer.parseInt(lead.getDealDay()))
								continue;
						}
					}
				}
				//If the lead is within range...
				clientHasLeadMatchingDate = true;
				break;
			}
			if(clientHasLeadMatchingDate)
				memory.getFilteredClients().add(client);
		}
		
		notifyBackendListeners();
		return true;
	}
	
	//Returns true if successful, as well as setting the filtered list of historical leads
	public boolean advancedSearchHistoricalLeads(int clientIndex, boolean notThisClient, String minimumDealValueString, String maximumDealValueString, int stageOfNegotiationsIndex, boolean notThisStageOfNegotiations, String dealEarliestDayString, String dealEarliestMonthString, String dealEarliestYearString, String dealLatestDayString, String dealLatestMonthString, String dealLatestYearString, boolean leadSucceeded, boolean leadFailed)
	{
		int minimumDealValue = 0;
		int maximumDealValue = Validator.getMaximumDealValue();
		int dealEarliestYear = Integer.parseInt(memory.getListOfFutureYears()[0]);
		int dealEarliestMonth = 1;
		int dealEarliestDay = 1;
		int dealLatestYear = Integer.parseInt(memory.getListOfFutureYears()[memory.getListOfFutureYears().length - 1]);
		int dealLatestMonth = 12;
		int dealLatestDay = 31;
		
		//If a (minimum) deal value has been entered...
		if(!minimumDealValueString.isEmpty())
		{
			try
			{
				minimumDealValue = Integer.parseInt(minimumDealValueString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum deal value was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(minimumDealValue))
			{
				JOptionPane.showMessageDialog(mainGUI, "Minimum deal value was not an acceptable value.");
				return false;
			}
		}
		
		//If a maximum deal value has been entered...
		if(!maximumDealValueString.isEmpty())
		{
			try
			{
				maximumDealValue = Integer.parseInt(maximumDealValueString);
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum deal value was not an integer.");
				return false;
			}
			if(!Validator.isAcceptableDealValue(maximumDealValue))
			{
				JOptionPane.showMessageDialog(mainGUI, "Maximum deal value was not an acceptable value.");
				return false;
			}
		}
		
		if(minimumDealValue > maximumDealValue)
		{
			JOptionPane.showMessageDialog(mainGUI, "Deal value minimum is greater than maximum.");
			return false;
		}
		
		//If an earliest deal year has been entered...
		if(!dealEarliestYearString.equals("-"))
		{
			try
			{
				dealEarliestYear = Integer.parseInt(dealEarliestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal month has been entered...
		if(!dealEarliestMonthString.equals("-"))
		{
			//If no earliest deal year has been entered...
			if(dealEarliestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestMonth = Integer.parseInt(dealEarliestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If an earliest deal day has been entered...
		if(!dealEarliestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealEarliestYearString.equals("-") || dealEarliestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealEarliestDay = Integer.parseInt(dealEarliestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealEarliestDay, dealEarliestMonth, dealEarliestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal month has been entered...
		if(!dealLatestMonthString.equals("-"))
		{
			//If no latest deal year has been entered...
			if(dealLatestYearString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestMonth = Integer.parseInt(dealLatestMonthString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		//If a latest deal day has been entered...
		if(!dealLatestDayString.equals("-"))
		{
			//If no earliest deal year or deal month has been entered...
			if(dealLatestYearString.equals("-") || dealLatestMonthString.equals("-"))
			{
				JOptionPane.showMessageDialog(mainGUI, "Dates must be in the format DD/MM/YYYY or MM/YYYY or YYYY.");
				return false;
			}
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			try
			{
				dealLatestDay = Integer.parseInt(dealLatestDayString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If the date is not valid
			if(Validator.historicalDateNotValid(dealLatestDay, dealLatestMonth, dealLatestYear))
			{
				JOptionPane.showMessageDialog(mainGUI, "Invalid date.");
				return false;
			}
		}
		
		//If a latest deal year has been entered...
		if(!dealLatestYearString.equals("-"))
		{
			try
			{
				dealLatestYear = Integer.parseInt(dealLatestYearString);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
				return false;
			}
			//If an earliest deal year has been entered...
			if(!dealEarliestYearString.equals("-"))
			{
				if(dealEarliestYear > dealLatestYear)
				{
					JOptionPane.showMessageDialog(mainGUI, "Earliest deal year is later than latest.");
					return false;
				}
				if(dealEarliestYear == dealLatestYear)
				{
					//If a latest and earliest deal month have both been entered...
					if(!dealLatestMonthString.equals("-") && !dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth > dealLatestMonth)
						{
							JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
							return false;
						}
						if(dealEarliestMonth == dealLatestMonth)
						{
							//If a latest and earliest deal day have both been entered...
							if(!dealLatestDayString.equals("-") && !dealEarliestDayString.equals("-"))
							{
								if(dealEarliestDay > dealLatestDay)
								{
									JOptionPane.showMessageDialog(mainGUI, "Earliest deal date is later than latest.");
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		//Start the search
		memory.setFilteredHistoricalLeads(new ArrayList<HistoricalLead>());
		//For each current lead...
		for(HistoricalLead lead : memory.getHistoricalLeads())
		{
			//Reject unsuccessful leads if only accepting successful ones
			if(leadSucceeded)
			{
				if(!lead.wasSuccessful())
					continue;
			}
			//Reject successful leads if only accepting unsuccessful ones
			else if(leadFailed)
			{
				if(lead.wasSuccessful())
					continue;
			}
			
			//If a client has been chosen...
			if(clientIndex >= 0)
			{
				//If the client matches the selected one...
				if(lead.getClientID() == clientIndex)
				{
					//If matches are being rejected...
					if(notThisClient)
						//Go to the next lead
						continue;
				}
				//If the client does not match the selected one, and only matches are being accepted...
				else if(!notThisClient)
					//Go to the next lead
					continue;
			}
			
			//If a latest deal year has been entered...
			if(!dealLatestYearString.equals("-"))
			{
				//If the lead's date is outside the input range, go to the next lead
				if(Validator.firstDateAfterSecond(dealEarliestDay, dealEarliestMonth, dealEarliestYear, Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())) || Validator.firstDateAfterSecond(Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear()), dealLatestDay, dealLatestMonth, dealLatestYear))
					continue;
			}
			//If a latest deal year has not been entered...
			else
			{	
				//If an earliest deal year has been entered... 
				if(!dealEarliestYearString.equals("-"))
				{
					if(dealEarliestYear != Integer.parseInt(lead.getDealYear()))
						continue;
					//If an earliest deal month has been entered...
					if(!dealEarliestMonthString.equals("-"))
					{
						if(dealEarliestMonth != Integer.parseInt(lead.getDealMonth()))
							continue;
						//If an earliest deal day has been entered but doesn't match that of the lead...
						if(!dealEarliestDayString.equals("-") && dealEarliestDay != Integer.parseInt(lead.getDealDay()))
							continue;
					}
				}
			}
			
			//If the input deal value is outside the range...
			if(maximumDealValue < lead.getValue() || minimumDealValue > lead.getValue())
				continue;
			//If a maximum deal value has not been entered, but a minimum value has, and the lead's value does not match this...
			if(maximumDealValueString.isEmpty() && !minimumDealValueString.isEmpty() && minimumDealValue != lead.getValue())
				continue;
				
			//If a stage of negotiation has been chosen...
			if(stageOfNegotiationsIndex >= 0)
			{
				//If the stage of negotiation matches that selected...
				if(lead.getStageOfNegotiationsID() == stageOfNegotiationsIndex)
				{
					//If rejecting matches...
					if(notThisStageOfNegotiations)
						//Go to the next lead
						continue;
				}
				//If the stage of negotiation does not match that selected, and if only accepting matches...
				else if(!notThisStageOfNegotiations)
					//Go to the next lead
					continue;
			}
			
			memory.getFilteredHistoricalLeads().add(lead);
		}
		
		notifyBackendListeners();
		return true;
	}
	
	public void saveSettings(boolean lockHistoricalData, boolean automaticallyDeleteOldActionReminders, boolean automaticallyDeleteOldLeads, boolean sendToHistorical, boolean automaticallyDeleteHistoricalLeads, boolean deleteAllButNewestN, String numberOfLeadsString, String numberOfDaysString, String currencySymbol)
	{
		int numberOfLeads = 1000;
		int numberOfDays = 3653;
		
		try
		{
			numberOfLeads = Integer.parseInt(numberOfLeadsString);
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(mainGUI, "Number of leads was not an integer.");
			return;
		}
		if(!Validator.isAcceptableNumberOfDays(numberOfLeads))
		{
			JOptionPane.showMessageDialog(mainGUI, "Number of leads was not an acceptable value.");
			return;
		}
		
		try
		{
			numberOfDays = Integer.parseInt(numberOfDaysString);
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(mainGUI, "Number of days was not an integer.");
			return;
		}
		if(!Validator.isAcceptableNumberOfLeads(numberOfDays))
		{
			JOptionPane.showMessageDialog(mainGUI, "Number of days was not an acceptable value.");
			return;
		}
		
		Settings settings = memory.getSettings();
		
		settings.setLockHistoricalData(lockHistoricalData);
		settings.setAutomaticallyDeleteActionReminders(automaticallyDeleteOldActionReminders);
		settings.setAutomaticallyDeleteLeads(automaticallyDeleteOldLeads);
		settings.setSendLeadsToHistorical(sendToHistorical);
		settings.setAutomaticallyDeleteHistoricalLeads(automaticallyDeleteHistoricalLeads);
		settings.setDeleteAllButNewestNHistoricalLeads(deleteAllButNewestN);
		settings.setNumberOfNewestHistoricalLeadsToKeep(numberOfLeads);
		settings.setMaximumAgeOfHistoricalLeadsInDays(numberOfDays);
		settings.setCurrencySymbol(currencySymbol);
		
		JOptionPane.showMessageDialog(mainGUI, "Settings saved.");
	}
	
	public void createAddLeadFrame()
	{
		addLeadFrame = new AddLeadFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfFutureYears(), getNamesOfStagesOfNegotiation(false), getClientNames(false), getDefaultProbabilities());
	}
	
	public void createAddActionReminderFrame(int leadIndexAmongFiltered)
	{
		CurrentLead lead = memory.getFilteredCurrentLeads().get(leadIndexAmongFiltered);
		
		addActionReminderFrame = new AddActionReminderFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfFutureYears(), memory.getClients().get(lead.getClientID()).getName() /*client name*/, lead.getDealDate(), memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage() /*stage of negotiation*/, lead.getProbabilityString(), lead.getValueString(), lead.getID());
	}
	
	public void createAddClientFrame()
	{
		new AddClientFrame(this);
	}
	
	public void createAddHistoricalLeadFrame()
	{
		new AddHistoricalLeadFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfYears(), getNamesOfStagesOfNegotiation(true), getClientNames(true));
	}
	
	public void createAddStageFrame()
	{
		new AddStageFrame(this);
	}
	
	public void createRestoreToCurrentFrame(int leadIndexAmongFiltered)
	{
		HistoricalLead lead = memory.getFilteredHistoricalLeads().get(leadIndexAmongFiltered);
		int probability;
		if(lead.wasSuccessful())
			probability = 100;
		else if(lead.getStageOfNegotiationsID() >= 0)
			probability = memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getDefaultProbability();
		else
			probability = 0;
		
		new RestoreToCurrentLeadFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfFutureYears(), getNamesOfStagesOfNegotiation(false), getClientNames(false), (lead.getClientID() >= 0 ? lead.getClientID() : 0), lead.getDealDay(), lead.getDealMonth(), lead.getDealYear(), (lead.getStageOfNegotiationsID() >= 0 ? lead.getStageOfNegotiationsID() : 0), probability, lead.getValue(), lead.getID(), getDefaultProbabilities());
	}
	
	public void createEditLeadFrame(int leadIndexAmongFiltered)
	{
		CurrentLead lead = memory.getFilteredCurrentLeads().get(leadIndexAmongFiltered);
		
		editLeadFrame = new EditLeadFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfFutureYears(), getNamesOfStagesOfNegotiation(false), getClientNames(false), lead.getClientID(), lead.getDealDay(), lead.getDealMonth(), lead.getDealYear(), lead.getStageOfNegotiationsID(), lead.getProbability(), lead.getValue(), lead.getID(), getDefaultProbabilities());
	}
	
	public void createEditActionReminderFrame(int reminderIndexAmongFiltered)
	{
		ActionReminder reminder = memory.getFilteredActionReminders().get(reminderIndexAmongFiltered);
		CurrentLead lead = memory.getCurrentLeads().get(reminder.getCurrentLeadID());
		
		editActionReminderFrame = new EditActionReminderFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfFutureYears(), reminder.getActionDay(), reminder.getActionMonth(), reminder.getActionYear(), memory.getClients().get(lead.getClientID()).getName() /*client name*/, lead.getDealDate(), memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage() /*stage of negotiation*/, lead.getProbabilityString(), lead.getValueString(), reminder.getMessage(), reminder.getID());
	}
	
	public void createEditClientFrame(int clientIndexAmongFiltered)
	{
		Client client = memory.getFilteredClients().get(clientIndexAmongFiltered);
		
		new EditClientFrame(this, client.getName(), client.getEmailAddress(), client.getPhoneNumber(), client.getID());
	}
	
	public void createEditHistoricalLeadFrame(int leadIndexAmongFiltered)
	{
		HistoricalLead lead = memory.getFilteredHistoricalLeads().get(leadIndexAmongFiltered);
		
		new EditHistoricalLeadFrame(this, memory.getListOfDays(), memory.getListOfMonths(), memory.getListOfYears(), getNamesOfStagesOfNegotiation(true), getClientNames(true), lead.getClientID(), lead.getDealDay(), lead.getDealMonth(), lead.getDealYear(), lead.getStageOfNegotiationsID(), lead.getValue(), lead.getID(), lead.wasSuccessful());
	}
	
	public void createEditStageFrame(int stageIndex)
	{
		StageOfNegotiation stage = memory.getStagesOfNegotiation().get(stageIndex);
		new EditStageFrame(this, stage.getDefaultProbability(), stage.getNameOfStage(), stage.leadShouldBeMoved(), stage.assumingSuccessfulIfMoved(), stageIndex);
	}
	
	public void createAdvancedSearchLeadsFrame()
	{
		//Create the Advanced Search Leads frame
		advancedSearchLeadsFrame = new AdvancedSearchLeadsFrame(this, getDaysWithDash(), getMonthsWithDash(), getYearsWithDash(), getNamesOfStagesOfNegotiation(true), getClientNames(true), getProbabilitiesWithDash());
	}
	
	public void createAdvancedSearchRemindersFrame()
	{
		advancedSearchRemindersFrame = new AdvancedSearchRemindersFrame(this, getDaysWithDash(), getMonthsWithDash(), getYearsWithDash(), getClientNames(true), getProbabilitiesWithDash());
	}
	
	public void createAdvancedSearchClientsFrame()
	{
		new AdvancedSearchClientsFrame(this, getDaysWithDash(), getMonthsWithDash(), getYearsWithDash());
	}
	
	public void createAdvancedSearchHistoricalLeadsFrame()
	{
		String[] listOfYears = new String[memory.getListOfYears().length + 1];
		listOfYears[0] = "-";
		for(int i = 1; i < memory.getListOfYears().length + 1; i++)
			listOfYears[i] = memory.getListOfYears()[i-1];
		
		new AdvancedSearchHistoricalLeadsFrame(this, getDaysWithDash(), getMonthsWithDash(), listOfYears, getNamesOfStagesOfNegotiation(true), getClientNames(true));
	}
	
	public void createViewActionReminderFrame(int reminderIndexAmongFiltered)
	{
		ActionReminder reminder = memory.getFilteredActionReminders().get(reminderIndexAmongFiltered);
		CurrentLead lead = memory.getCurrentLeads().get(reminder.getCurrentLeadID());
		
		viewActionReminderFrame = new ViewActionReminderFrame(this, reminder.getActionDate() , memory.getClients().get(lead.getClientID()).getName() /*client name*/, lead.getDealDate(), memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage() /*stage of negotiation*/, lead.getProbabilityString(), lead.getValueString(), reminder.getMessage());
	}
	
	public void createDeleteCurrentLeadFrame(int leadIndexAmongFiltered, boolean sendToHistorical)
	{
		CurrentLead lead = memory.getFilteredCurrentLeads().get(leadIndexAmongFiltered);
		
		deleteCurrentLeadFrame = new DeleteCurrentLeadFrame(this, memory.getClients().get(lead.getClientID()).getName() /*client name*/, lead.getDealDate(), memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage() /*stage of negotiation*/, lead.getProbabilityString(), lead.getValueString(), lead.getID(), (lead.getProbability() == 100 ? true : memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).assumingSuccessfulIfMoved()) /*whether to assume the lead was successful*/, sendToHistorical);
	}
	
	public void createDeleteActionReminderFrame(int reminderIndexAmongFiltered)
	{
		ActionReminder reminder = memory.getFilteredActionReminders().get(reminderIndexAmongFiltered);
		CurrentLead lead = memory.getCurrentLeads().get(reminder.getCurrentLeadID());
		
		deleteActionReminderFrame = new DeleteActionReminderFrame(this, reminder.getActionDate() , memory.getClients().get(lead.getClientID()).getName() /*client name*/, lead.getDealDate(), memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage() /*stage of negotiation*/, lead.getProbabilityString(), lead.getValueString(), reminder.getMessage(), reminder.getID());
	}
	
	public void createDeleteClientFrame(int clientIDOrIndexAmongFiltered, boolean usingClientIDNotIndex)
	{
		Client client;
		if(usingClientIDNotIndex)
			client = memory.getClients().get(clientIDOrIndexAmongFiltered);
		else
			client = memory.getFilteredClients().get(clientIDOrIndexAmongFiltered);
		
		int expectedIncome = 0;
		for(int currentLeadID : client.getCurrentLeadIDs())
		{
			expectedIncome += (int) (memory.getCurrentLeads().get(currentLeadID).getValue() * (double) memory.getCurrentLeads().get(currentLeadID).getProbability() / 100 + 0.5);//Rounds for each lead.
		}
		
		String expectedIncomeString = memory.getSettings().getCurrencySymbol() + expectedIncome;
		
		new DeleteClientFrame(this, client.getName(), client.getPhoneNumber(), client.getEmailAddress(), expectedIncomeString, client.getCurrentLeadIDs().length, client.getHistoricalLeadIDs().length, client.getID());
	}
	
	public void createDeleteHistoricalLeadFrame(int leadIndexAmongFiltered)
	{
		HistoricalLead lead = memory.getFilteredHistoricalLeads().get(leadIndexAmongFiltered);
		
		boolean allowDeletingClient = false;
		String clientName;
		String stageOfNegotiations;
		
		if(lead.getClientID() >= 0)
		{
			clientName = memory.getClients().get(lead.getClientID()).getName();
			if(memory.getClients().get(lead.getClientID()).getCurrentLeadIDs().length == 0 && memory.getClients().get(lead.getClientID()).getHistoricalLeadIDs().length == 1)
				allowDeletingClient = true;
		}
		else
			clientName = "-";
		
		if(lead.getStageOfNegotiationsID() >= 0)
			stageOfNegotiations = memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage();
		else
			stageOfNegotiations = "-";
		
		new DeleteHistoricalLeadFrame(this, clientName, lead.getDealDate(), lead.getValueString(), stageOfNegotiations, lead.getID(), lead.wasSuccessful(), allowDeletingClient);
	}
	
	public void createDeleteStageFrame(int stageIndex)
	{
		StageOfNegotiation stage = memory.getStagesOfNegotiation().get(stageIndex);
		new DeleteStageFrame(this, stage.getDefaultProbability(), stage.getNameOfStage(), stage.leadShouldBeMoved(), stage.assumingSuccessfulIfMoved(), stageIndex);
	}
	
	public void viewLead(int reminderIndexAmongFiltered)
	{
		ActionReminder reminder = memory.getFilteredActionReminders().get(reminderIndexAmongFiltered);
		//Show the Current leads tab
		mainGUI.getTabbedPane().setSelectedIndex(0);
		//Show all current leads, unsorted
		quickSearchCurrentLeads("");
		//Select the matching lead
		if(CurrentLeadsTab.class.isInstance(mainGUI.getTabbedPane().getSelectedComponent()))
		{
			CurrentLeadsTab currentLeadsTab = (CurrentLeadsTab) mainGUI.getTabbedPane().getSelectedComponent();
			currentLeadsTab.getTable().setRowSelectionInterval(reminder.getCurrentLeadID(), reminder.getCurrentLeadID());
		}
		else
			System.err.println("An instance of CurrentLeadsTab is not selected.");
	}
	
	public void viewClientOfCurrentLead(int leadIndexAmongFiltered)
	{
		CurrentLead lead = memory.getFilteredCurrentLeads().get(leadIndexAmongFiltered);
		//Show the Clients tab
		mainGUI.getTabbedPane().setSelectedIndex(3);
		//Show all clients, unsorted
		quickSearchClients("");
		//Select the matching lead
		if(ClientsTab.class.isInstance(mainGUI.getTabbedPane().getSelectedComponent()))
		{
			ClientsTab clientsTab = (ClientsTab) mainGUI.getTabbedPane().getSelectedComponent();
			clientsTab.getTable().setRowSelectionInterval(lead.getClientID(), lead.getClientID());
		}
		else
			System.err.println("An instance of ClientsTab is not selected.");
	}
	
	public void viewClientOfHistoricalLead(int leadIndexAmongFiltered)
	{
		HistoricalLead lead = memory.getFilteredHistoricalLeads().get(leadIndexAmongFiltered);
		//Show the Clients tab
		mainGUI.getTabbedPane().setSelectedIndex(3);
		//Show all clients, unsorted
		quickSearchClients("");
		//Select the matching lead
		if(ClientsTab.class.isInstance(mainGUI.getTabbedPane().getSelectedComponent()))
		{
			ClientsTab clientsTab = (ClientsTab) mainGUI.getTabbedPane().getSelectedComponent();
			clientsTab.getTable().setRowSelectionInterval(lead.getClientID(), lead.getClientID());
		}
		else
			System.err.println("An instance of ClientsTab is not selected.");
	}
	
	public void viewActionReminders(int leadIndexAmongFiltered)
	{
		CurrentLead lead = memory.getFilteredCurrentLeads().get(leadIndexAmongFiltered);
		memory.setFilteredActionReminders(new ArrayList<ActionReminder>());
		
		for(int key : lead.getActionReminderIDs())
			memory.getFilteredActionReminders().add(memory.getActionReminders().get(key));
		
		//Show the Action reminders tab
		mainGUI.getTabbedPane().setSelectedIndex(2);
		
		notifyBackendListeners();
	}
	
	public void viewCurrentLeads(int clientIndexAmongFiltered)
	{
		Client client = memory.getFilteredClients().get(clientIndexAmongFiltered);
		memory.setFilteredCurrentLeads(new ArrayList<CurrentLead>());
		
		for(int key : client.getCurrentLeadIDs())
			memory.getFilteredCurrentLeads().add(memory.getCurrentLeads().get(key));
		
		//Show the Current leads tab
		mainGUI.getTabbedPane().setSelectedIndex(0);
		
		notifyBackendListeners();
	}
	
	public void viewHistoricalLeads(int clientIndexAmongFiltered)
	{
		Client client = memory.getFilteredClients().get(clientIndexAmongFiltered);
		memory.setFilteredHistoricalLeads(new ArrayList<HistoricalLead>());
		
		for(int key : client.getHistoricalLeadIDs())
			memory.getFilteredHistoricalLeads().add(memory.getHistoricalLeads().get(key));
		
		//Show the Historical data tab
		mainGUI.getTabbedPane().setSelectedIndex(4);
		
		notifyBackendListeners();
	}
	
	public void generateNewGraph(int clientIndex, boolean notThisClient, String earliestDealMonthString, String earliestDealYearString, String latestDealMonthString, String latestDealYearString, boolean showingGuaranteedIncome, boolean showingOverallIncome, boolean showingMaximumIncome, String probabilityString)
	{
		//Unsort the current and historical leads to make the tables' lead orders agree with those of the unfiltered lists
		((CurrentLeadsTab) mainGUI.getTabbedPane().getComponentAt(0)).getTable().getRowSorter().setSortKeys(null);
		((HistoricalDataTab)mainGUI.getTabbedPane().getComponentAt(4)).getTable().getRowSorter().setSortKeys(null);
		
		if(showingGuaranteedIncome)
		{
			//If an advanced search has been successful...
			if(advancedSearchCurrentLeads(clientIndex, notThisClient, "", "", "100", "100", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, "-", "-", "-", "-", "-", "-") && advancedSearchHistoricalLeads(clientIndex, notThisClient, "", "", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, true, false))
			{
				//currentQuarter counts from zero.
				int currentQuarter = Integer.parseInt(earliestDealMonthString)/4;
				int currentYear = Integer.parseInt(earliestDealYearString);
				int expectedRevenue = 0;
				int pastRevenue = 0;
				ArrayList<Double> graphPoints = new ArrayList<Double>();
				
				//For each quarter in the range...
				while(currentYear < Integer.parseInt(latestDealYearString) || currentYear == Integer.parseInt(latestDealYearString) && currentQuarter <= Integer.parseInt(latestDealMonthString)/4)
				{
					int incomeThisQuarter = 0;
					//For each CurrentLead...
					for(CurrentLead lead : memory.getFilteredCurrentLeads())
					{
						//If the lead ends during this quarter, add its income
						if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
						{
							incomeThisQuarter += lead.getValue();
							expectedRevenue += lead.getValue();
						}
					}
					//For each HistoricalLead...
					for(HistoricalLead lead : memory.getFilteredHistoricalLeads())
					{
						//If the lead ends during this quarter, add its income
						if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
						{
							incomeThisQuarter += lead.getValue();
							pastRevenue += lead.getValue();
						}
					}
					
					graphPoints.add(new Double((double) incomeThisQuarter));
					currentQuarter ++;
					if(currentQuarter > 3)
					{
						currentYear++;
						currentQuarter = 0;
					}
				}
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).getGraphPanel().redraw(graphPoints, Integer.parseInt(earliestDealMonthString)/4, Integer.parseInt(earliestDealYearString));
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setPastRevenueThisPeriod(pastRevenue);
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setExpectedRevenueThisPeriod(expectedRevenue);
			}
		}
		else if(showingOverallIncome)
		{
			if(advancedSearchCurrentLeads(clientIndex, notThisClient, "", "", "-", "-", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, "-", "-", "-", "-", "-", "-") && advancedSearchHistoricalLeads(clientIndex, notThisClient, "", "", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, true, false))
			{
				//currentQuarter counts from zero.
				int currentQuarter = Integer.parseInt(earliestDealMonthString)/4;
				int currentYear = Integer.parseInt(earliestDealYearString);
				double expectedRevenue = 0;
				double pastRevenue = 0;
				ArrayList<Double> graphPoints = new ArrayList<Double>();
				
				//For each quarter in the range...
				while(currentYear < Integer.parseInt(latestDealYearString) || currentYear == Integer.parseInt(latestDealYearString) && currentQuarter <= Integer.parseInt(latestDealMonthString)/4)
				{
					double incomeThisQuarter = 0;
					//For each CurrentLead...
					for(CurrentLead lead : memory.getFilteredCurrentLeads())
					{
						//If the lead ends during this quarter...
						if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
						{
							incomeThisQuarter += lead.getValue()*lead.getProbability()/100.0;
							expectedRevenue += lead.getValue()*lead.getProbability()/100.0;
						}
					}
					//For each HistoricalLead...
					for(HistoricalLead lead : memory.getFilteredHistoricalLeads())
					{
						//If the lead ends during this quarter, add its income
						if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
						{
							incomeThisQuarter += lead.getValue();
							pastRevenue += lead.getValue();
						}
					}
					
					graphPoints.add(new Double(incomeThisQuarter));
					currentQuarter ++;
					if(currentQuarter > 3)
					{
						currentYear++;
						currentQuarter = 0;
					}
				}
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).getGraphPanel().redraw(graphPoints, Integer.parseInt(earliestDealMonthString)/4, Integer.parseInt(earliestDealYearString));
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setPastRevenueThisPeriod((int)Math.round(pastRevenue));
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setExpectedRevenueThisPeriod((int)Math.round(expectedRevenue));
			}
		}
		else if(showingMaximumIncome)
		{
			if(advancedSearchCurrentLeads(clientIndex, notThisClient, "", "", "-", "-", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, "-", "-", "-", "-", "-", "-") && advancedSearchHistoricalLeads(clientIndex, notThisClient, "", "", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, false, false))
			{
				//currentQuarter counts from zero.
				int currentQuarter = Integer.parseInt(earliestDealMonthString)/4;
				int currentYear = Integer.parseInt(earliestDealYearString);
				int expectedRevenue = 0;
				int pastRevenue = 0;
				ArrayList<Double> graphPoints = new ArrayList<Double>();
				
				//For each quarter in the range...
				while(currentYear < Integer.parseInt(latestDealYearString) || currentYear == Integer.parseInt(latestDealYearString) && currentQuarter <= Integer.parseInt(latestDealMonthString)/4)
				{
					int incomeThisQuarter = 0;
					//For each CurrentLead...
					for(CurrentLead lead : memory.getFilteredCurrentLeads())
					{
						//If the lead ends during this quarter...
						if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
						{
							incomeThisQuarter += lead.getValue();
							expectedRevenue += lead.getValue();
						}
					}
					//For each HistoricalLead...
					for(HistoricalLead lead : memory.getFilteredHistoricalLeads())
					{
						//If the lead ends during this quarter, add its income
						if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
						{
							incomeThisQuarter += lead.getValue();
							pastRevenue += lead.getValue();
						}
					}
					
					graphPoints.add(new Double((double) incomeThisQuarter));
					currentQuarter ++;
					if(currentQuarter > 3)
					{
						currentYear++;
						currentQuarter = 0;
					}
				}
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).getGraphPanel().redraw(graphPoints, Integer.parseInt(earliestDealMonthString)/4, Integer.parseInt(earliestDealYearString));
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setPastRevenueThisPeriod(pastRevenue);
				((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setExpectedRevenueThisPeriod(expectedRevenue);
			}
		}
		//If showing income with probability above a given percentage...
		else if(advancedSearchCurrentLeads(clientIndex, notThisClient, "", "", probabilityString, "100", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, "-", "-", "-", "-", "-", "-") && advancedSearchHistoricalLeads(clientIndex, notThisClient, "", "", -1, false, "-", earliestDealMonthString, earliestDealYearString, "-", latestDealMonthString, latestDealYearString, true, false))
		{
			//currentQuarter counts from zero.
			int currentQuarter = Integer.parseInt(earliestDealMonthString)/4;
			int currentYear = Integer.parseInt(earliestDealYearString);
			int expectedRevenue = 0;
			int pastRevenue = 0;
			ArrayList<Double> graphPoints = new ArrayList<Double>();
			
			//For each quarter in the range...
			while(currentYear < Integer.parseInt(latestDealYearString) || currentYear == Integer.parseInt(latestDealYearString) && currentQuarter <= Integer.parseInt(latestDealMonthString)/4)
			{
				int incomeThisQuarter = 0;
				//For each CurrentLead...
				for(CurrentLead lead : memory.getFilteredCurrentLeads())
				{
					//If the lead ends during this quarter...
					if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
					{
						incomeThisQuarter += lead.getValue();
						expectedRevenue += lead.getValue();
					}
				}
				//For each HistoricalLead...
				for(HistoricalLead lead : memory.getFilteredHistoricalLeads())
				{
					//If the lead ends during this quarter, add its income
					if(Integer.parseInt(lead.getDealYear()) == currentYear && Integer.parseInt(lead.getDealMonth())/4 == currentQuarter)
					{
						incomeThisQuarter += lead.getValue();
						pastRevenue += lead.getValue();
					}
				}
				
				graphPoints.add(new Double((double) incomeThisQuarter));
				currentQuarter ++;
				if(currentQuarter > 3)
				{
					currentYear++;
					currentQuarter = 0;
				}
			}
			((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).getGraphPanel().redraw(graphPoints, Integer.parseInt(earliestDealMonthString)/4, Integer.parseInt(earliestDealYearString));
			((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setPastRevenueThisPeriod(pastRevenue);
			((GraphsTab) mainGUI.getTabbedPane().getComponentAt(1)).setExpectedRevenueThisPeriod(expectedRevenue);
		}
	}
	
	//Write memory's contents to file
	public void saveAllToFile()
	{
		//Write current leads to file
		try
		{
			//Set up a BufferedWriter which will append new leads to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Data/CurrentLeads.csv"));//Does not append
			//For each current lead...
			for(CurrentLead lead : memory.getCurrentLeads())
			{
				//Write the data, separated with commas, onto the next line in the buffer
				bufferedWriter.write(lead.getID() + "," + lead.getClientID() + "," + lead.getDealDate() + "," + lead.getValue() + "," + lead.getStageOfNegotiationsID() + "," + lead.getProbability());
				//Start an empty next line in the buffer
				bufferedWriter.newLine();
			}

			//Save the buffer's contnents to file
			bufferedWriter.close();
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Print information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to Current Leads file");
			e.printStackTrace();
		}
		
		//Write historical leads to file
		try
		{
			//Set up a BufferedWriter which will append new leads to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Data/HistoricalLeads.csv"));//Does not append
			//For each historical lead...
			for(HistoricalLead lead : memory.getHistoricalLeads())
			{
				//Write the data, separated with commas, onto the next line in the buffer
				bufferedWriter.write(lead.getID() + "," + lead.getClientID() + "," + lead.getDealDate() + "," + lead.getValue() + "," + lead.getStageOfNegotiationsID() + "," + lead.wasSuccessful());
				//Start an empty next line in the buffer
				bufferedWriter.newLine();
			}

			//Save the buffer's contnents to file
			bufferedWriter.close();
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Print information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to Historical Leads file");
			e.printStackTrace();
		}
		
		//Write clients to file
		try
		{
			//Set up a BufferedWriter which will append new leads to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Data/Clients.csv"));//Does not append
			//For each current lead...
			for(Client client : memory.getClients())
			{
				//Write the data, separated with commas, onto the next line in the buffer
				bufferedWriter.write(client.getID() + "," + convertToCSVFormat(client.getName()) + "," + convertToCSVFormat(client.getPhoneNumber()) + "," + convertToCSVFormat(client.getEmailAddress()));
				//Start an empty next line in the buffer
				bufferedWriter.newLine();
			}

			//Save the buffer's contnents to file
			bufferedWriter.close();
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Print information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to Clients file");
			e.printStackTrace();
		}
		
		//Write action reminders to file
		try
		{
			//Set up a BufferedWriter which will append new leads to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Data/Actions.csv"));//Does not append
			//For each current lead...
			for(ActionReminder action : memory.getActionReminders())
			{
				//Write the data, separated with commas, onto the next line in the buffer
				bufferedWriter.write(action.getID() + "," + action.getCurrentLeadID() + "," + action.getActionDate() + "," + convertToCSVFormat(action.getMessage()));
				//Start an empty next line in the buffer
				bufferedWriter.newLine();
			}

			//Save the buffer's contnents to file
			bufferedWriter.close();
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Print information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to Actions file");
			e.printStackTrace();
		}
		
		//Write stages of negotiation to file
		try
		{
			//Set up a BufferedWriter which will append new leads to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Data/StagesOfNegotiation.csv"));//Does not append
			//For each current lead...
			for(StageOfNegotiation stage : memory.getStagesOfNegotiation())
			{
				//Write the data, separated with commas, onto the next line in the buffer
				bufferedWriter.write(stage.getID() + "," + convertToCSVFormat(stage.getNameOfStage()) + "," + stage.getDefaultProbability() + "," + stage.leadShouldBeMoved() + "," + stage.assumingSuccessfulIfMoved());
				//Start an empty next line in the buffer
				bufferedWriter.newLine();
			}

			//Save the buffer's contnents to file
			bufferedWriter.close();
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Print information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to Stages of negotiation file");
			e.printStackTrace();
		}
		
		//Save settings
		try
		{
			memory.getProperties().store(new BufferedWriter(new FileWriter("Data/settings.properties")), null);
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Print information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to settings.properties file");
			e.printStackTrace();
		}
	}
	
	public void quickSearchCurrentLeads(String searchValue)
	{
		memory.setFilteredCurrentLeads(new ArrayList<CurrentLead>());
		//For each current lead...
		for(CurrentLead lead : memory.getCurrentLeads())
		{
			//If any visible field contains the search value...
			if(memory.getClients().get(lead.getClientID()).getName().contains(searchValue) /*client name*/
					|| lead.getDealDate().contains(searchValue)
					|| lead.getValueString().contains(searchValue)
					|| memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage().contains(searchValue) /*Stage of negotiation*/
					|| lead.getProbabilityString().contains(searchValue))
				memory.getFilteredCurrentLeads().add(lead);
			else
			{
				//Find the lead's earliest reminder date
				if(lead.getActionReminderIDs().length == 0)
					continue;
				int lowestID = lead.getActionReminderIDs()[0];
				for(int actionReminderID : lead.getActionReminderIDs())
				{
					if(actionReminderID < lowestID)
						lowestID = actionReminderID;
				}
				if(memory.getActionReminders().get(lowestID).getActionDate().contains(searchValue))
					memory.getFilteredCurrentLeads().add(lead);
			}
		}
		notifyBackendListeners();
	}
	
	public void quickSearchActionReminders(String searchValue)
	{
		memory.setFilteredActionReminders(new ArrayList<ActionReminder>());
		//For each action...
		for(ActionReminder action : memory.getActionReminders())
		{
			CurrentLead lead = memory.getCurrentLeads().get(action.getCurrentLeadID());
			//If any visible field contains the search value...
			if(action.getActionDate().contains(searchValue)
					|| memory.getClients().get(lead.getClientID()).getName().contains(searchValue) /*client name*/
					|| lead.getDealDate().contains(searchValue)
					|| lead.getValueString().contains(searchValue)
					|| lead.getProbabilityString().contains(searchValue)
					|| action.getMessage().contains(searchValue))
				memory.getFilteredActionReminders().add(action);
		}
		notifyBackendListeners();
	}
	
	public void quickSearchClients(String searchValue)
	{
		memory.setFilteredClients(new ArrayList<Client>());
		//For each client...
		for(Client client : memory.getClients())
		{
			//If any visible field contains the search value...
			if(client.getName().contains(searchValue)
					|| client.getPhoneNumber().contains(searchValue)
					|| client.getEmailAddress().contains(searchValue))
				memory.getFilteredClients().add(client);
			else
			{
				//Find the client's earliest deal date
				if(client.getCurrentLeadIDs().length != 0)
				{
					int lowestID = client.getCurrentLeadIDs()[0];
					for(int currentleadID : client.getCurrentLeadIDs())
					{
						if(currentleadID < lowestID)
							lowestID = currentleadID;
					}
					if(memory.getCurrentLeads().get(lowestID).getDealDate().contains(searchValue))
					{
						memory.getFilteredClients().add(client);
						continue;
					}
				}
				int totalExpectedIncome = 0;
				for(int currentLeadID : client.getCurrentLeadIDs())
				{
					totalExpectedIncome += (int) (memory.getCurrentLeads().get(currentLeadID).getValue() * (double) memory.getCurrentLeads().get(currentLeadID).getProbability() / 100 + 0.5);//Rounds for each lead.
				}
				String expectedRevenue = memory.getSettings().getCurrencySymbol() + Integer.toString(totalExpectedIncome);
				if(expectedRevenue.contains(searchValue))
					memory.getFilteredClients().add(client);
			}
		}
		notifyBackendListeners();
	}
	
	public void quickSearchHistoricalLeads(String searchValue)
	{
		memory.setFilteredHistoricalLeads(new ArrayList<HistoricalLead>());
		//For each lead...
		for(HistoricalLead lead : memory.getHistoricalLeads())
		{
			//If any visible field contains the search value...
			if(lead.getDealDate().contains(searchValue)
					|| lead.getValueString().contains(searchValue)
					|| lead.wasSuccessful() && "Succeeded".contains(searchValue)
					|| !lead.wasSuccessful() && "Failed".contains(searchValue))
				memory.getFilteredHistoricalLeads().add(lead);
			else
			{
				if(lead.getClientID() >= 0)
				{
					if(memory.getClients().get(lead.getClientID()).getName().contains(searchValue))
					{
						memory.getFilteredHistoricalLeads().add(lead);
						continue;
					}
				}
				if(lead.getStageOfNegotiationsID() >= 0)
				{
					if(memory.getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage().contains(searchValue))
						memory.getFilteredHistoricalLeads().add(lead);
				}
			}
		}
		notifyBackendListeners();
	}
	
	public boolean clientHasNoCurrentLeads(int clientIndexAmongFiltered)
	{
		return(memory.getFilteredClients().get(clientIndexAmongFiltered).getCurrentLeadIDs().length == 0);
	}
	
	public boolean clientHasNoHistoricalLeads(int clientIndexAmongFiltered)
	{
		return(memory.getFilteredClients().get(clientIndexAmongFiltered).getHistoricalLeadIDs().length == 0);
	}
	
	public boolean stageHasLeads(int stageIndex)
	{
		for(CurrentLead lead : memory.getCurrentLeads())
		{
			if(lead.getStageOfNegotiationsID() == stageIndex)
				return true;
		}
		for(HistoricalLead lead : memory.getHistoricalLeads())
		{
			if(lead.getStageOfNegotiationsID() == stageIndex)
				return true;
		}
		return false;
	}
	
	public void exportGraph(String filenamePrefix, ArrayList<Double> points, int earliestQuarter, int earliestYear)
	{
		if(filenamePrefix.length() == 0)
		{
			JOptionPane.showMessageDialog(mainGUI, "Please enter a filename");
			return;
		}
		//else
		String filename = filenamePrefix;
		if(!filenamePrefix.endsWith(".csv"))
			filename += ".csv";
		
		if(new File("Exported graphs/" + filename).isFile())
		{
			JOptionPane.showMessageDialog(mainGUI, "File already exists!");
			return;
		}
		//If the file doesn't already exist...
		//Write current leads to file
		try
		{
			//Set up a BufferedWriter which will append new leads to the file
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Exported graphs/" + filename));//Does not append
			bufferedWriter.write("Date,Income");
			int currentQuarter = earliestQuarter;
			int currentYear = earliestYear;
			//For each current lead...
			for(Double point : points)
			{
				if(currentQuarter > 3)
				{
					currentYear++;
					currentQuarter = currentQuarter % 4;
				}
				
				//Start an empty next line in the buffer
				bufferedWriter.newLine();
				//Write the date of the end of each quarter, followed by the quarter's income, onto the next line in the buffer
				bufferedWriter.write("30/" + (currentQuarter + 1)*3 + "/" + currentYear + "," + memory.getSettings().getCurrencySymbol() + Math.round(point));
				currentQuarter ++;
			}

			//Save the buffer's contnents to file
			bufferedWriter.close();
			
			JOptionPane.showMessageDialog(mainGUI, "Successfully exported file: " + filename + ".");
		}
		//If writing to file failed...
		catch(Exception e)
		{
			//Output information about the error
			JOptionPane.showMessageDialog(mainGUI, "Failed to write to " + filename + ".");
			e.printStackTrace();
		}
	}
	
	public String convertToCSVFormat(String input)
	{
		//If the input contains a comma or speech mark (which will need escaping)...
		if(input.contains("\"") || input.contains(","))
		{
			//Begin output with a speech mark
			String output = "\"";
			//For each character in the input...
			for(int i = 0; i < input.length(); i++)
			{
				//If the current character is a speech mark...
				if(input.charAt(i) == '"')
					//Add two speech marks to the output
					output += "\"\"";
				else
					output += input.charAt(i);
			}
			//Add a speech mark at the end
			return output + '"';
		}
		else
			return input;
	}
	
	//When a window's close button (x) is pressed...
	public void windowClosing(WindowEvent e)
	{
		if(e.getSource() == mainGUI)
		{
			saveAllToFile();
			System.exit(0);
		}
	}
	
	public void actionReminderRemoved()
	{
		mActionReminderRemoved = true;
	}
	
	public void currentLeadRemoved()
	{
		mCurrentLeadRemoved = true;
	}
	
	public void historicalLeadRemoved()
	{
		mHistoricalLeadRemoved = true;
	}
	
	private void notifyBackendListeners()
	{
		((AbstractTableModel) ((CurrentLeadsTab) mainGUI.getTabbedPane().getComponentAt(0)).getTable().getModel()).fireTableDataChanged();
		((AbstractTableModel) ((ActionRemindersTab) mainGUI.getTabbedPane().getComponentAt(2)).getTable().getModel()).fireTableDataChanged();
		((AbstractTableModel) ((ClientsTab) mainGUI.getTabbedPane().getComponentAt(3)).getTable().getModel()).fireTableDataChanged();
		((AbstractTableModel) ((HistoricalDataTab) mainGUI.getTabbedPane().getComponentAt(4)).getTable().getModel()).fireTableDataChanged();
		((GraphsTab)mainGUI.getTabbedPane().getComponentAt(1)).updateComboBox();
		((AbstractTableModel) ((SettingsTab) mainGUI.getTabbedPane().getComponentAt(5)).getTable().getModel()).fireTableDataChanged();
		
		//Notify everything listening to the BackendManager
		for(BackendListener listener : backendListeners)
			listener.backendUpdated();
	}
	
	public Memory getMemory()
	{
		return memory;
	}
	
	public GUI getGUI()
	{
		return mainGUI;
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}
package backend;

import java.util.Calendar;
import javax.swing.JOptionPane;

public class Validator
{
	private static int maximumDealValue = 20000;
	BackendManager mBackendManager;
	
	public Validator(BackendManager backendManager)
	{
		mBackendManager = backendManager;
	}
	
	//Type check for Strings which should be integers
	public static boolean isAnInteger(String numberString)
	{
		//Try to convert the String to an int
		try
		{
			Integer.parseInt(numberString);
		}
		//If this fails...
		catch(NumberFormatException e)
		{
			return false;
		}
		//If successful...
		return true;
	}
	
	public static boolean isAcceptableDealValue(int number)
	{
		if(0 <= number && number <= maximumDealValue)
			return true;
		return false;
	}
	
	public static boolean isAcceptableProbability(int number)
	{
		if(0 <= number && number <= 100)
			return true;
		return false;
	}
	
	public static boolean isAcceptableNumberOfDays(int number)
	{
		if(0 <= number && number <= 3653)
			return true;
		return false;
	}
	
	public static boolean isAcceptableNumberOfLeads(int number)
	{
		if(0 <= number && number <= 1000)
			return true;
		return false;
	}
	
	//Returns true for non-existent or past dates, e.g. 31/02/YYYY. Will falsely accept 29/02/2100.
	public static boolean dateNotValid(int day, int month, int year)
	{
		//If after the 28th...
		if(day > 28)
		{
			//If February...
			if(month == 2)
			{
				//If the 30th or later..
				if(day != 29)
					return true;
				//If the 29th and not a leap year (Will work until 2100.)...
				else if(year % 4 != 0)
					return true;
			}
			//If the 31st and of an invalid month...
			if(day == 31 && (month == 4 || month == 6 || month == 9 || month == 11))
				return true;
		}
		//If before the current year...
		if(year < Calendar.getInstance().get(Calendar.YEAR))
			return true;
		if(year == Calendar.getInstance().get(Calendar.YEAR))
		{
			//If before the current month...
			if(month < Calendar.getInstance().get(Calendar.MONTH) + 1)
				return true;
			//If before today
			if(month == Calendar.getInstance().get(Calendar.MONTH) + 1 && day < Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
				return true;
		}
		return false;
	}
	
	//Returns true for non-existent dates, e.g. 31/02/YYYY. Will falsely accept 29/02/2100.
	public static boolean historicalDateNotValid(int day, int month, int year)
	{
	//If the 28th...
		if(day > 28)
		{
			//If February...
			if(month == 2)
			{
				//If the 30th or later..
				if(day != 29)
					return true;
				//If the 29th and not a leap year (Will work until 2100.)...
				else if(year % 4 != 0)
					return true;
				//If the 29th and a leap year...
				else
					return false;
			}
			//If the 31st and of an invalid month...
			if(day == 31 && (month == 4 || month == 6 || month == 9 || month == 11))
				return true;
		}
		return false;
	}
	
	public boolean isValid(ActionReminder reminder)
	{
		int actionDay = Integer.parseInt(reminder.getActionDay());
		int actionMonth = Integer.parseInt(reminder.getActionMonth());
		int actionYear = Integer.parseInt(reminder.getActionYear());
		
		if(dateNotValid(actionDay, actionMonth, actionYear))
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Action reminder date is not valid.");
			return false;
		}
		if(reminder.getMessage().length() > 1000)
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Message is too long.");
			return false;
		}
		//If the reminder date is after the deal date...
		if(firstDateAfterSecond(actionDay, actionMonth, actionYear, Integer.parseInt(mBackendManager.getMemory().getCurrentLeads().get(reminder.getCurrentLeadID()).getDealDay()), Integer.parseInt(mBackendManager.getMemory().getCurrentLeads().get(reminder.getCurrentLeadID()).getDealMonth()), Integer.parseInt(mBackendManager.getMemory().getCurrentLeads().get(reminder.getCurrentLeadID()).getDealYear())))
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Action reminder date is after lead close date.");
			return false;
		}
		return true;
	}
	
	public boolean isValid(CurrentLead lead)
	{
		if(dateNotValid(Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())))
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Lead date is not valid.");
			return false;
		}
		//If the lead's deal value isn't acceptable...
		if(!isAcceptableDealValue(lead.getValue()))
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Lead value is not valid.");
			return false;
		}
		//If the lead has action reminders...
		if(lead.getActionReminderIDs().length > 0)
		{
			int lowestID = lead.getActionReminderIDs()[0];
			for(int actionReminderID : lead.getActionReminderIDs())
			{
				if(actionReminderID < lowestID)
					lowestID = actionReminderID;
			}
			//If the lead's deal date is before its latest action date...
			if(firstDateAfterSecond(Integer.parseInt(mBackendManager.getMemory().getActionReminders().get(lowestID).getActionDay()), Integer.parseInt(mBackendManager.getMemory().getActionReminders().get(lowestID).getActionMonth()), Integer.parseInt(mBackendManager.getMemory().getActionReminders().get(lowestID).getActionYear()), Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())))
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This deal date is before one of the lead's action dates.");
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid(Client client, int clientID)
	{
		//Client name length check
		if(client.getName().length() < 2 || client.getName().length() > 64)
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This client name's length is invalid.");
			return false;
		}
		//Client name mustn't start or end with a space
		if(client.getName().charAt(0) == ' ' || client.getName().charAt(client.getName().length() - 1) == ' ')
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This client name is invalid.");
			return false;
		}
		//Lookup check (if a client with this name already exists)
		for(Client existingClient : mBackendManager.getMemory().getClients())
		{
			//If the names match AND the existing client isn't being replaced...
			if(client.getName().equals(existingClient.getName()) && existingClient.getID() != clientID)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "A client with this name already exists.");
				return false;
			}
		}
		
		//If a telephone number has been entered...
		if(client.getPhoneNumber().length() != 0)
		{
			//Length check
			if(client.getPhoneNumber().length() < 10 || client.getPhoneNumber().length() > 16)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This phone number's length is invalid.");
				return false;
			}
			//Format check
			boolean bracketExpected = false;
			if(client.getPhoneNumber().charAt(0) != '(')
			{
				if(client.getPhoneNumber().charAt(0) != '+' && !Character.isDigit(client.getPhoneNumber().charAt(0)))
				{
					JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This phone number is invalid.");
					return false;
				}
			}
			else
				bracketExpected = true;
			
			int numberOfDigits = (Character.isDigit(client.getPhoneNumber().charAt(0)) ? 1 : 0);
			for(int i = 1; i < client.getPhoneNumber().length(); i++)
			{
				if(Character.isDigit(client.getPhoneNumber().charAt(i)))
					numberOfDigits++;
				else if(client.getPhoneNumber().charAt(i) != ' ' && client.getPhoneNumber().charAt(i) != '-')
				{
					if(client.getPhoneNumber().charAt(i) == ')' && bracketExpected)
						bracketExpected = false;
					else
					{
						JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This phone number is invalid.");
						return false;
					}
				}
			}
			if(bracketExpected || numberOfDigits < 8)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This phone number is invalid.");
				return false;
			}
		}
		
		//If an email address has been entered
		if(client.getEmailAddress().length() != 0)
		{
			if(client.getEmailAddress().length() > 64)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This email address length is invalid.");
				return false;
			}
			if(!client.getEmailAddress().contains("@"))
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Email address must contain '@'.");
				return false;
			}
			if(client.getEmailAddress().endsWith("@"))
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Email address domain name is invalid.");
				return false;
			}
			
			String[] emailAddressHalves = client.getEmailAddress().split("@");
			if(emailAddressHalves.length != 2)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Email address contains too many '@'s.");
				return false;
			}
			if(!emailAddressHalves[1].contains("."))
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Email address domain name must contain '.'.");
				return false;
			}
			if(emailAddressHalves[0].length() == 0)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Email address local-part is empty.");
				return false;
			}
			if(emailAddressHalves[1].length() < 3)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Email address domain name is too short.");
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid(HistoricalLead lead)
	{
		if(historicalDateNotValid(Integer.parseInt(lead.getDealDay()), Integer.parseInt(lead.getDealMonth()), Integer.parseInt(lead.getDealYear())))
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Lead date is not valid.");
			return false;
		}
		//If the lead's deal value isn't acceptable...
		if(!isAcceptableDealValue(lead.getValue()))
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "Lead value is not valid.");
			return false;
		}
		return true;
	}
	
	public boolean isValid(StageOfNegotiation stage, int stageID)
	{
		//Stage name length check
		if(stage.getNameOfStage().length() < 2 || stage.getNameOfStage().length() > 64)
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This stage name's length is invalid.");
			return false;
		}
		//Stage name mustn't start or end with a space
		if(stage.getNameOfStage().charAt(0) == ' ' || stage.getNameOfStage().charAt(stage.getNameOfStage().length() - 1) == ' ')
		{
			JOptionPane.showMessageDialog(mBackendManager.getGUI(), "This stage name is invalid.");
			return false;
		}
		//Lookup check (if a stage with this name already exists)
		for(StageOfNegotiation existingStage : mBackendManager.getMemory().getStagesOfNegotiation())
		{
			//If the names match AND the existing stage isn't being replaced...
			if(stage.getNameOfStage().equals(existingStage.getNameOfStage()) && existingStage.getID() != stageID)
			{
				JOptionPane.showMessageDialog(mBackendManager.getGUI(), "A stage with this name already exists.");
				return false;
			}
		}
		return true;
	}
	
	public static boolean firstDateAfterSecond(int firstDay, int firstMonth, int firstYear, int secondDay, int secondMonth, int secondYear)
	{
		if(firstYear > secondYear || firstYear == secondYear && (firstMonth > secondMonth || (firstMonth == secondMonth && firstDay > secondDay)))
			return true;
		//Else return false
		return false;
	}
	
	public static int getMaximumDealValue()
	{
		return maximumDealValue;
	}
}
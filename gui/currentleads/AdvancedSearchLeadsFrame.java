package gui.currentleads;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class AdvancedSearchLeadsFrame extends JDialog implements ActionListener 
{
	JLabel clientNameLabel = new JLabel();
	JComboBox clientNameComboBox;
	JCheckBox notClientNameCheckBox = new JCheckBox();
	JComboBox actionReminderEarliestYearComboBox;
	JComboBox actionReminderLatestYearComboBox;
	JComboBox actionReminderEarliestMonthComboBox;
	JComboBox actionReminderLatestMonthComboBox;
	JComboBox actionReminderEarliestDayComboBox;
	JComboBox actionReminderLatestDayComboBox;
	JLabel actionReminderDateLabel = new JLabel();
	JLabel dealDateLabel = new JLabel();
	JComboBox dealEarliestDayComboBox;
	JComboBox dealLatestDayComboBox;
	JComboBox dealEarliestMonthComboBox;
	JComboBox dealLatestMonthComboBox;
	JComboBox dealEarliestYearComboBox;
	JComboBox dealLatestYearComboBox;
	JLabel dealValueLabel = new JLabel();
	JTextField minimumDealValueTextBox = new JTextField();
	JTextField maximumDealValueTextBox = new JTextField();
	JLabel currencySymbolLabel = new JLabel();
	JLabel toValueLabel = new JLabel();
	JLabel toDealDateLabel = new JLabel();
	JLabel toActionReminderlDateLabel = new JLabel();
	JLabel probabilityLabel = new JLabel();
	JSpinner minimumProbabilitySpinner;
	JSpinner maximumProbabilitySpinner;
	JLabel toProbabilityLabel = new JLabel();
	JLabel percentageLabel0 = new JLabel();
	JLabel percentageLabel1 = new JLabel();
	JLabel stageOfNegotiationsLabel = new JLabel();
	JComboBox stageOfNegotiationsComboBox;
	JCheckBox notStageOfNegotiationsCheckBox = new JCheckBox();
	JButton cancelButton = new JButton();
	JButton searchButton = new JButton();
	
	private final BackendManager backendManager;
	private final String[] listOfDays;
	private final String[] listOfMonths;
	private final String[] listOfYears;
	private final String[] stagesOfNegotiation;
	private final String[] clients;
	private final String[] probabilitiesInPercent;
	
	public AdvancedSearchLeadsFrame(BackendManager backendManager, String[] days, String[] months, String[] years, String[] stagesOfNegotiation, String[] clients, String[] probabilitiesInPercent)
	{
		this.backendManager = backendManager;
		listOfDays = days;
		listOfMonths = months;
		listOfYears = years;
		this.stagesOfNegotiation = stagesOfNegotiation;
		this.clients = clients;
		this.probabilitiesInPercent = probabilitiesInPercent;
		//Set the dialog's contentPane's layout to null, so that components can be placed precisely
		getContentPane().setLayout(null);
		//Disable the main GUI frame
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		addWindowListener(backendManager);
		
		setUpGUI();
	}
	
	public void setUpGUI()
	{
		clientNameLabel.setLocation(15,15);
		clientNameLabel.setSize(70,20);
		clientNameLabel.setText("Client name");
		getContentPane().add(clientNameLabel);
		
		clientNameComboBox = new JComboBox(clients);
		clientNameComboBox.setLocation(15,40);
		clientNameComboBox.setSize(420,30);
		getContentPane().add(clientNameComboBox);

		notClientNameCheckBox.setLocation(393,15);
		notClientNameCheckBox.setSize(50,20);
		notClientNameCheckBox.setText("NOT");
		getContentPane().add(notClientNameCheckBox);
		
		actionReminderEarliestYearComboBox = new JComboBox(listOfYears);
		actionReminderEarliestYearComboBox.setLocation(830,40);
		actionReminderEarliestYearComboBox.setSize(55,30);
		getContentPane().add(actionReminderEarliestYearComboBox);
		
		actionReminderLatestYearComboBox = new JComboBox(listOfYears);
		actionReminderLatestYearComboBox.setLocation(830,80);
		actionReminderLatestYearComboBox.setSize(55,30);
		getContentPane().add(actionReminderLatestYearComboBox);
		
		actionReminderEarliestMonthComboBox = new JComboBox(listOfMonths);
		actionReminderEarliestMonthComboBox.setLocation(775,40);
		actionReminderEarliestMonthComboBox.setSize(45,30);
		getContentPane().add(actionReminderEarliestMonthComboBox);
		
		actionReminderLatestMonthComboBox = new JComboBox(listOfMonths);
		actionReminderLatestMonthComboBox.setLocation(775,80);
		actionReminderLatestMonthComboBox.setSize(45,30);
		getContentPane().add(actionReminderLatestMonthComboBox);
		
		actionReminderEarliestDayComboBox = new JComboBox(listOfDays);
		actionReminderEarliestDayComboBox.setLocation(720,40);
		actionReminderEarliestDayComboBox.setSize(45,30);
		getContentPane().add(actionReminderEarliestDayComboBox);
		
		actionReminderLatestDayComboBox = new JComboBox(listOfDays);
		actionReminderLatestDayComboBox.setLocation(720,80);
		actionReminderLatestDayComboBox.setSize(45,30);
		getContentPane().add(actionReminderLatestDayComboBox);

		actionReminderDateLabel.setLocation(720,15);
		actionReminderDateLabel.setSize(120,20);
		actionReminderDateLabel.setText("Action reminder date");
		getContentPane().add(actionReminderDateLabel);

		dealDateLabel.setLocation(490,15);
		dealDateLabel.setSize(60,20);
		dealDateLabel.setText("Deal date");
		getContentPane().add(dealDateLabel);
		
		dealEarliestDayComboBox = new JComboBox(listOfDays);
		dealEarliestDayComboBox.setLocation(490,40);
		dealEarliestDayComboBox.setSize(45,30);
		getContentPane().add(dealEarliestDayComboBox);
		
		dealLatestDayComboBox = new JComboBox(listOfDays);
		dealLatestDayComboBox.setLocation(490,80);
		dealLatestDayComboBox.setSize(45,30);
		getContentPane().add(dealLatestDayComboBox);
		
		dealEarliestMonthComboBox = new JComboBox(listOfMonths);
		dealEarliestMonthComboBox.setLocation(545,40);
		dealEarliestMonthComboBox.setSize(45,30);
		getContentPane().add(dealEarliestMonthComboBox);
		
		dealLatestMonthComboBox = new JComboBox(listOfMonths);
		dealLatestMonthComboBox.setLocation(545,80);
		dealLatestMonthComboBox.setSize(45,30);
		getContentPane().add(dealLatestMonthComboBox);
		
		dealEarliestYearComboBox = new JComboBox(listOfYears);
		dealEarliestYearComboBox.setLocation(600,40);
		dealEarliestYearComboBox.setSize(55,30);
		getContentPane().add(dealEarliestYearComboBox);
		
		dealLatestYearComboBox = new JComboBox(listOfYears);
		dealLatestYearComboBox.setLocation(600,80);
		dealLatestYearComboBox.setSize(55,30);
		getContentPane().add(dealLatestYearComboBox);

		dealValueLabel.setLocation(15,90);
		dealValueLabel.setSize(70,20);
		dealValueLabel.setText("Deal value");
		getContentPane().add(dealValueLabel);

		minimumDealValueTextBox.setLocation(46,115);
		minimumDealValueTextBox.setSize(100,30);
		getContentPane().add(minimumDealValueTextBox);

		maximumDealValueTextBox.setLocation(46,155);
		maximumDealValueTextBox.setSize(100,30);
		getContentPane().add(maximumDealValueTextBox);

		currencySymbolLabel.setLocation(35,120);
		currencySymbolLabel.setSize(10,20);
		currencySymbolLabel.setText(backendManager.getMemory().getSettings().getCurrencySymbol());
		getContentPane().add(currencySymbolLabel);

		toValueLabel.setLocation(15,160);
		toValueLabel.setSize(30,20);
		toValueLabel.setText("(to) " + backendManager.getMemory().getSettings().getCurrencySymbol());
		getContentPane().add(toValueLabel);

		toDealDateLabel.setLocation(465,85);
		toDealDateLabel.setSize(20,20);
		toDealDateLabel.setText("(to)");
		getContentPane().add(toDealDateLabel);

		toActionReminderlDateLabel.setLocation(695,85);
		toActionReminderlDateLabel.setSize(20,20);
		toActionReminderlDateLabel.setText("(to)");
		getContentPane().add(toActionReminderlDateLabel);

		probabilityLabel.setLocation(185,90);
		probabilityLabel.setSize(70,20);
		probabilityLabel.setText("Probability");
		getContentPane().add(probabilityLabel);
		
		minimumProbabilitySpinner = new JSpinner(new SpinnerListModel(probabilitiesInPercent));
		minimumProbabilitySpinner.setLocation(210,115);
		minimumProbabilitySpinner.setSize(50,30);
		getContentPane().add(minimumProbabilitySpinner);
		
		maximumProbabilitySpinner = new JSpinner(new SpinnerListModel(probabilitiesInPercent));
		maximumProbabilitySpinner.setLocation(210,155);
		maximumProbabilitySpinner.setSize(50,30);
		getContentPane().add(maximumProbabilitySpinner);
		
		toProbabilityLabel.setLocation(185,160);
		toProbabilityLabel.setSize(20,20);
		toProbabilityLabel.setText("(to)");
		getContentPane().add(toProbabilityLabel);

		percentageLabel0.setLocation(265,120);
		percentageLabel0.setSize(15,20);
		percentageLabel0.setText("%");
		getContentPane().add(percentageLabel0);

		percentageLabel1.setLocation(265,160);
		percentageLabel1.setSize(15,20);
		percentageLabel1.setText("%");
		getContentPane().add(percentageLabel1);

		stageOfNegotiationsLabel.setLocation(330,130);
		stageOfNegotiationsLabel.setSize(120,20);
		stageOfNegotiationsLabel.setText("Stage of negotiations");
		getContentPane().add(stageOfNegotiationsLabel);
		
		stageOfNegotiationsComboBox = new JComboBox(stagesOfNegotiation);
		stageOfNegotiationsComboBox.setLocation(330,155);
		stageOfNegotiationsComboBox.setSize(325,30);
		getContentPane().add(stageOfNegotiationsComboBox);

		notStageOfNegotiationsCheckBox.setLocation(613,130);
		notStageOfNegotiationsCheckBox.setSize(50,20);
		notStageOfNegotiationsCheckBox.setText("NOT");
		getContentPane().add(notStageOfNegotiationsCheckBox);

		cancelButton.setLocation(805,155);
		cancelButton.setSize(100,30);
		cancelButton.addActionListener(this);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);

		searchButton.setLocation(690,155);
		searchButton.setSize(100,30);
		searchButton.addActionListener(this);
		searchButton.setText("Search");
		getContentPane().add(searchButton);
		
		//Set the window's properties
		setTitle("Advanced search");
		setResizable(false);
		setSize(935,230);
		setVisible(true);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If Cancel has been pressed...
		if(e.getSource()==cancelButton)
		{
			 //Close the window
			 this.dispose();
		}
		//If Search has been pressed...
		else if(e.getSource()==searchButton)
		{
			 //If the search is successful...
			 if(backendManager.advancedSearchCurrentLeads(clientNameComboBox.getSelectedIndex() - 1, notClientNameCheckBox.isSelected(), minimumDealValueTextBox.getText(), maximumDealValueTextBox.getText(), (String) minimumProbabilitySpinner.getValue(), (String) maximumProbabilitySpinner.getValue(), stageOfNegotiationsComboBox.getSelectedIndex() - 1, notStageOfNegotiationsCheckBox.isSelected(), (String) dealEarliestDayComboBox.getSelectedItem(), (String) dealEarliestMonthComboBox.getSelectedItem(), (String) dealEarliestYearComboBox.getSelectedItem(), (String) dealLatestDayComboBox.getSelectedItem(), (String) dealLatestMonthComboBox.getSelectedItem(), (String) dealLatestYearComboBox.getSelectedItem(), (String) actionReminderEarliestDayComboBox.getSelectedItem(), (String) actionReminderEarliestMonthComboBox.getSelectedItem(), (String) actionReminderEarliestYearComboBox.getSelectedItem(), (String) actionReminderLatestDayComboBox.getSelectedItem(), (String) actionReminderLatestMonthComboBox.getSelectedItem(), (String) actionReminderLatestYearComboBox.getSelectedItem()))
				 dispose();
		}
	}
}  

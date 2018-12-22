package gui.historicaldata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class AdvancedSearchHistoricalLeadsFrame extends JDialog implements ActionListener 
{
	JLabel clientNameLabel = new JLabel();
	JComboBox clientNameComboBox;
	JCheckBox notClientNameCheckBox = new JCheckBox();
	JRadioButton succeededOrFailedRadioButton;
	JRadioButton succeededRadioButton;
	JRadioButton failedRadioButton;
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
	
	public AdvancedSearchHistoricalLeadsFrame(BackendManager backendManager, String[] days, String[] months, String[] years, String[] stagesOfNegotiation, String[] clients)
	{
		this.backendManager = backendManager;
		listOfDays = days;
		listOfMonths = months;
		listOfYears = years;
		this.stagesOfNegotiation = stagesOfNegotiation;
		this.clients = clients;
		//Set the dialog's contentPane's layout to null, so that components can be placed precisely
		getContentPane().setLayout(null);
		//Disable the main GUI frame
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		addWindowListener(backendManager);
		
		setUpGUI();
	}
	
	public void setUpGUI()
	{
		succeededOrFailedRadioButton = new JRadioButton("Succeeded or failed");
		succeededOrFailedRadioButton.setLocation(315,75);
		succeededOrFailedRadioButton.setSize(120,20);
		succeededOrFailedRadioButton.setSelected(true);
		getContentPane().add(succeededOrFailedRadioButton);
		
		succeededRadioButton = new JRadioButton("Succeeded");
		succeededRadioButton.setLocation(315,95);
		succeededRadioButton.setSize(80,20);
		getContentPane().add(succeededRadioButton);
		
		failedRadioButton = new JRadioButton("Failed");
		failedRadioButton.setLocation(315,115);
		failedRadioButton.setSize(80,20);
		getContentPane().add(failedRadioButton);
		
		//Put the radio buttons in a group so that only one may be selected at once
		ButtonGroup group = new ButtonGroup();
		group.add(succeededOrFailedRadioButton);
		group.add(succeededRadioButton);
		group.add(failedRadioButton);
		
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

		stageOfNegotiationsLabel.setLocation(165,130);
		stageOfNegotiationsLabel.setSize(120,20);
		stageOfNegotiationsLabel.setText("Stage of negotiations");
		getContentPane().add(stageOfNegotiationsLabel);
		
		stageOfNegotiationsComboBox = new JComboBox(stagesOfNegotiation);
		stageOfNegotiationsComboBox.setLocation(165,155);
		stageOfNegotiationsComboBox.setSize(325,30);
		getContentPane().add(stageOfNegotiationsComboBox);

		notStageOfNegotiationsCheckBox.setLocation(448,130);
		notStageOfNegotiationsCheckBox.setSize(50,20);
		notStageOfNegotiationsCheckBox.setText("NOT");
		getContentPane().add(notStageOfNegotiationsCheckBox);

		cancelButton.setLocation(625,155);
		cancelButton.setSize(100,30);
		cancelButton.addActionListener(this);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);

		searchButton.setLocation(510,155);
		searchButton.setSize(100,30);
		searchButton.addActionListener(this);
		searchButton.setText("Search");
		getContentPane().add(searchButton);
		
		//Set the window's properties
		setTitle("Advanced search");
		setResizable(false);
		setSize(755,230);
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
			 if(backendManager.advancedSearchHistoricalLeads(clientNameComboBox.getSelectedIndex() - 1, notClientNameCheckBox.isSelected(), minimumDealValueTextBox.getText(), maximumDealValueTextBox.getText(), stageOfNegotiationsComboBox.getSelectedIndex() - 1, notStageOfNegotiationsCheckBox.isSelected(), (String) dealEarliestDayComboBox.getSelectedItem(), (String) dealEarliestMonthComboBox.getSelectedItem(), (String) dealEarliestYearComboBox.getSelectedItem(), (String) dealLatestDayComboBox.getSelectedItem(), (String) dealLatestMonthComboBox.getSelectedItem(), (String) dealLatestYearComboBox.getSelectedItem(), succeededRadioButton.isSelected(), failedRadioButton.isSelected()))
				 dispose();
		}
	}
}  

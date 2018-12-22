package gui.historicaldata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class EditHistoricalLeadFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton saveButton;
	private JCheckBox newClientCheckBox;
	private JComboBox dealDayComboBox;
	private JComboBox dealYearComboBox;
	private JComboBox dealMonthComboBox;
	private JLabel dealDateLabel;
	private JLabel clientNameLabel;
	private JComboBox clientNameComboBox;
	private JLabel dealValueCurrencySymbolLabel;
	JCheckBox succeededCheckBox;
	private JComboBox stageOfNegotiationsComboBox;
	private JLabel stageOfNegotiationsLabel;
	private JLabel dealValueLabel;
	private JTextField dealValueTextBox;
	
	private final BackendManager backendManager;
	private final String[] listOfDays;
	private final String[] listOfMonths;
	private final String[] listOfYears;
	private final String[] stagesOfNegotiation;
	private final String[] clients;
	private final int clientID;
	private final String dealDay;
	private final String dealMonth;
	private final String dealYear;
	private final int stageOfNegotiationsID;
	private final boolean mWasSuccessful;
	private final int value;
	final int mLeadID;
	
	public EditHistoricalLeadFrame(BackendManager backendMgr, String[] days, String[] months, String[] years, String[] stages, String[] clients, int clientID, String dealDay, String dealMonth, String dealYear, int stageOfNegotiationsID, int value, int leadID, boolean wasSuccessful)
	{
		backendManager = backendMgr;
		listOfDays = days;
		listOfMonths = months;
		listOfYears = years;
		stagesOfNegotiation = stages;
		this.clients = clients;
		this.clientID = clientID;
		this.dealDay = dealDay;
		this.dealMonth = dealMonth;
		this.dealYear = dealYear;
		this.stageOfNegotiationsID = stageOfNegotiationsID;
		mWasSuccessful = wasSuccessful;
		this.value = value;
		mLeadID = leadID;
		//Set the JDialog's contentPane's layout to null, so that components can be placed precisely
		getContentPane().setLayout(null);
		//Disable the main GUI frame
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		
		addWindowListener(backendManager);
		
		setUpGUI();
	}
   
   private void setUpGUI()
   {
		//Set the size, location, text and other properties of the window's components
		cancelButton = new JButton();
		cancelButton.setLocation(585,180);
		cancelButton.setSize(100,30);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		saveButton = new JButton("Save");
		saveButton.setLocation(470,180);
		saveButton.setSize(100,30);
		getContentPane().add(saveButton);
		saveButton.addActionListener(this);
		
		newClientCheckBox = new JCheckBox("New client");
		newClientCheckBox.setLocation(345,15);
		newClientCheckBox.setSize(90,20);
		getContentPane().add(newClientCheckBox);
		
		clientNameLabel = new JLabel("Client name");
		clientNameLabel.setLocation(15,15);
		clientNameLabel.setSize(70,20);
		getContentPane().add(clientNameLabel);
		
		clientNameComboBox = new JComboBox(clients);
		clientNameComboBox.setLocation(15,40);
		clientNameComboBox.setSize(420,30);
		clientNameComboBox.setSelectedIndex(clientID + 1);
		getContentPane().add(clientNameComboBox);
		
		dealDayComboBox = new JComboBox(listOfDays);
		dealDayComboBox.setLocation(270,110);
		dealDayComboBox.setSize(45,30);
		dealDayComboBox.setSelectedItem(dealDay);
		getContentPane().add(dealDayComboBox);
		
		dealYearComboBox = new JComboBox(listOfYears);
		dealYearComboBox.setLocation(380,110);
		dealYearComboBox.setSize(55,30);
		dealYearComboBox.setSelectedItem(dealYear);
		getContentPane().add(dealYearComboBox);
		
		dealMonthComboBox = new JComboBox(listOfMonths);
		dealMonthComboBox.setLocation(325,110);
		dealMonthComboBox.setSize(45,30);
		dealMonthComboBox.setSelectedItem(dealMonth);
		getContentPane().add(dealMonthComboBox);
		
		dealDateLabel = new JLabel("Deal date");
		dealDateLabel.setLocation(270,85);
		dealDateLabel.setSize(60,20);
		getContentPane().add(dealDateLabel);
		
		dealValueCurrencySymbolLabel = new JLabel(backendManager.getMemory().getSettings().getCurrencySymbol());
		dealValueCurrencySymbolLabel.setLocation(15,115);
		dealValueCurrencySymbolLabel.setSize(15,20);
		getContentPane().add(dealValueCurrencySymbolLabel);
		
		succeededCheckBox = new JCheckBox("Succeeded");
		succeededCheckBox.setLocation(165,110);
		succeededCheckBox.setSize(80,20);
		succeededCheckBox.setSelected(mWasSuccessful);
		getContentPane().add(succeededCheckBox);
		
		stageOfNegotiationsComboBox = new JComboBox(stagesOfNegotiation);
		stageOfNegotiationsComboBox.setLocation(15,180);
		stageOfNegotiationsComboBox.setSize(335,30);
		stageOfNegotiationsComboBox.setSelectedIndex(stageOfNegotiationsID + 1);
		getContentPane().add(stageOfNegotiationsComboBox);
		
		stageOfNegotiationsLabel = new JLabel("Stage of negotiations");
		stageOfNegotiationsLabel.setLocation(15,155);
		stageOfNegotiationsLabel.setSize(130,20);
		getContentPane().add(stageOfNegotiationsLabel);
		
		dealValueLabel = new JLabel("Deal value");
		dealValueLabel.setLocation(15,85);
		dealValueLabel.setSize(65,20);
		getContentPane().add(dealValueLabel);
		
		dealValueTextBox = new JTextField();
		dealValueTextBox.setLocation(25,110);
		dealValueTextBox.setSize(90,30);
		dealValueTextBox.setText(String.valueOf(value));
		getContentPane().add(dealValueTextBox);
		
		//Set the window's properties
		setTitle("Edit lead");
		setResizable(false);
		setSize(710,255);
		setVisible(true);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Cancel' has been pressed...
		if(e.getSource() == cancelButton)
		{
			//Close the window
			dispose();
		}
		//If 'Save' is pressed...
		else if(e.getSource() == saveButton)
		{
			//If editing the lead is successful...
			if(backendManager.editHistoricalLead(newClientCheckBox.isSelected(), clientNameComboBox.getSelectedIndex(), dealDayComboBox.getSelectedItem(), dealMonthComboBox.getSelectedItem(), dealYearComboBox.getSelectedItem(), dealValueTextBox.getText(), stageOfNegotiationsComboBox.getSelectedIndex(), succeededCheckBox.isSelected(), mLeadID))
				dispose();
		}
	}
}  
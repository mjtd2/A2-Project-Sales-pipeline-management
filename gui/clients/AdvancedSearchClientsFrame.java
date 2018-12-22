package gui.clients;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class AdvancedSearchClientsFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton searchButton;
	private JLabel emailAddressLabel;
	private JLabel clientNameLabel;
	private JLabel phoneNumberLabel;
	private JTextField emailAddressTextBox;
	JCheckBox notEmailAddressCheckBox = new JCheckBox();
	JTextField clientNameTextBox;
	JCheckBox notClientNameCheckBox = new JCheckBox();
	JTextField phoneNumberTextBox;
	JCheckBox notPhoneNumberCheckBox = new JCheckBox();
	JLabel dealDateLabel;
	JLabel currencySymbolLabel;
	JLabel expectedIncomeLabel;
	JLabel toValueLabel = new JLabel();
	JLabel toDealDateLabel = new JLabel();
	JComboBox dealEarliestDayComboBox;
	JComboBox dealLatestDayComboBox;
	JComboBox dealEarliestMonthComboBox;
	JComboBox dealLatestMonthComboBox;
	JComboBox dealEarliestYearComboBox;
	JComboBox dealLatestYearComboBox;
	JTextField minimumExpectedIncomeTextBox = new JTextField();
	JTextField maximumExpectedIncomeTextBox = new JTextField();
	
	private final BackendManager backendManager;
	private final String[] mListOfDays;
	private final String[] mListOfMonths;
	private final String[] mListOfYears;
	
	public AdvancedSearchClientsFrame(BackendManager backendMgr, String[] listOfDays, String[] listOfMonths, String[] listOfYears)
	{
		backendManager = backendMgr;
		mListOfDays = listOfDays;
		mListOfMonths = listOfMonths;
		mListOfYears = listOfYears;
		
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
		cancelButton = new JButton("Cancel");
		cancelButton.setLocation(670,140);
		cancelButton.setSize(100,30);
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		searchButton = new JButton("Search");
		searchButton.setLocation(670,100);
		searchButton.setSize(100,30);
		getContentPane().add(searchButton);
		searchButton.addActionListener(this);
		
		clientNameLabel = new JLabel("Client name");
		clientNameLabel.setLocation(15,10);
		clientNameLabel.setSize(70,20);
		getContentPane().add(clientNameLabel);
		
		clientNameTextBox = new JTextField();
		clientNameTextBox.setLocation(15,30);
		clientNameTextBox.setSize(420,30);
		getContentPane().add(clientNameTextBox);
		
		phoneNumberLabel = new JLabel("Phone number");
		phoneNumberLabel.setLocation(15,120);
		phoneNumberLabel.setSize(70,20);
		getContentPane().add(phoneNumberLabel);
		
		phoneNumberTextBox = new JTextField();
		phoneNumberTextBox.setLocation(15,140);
		phoneNumberTextBox.setSize(420,30);
		getContentPane().add(phoneNumberTextBox);
		
		emailAddressLabel = new JLabel("Email address");
		emailAddressLabel.setLocation(15,65);
		emailAddressLabel.setSize(65,20);
		getContentPane().add(emailAddressLabel);
		
		emailAddressTextBox = new JTextField();
		emailAddressTextBox.setLocation(15,85);
		emailAddressTextBox.setSize(420,30);
		getContentPane().add(emailAddressTextBox);
		
		notClientNameCheckBox.setLocation(393,10);
		notClientNameCheckBox.setSize(50,20);
		notClientNameCheckBox.setText("NOT");
		getContentPane().add(notClientNameCheckBox);
		
		notEmailAddressCheckBox.setLocation(393,65);
		notEmailAddressCheckBox.setSize(50,20);
		notEmailAddressCheckBox.setText("NOT");
		getContentPane().add(notEmailAddressCheckBox);
		
		notPhoneNumberCheckBox.setLocation(393,120);
		notPhoneNumberCheckBox.setSize(50,20);
		notPhoneNumberCheckBox.setText("NOT");
		getContentPane().add(notPhoneNumberCheckBox);
		
		expectedIncomeLabel = new JLabel("Total expected income");
		expectedIncomeLabel.setLocation(470,10);
		expectedIncomeLabel.setSize(150,20);
		getContentPane().add(expectedIncomeLabel);
		
		currencySymbolLabel = new JLabel(backendManager.getMemory().getSettings().getCurrencySymbol());
		currencySymbolLabel.setLocation(479,35);
		currencySymbolLabel.setSize(10,20);
		getContentPane().add(currencySymbolLabel);
		
		toValueLabel.setLocation(610,35);
		toValueLabel.setSize(30,20);
		toValueLabel.setText("(to) " + backendManager.getMemory().getSettings().getCurrencySymbol());
		getContentPane().add(toValueLabel);
		
		minimumExpectedIncomeTextBox.setLocation(490,30);
		minimumExpectedIncomeTextBox.setSize(100,30);
		getContentPane().add(minimumExpectedIncomeTextBox);

		maximumExpectedIncomeTextBox.setLocation(641,30);
		maximumExpectedIncomeTextBox.setSize(100,30);
		getContentPane().add(maximumExpectedIncomeTextBox);
		
		toDealDateLabel.setLocation(465,145);
		toDealDateLabel.setSize(20,20);
		toDealDateLabel.setText("(to)");
		getContentPane().add(toDealDateLabel);
		
		dealDateLabel = new JLabel("Deal date");
		dealDateLabel.setLocation(490,75);
		dealDateLabel.setSize(60,20);
		getContentPane().add(dealDateLabel);
		
		dealEarliestDayComboBox = new JComboBox(mListOfDays);
		dealEarliestDayComboBox.setLocation(490,100);
		dealEarliestDayComboBox.setSize(45,30);
		getContentPane().add(dealEarliestDayComboBox);
		
		dealLatestDayComboBox = new JComboBox(mListOfDays);
		dealLatestDayComboBox.setLocation(490,140);
		dealLatestDayComboBox.setSize(45,30);
		getContentPane().add(dealLatestDayComboBox);
		
		dealEarliestMonthComboBox = new JComboBox(mListOfMonths);
		dealEarliestMonthComboBox.setLocation(545,100);
		dealEarliestMonthComboBox.setSize(45,30);
		getContentPane().add(dealEarliestMonthComboBox);
		
		dealLatestMonthComboBox = new JComboBox(mListOfMonths);
		dealLatestMonthComboBox.setLocation(545,140);
		dealLatestMonthComboBox.setSize(45,30);
		getContentPane().add(dealLatestMonthComboBox);
		
		dealEarliestYearComboBox = new JComboBox(mListOfYears);
		dealEarliestYearComboBox.setLocation(600,100);
		dealEarliestYearComboBox.setSize(55,30);
		getContentPane().add(dealEarliestYearComboBox);
		
		dealLatestYearComboBox = new JComboBox(mListOfYears);
		dealLatestYearComboBox.setLocation(600,140);
		dealLatestYearComboBox.setSize(55,30);
		getContentPane().add(dealLatestYearComboBox);
		
		//Set the window's properties
		setTitle("Advanced search");
		setResizable(false);
		setSize(785,210);
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
		//If 'Search' is pressed...
		else if(e.getSource() == searchButton)
		{
			//If searching for the client is possible...
			if(backendManager.advancedSearchClients(clientNameTextBox.getText(), emailAddressTextBox.getText(), phoneNumberTextBox.getText(), notClientNameCheckBox.isSelected(), notEmailAddressCheckBox.isSelected(), notPhoneNumberCheckBox.isSelected(), minimumExpectedIncomeTextBox.getText(), maximumExpectedIncomeTextBox.getText(), (String) dealEarliestDayComboBox.getSelectedItem(), (String) dealEarliestMonthComboBox.getSelectedItem(), (String) dealEarliestYearComboBox.getSelectedItem(), (String) dealLatestDayComboBox.getSelectedItem(), (String) dealLatestMonthComboBox.getSelectedItem(), (String) dealLatestYearComboBox.getSelectedItem()))
				dispose();
		}
	}
}  
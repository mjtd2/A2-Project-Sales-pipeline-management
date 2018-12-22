package gui.clients;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class DeleteClientFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton deleteButton;
	JLabel clientNameLabel;
	JLabel phoneNumberLabel;
	JLabel expectedIncomeLabel;
	JLabel numberOfCurrentLeadsLabel;
	JLabel emailAddressLabel;
	JLabel areYouSureLabel;
	JLabel numberOfHistoricalLeadsLabel;
	JCheckBox sendToHistoricalCheckbox;
	JRadioButton anonymiseHistoricalDataRadioButton;
	JRadioButton deleteHistoricalDataRadioButton;
	
	final String mClientName;
	final String mPhoneNumber;
	final String mExpectedIncome;
	final int mNumberOfCurrentLeads;
	final int mNumberOfHistoricalLeads;
	final String mEmailAddress;
	final BackendManager mBackendManager;
	final int mClientID;
	
	public DeleteClientFrame(BackendManager backendManager, String clientName, String phoneNumber, String emailAddress, String expectedIncome, int numberOfCurrentLeads, int numberOfHistoricalLeads, int clientID)
	{
		mClientName = clientName;
		mPhoneNumber = phoneNumber;
		mExpectedIncome = expectedIncome;
		mNumberOfCurrentLeads = numberOfCurrentLeads;
		mNumberOfHistoricalLeads = numberOfHistoricalLeads;
		mEmailAddress = emailAddress;
		mBackendManager = backendManager;
		mClientID = clientID;
		
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
		clientNameLabel = new JLabel("<html>Client name: <b>" + mClientName);
		clientNameLabel.setLocation(15, 15);
		clientNameLabel.setSize(290, 20);
		getContentPane().add(clientNameLabel);
		
		phoneNumberLabel = new JLabel("<html>Phone number: <b>" + mPhoneNumber);
		phoneNumberLabel.setLocation(15, 35);
		phoneNumberLabel.setSize(290, 20);
		getContentPane().add(phoneNumberLabel);
		
		emailAddressLabel = new JLabel("<html>Email address: <b>" + mEmailAddress);
		emailAddressLabel.setLocation(15, 55);
		emailAddressLabel.setSize(290, 20);
		getContentPane().add(emailAddressLabel);
		
		expectedIncomeLabel = new JLabel("<html>Total expected income: <b>" + mExpectedIncome);
		expectedIncomeLabel.setLocation(15, 75);
		expectedIncomeLabel.setSize(290, 20);
		getContentPane().add(expectedIncomeLabel);
		
		numberOfCurrentLeadsLabel = new JLabel("<html>Number of current leads: <b>" + mNumberOfCurrentLeads);
		numberOfCurrentLeadsLabel.setLocation(15, 95);
		numberOfCurrentLeadsLabel.setSize(290, 20);
		getContentPane().add(numberOfCurrentLeadsLabel);
		
		numberOfHistoricalLeadsLabel = new JLabel("<html>Number of historical leads: <b>" + mNumberOfHistoricalLeads);
		numberOfHistoricalLeadsLabel.setLocation(15, 115);
		numberOfHistoricalLeadsLabel.setSize(290, 20);
		getContentPane().add(numberOfHistoricalLeadsLabel);
		
		areYouSureLabel = new JLabel("<html><p style=\"text-align:center;\">Are you sure you want to<br>delete this client?", SwingConstants.CENTER);
		areYouSureLabel.setLocation(335, 15);
		areYouSureLabel.setSize(290, 35);
		getContentPane().add(areYouSureLabel);
		
		anonymiseHistoricalDataRadioButton = new JRadioButton("Anonymise historical data");
		anonymiseHistoricalDataRadioButton.setLocation(400, 60);
		anonymiseHistoricalDataRadioButton.setSize(290, 20);
		anonymiseHistoricalDataRadioButton.setSelected(true);
		getContentPane().add(anonymiseHistoricalDataRadioButton);
		
		deleteHistoricalDataRadioButton = new JRadioButton("Delete historical data");
		deleteHistoricalDataRadioButton.setLocation(400, 80);
		deleteHistoricalDataRadioButton.setSize(290, 20);
		getContentPane().add(deleteHistoricalDataRadioButton);
		
		//Put the radio buttons in a group so that only one may be selected at once
		ButtonGroup group = new ButtonGroup();
		group.add(anonymiseHistoricalDataRadioButton);
		group.add(deleteHistoricalDataRadioButton);
		
		cancelButton = new JButton();
		cancelButton.setLocation(488,105);
		cancelButton.setSize(100,30);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		deleteButton = new JButton();
		deleteButton.setLocation(372,105);
		deleteButton.setSize(100,30);
		deleteButton.setText("Delete");
		getContentPane().add(deleteButton);
		deleteButton.addActionListener(this);
		
		//Set the window's properties
		setTitle("Delete client");
		setResizable(false);
		setSize(640,180);
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
		else if(e.getSource() == deleteButton)
		{
			mBackendManager.deleteClient(mClientID, deleteHistoricalDataRadioButton.isSelected());
			dispose();
		}
	}
}
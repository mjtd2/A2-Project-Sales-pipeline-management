package gui.currentleads;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class EditLeadFrame extends JDialog implements ActionListener
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
	private JLabel probabilityPercentageSymbolTextBox;
	private JLabel dealValueCurrencySymbolTextBox;
	private JSpinner probabilitySpinner;
	private JLabel probabilityLabel;
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
	private final int[] mDefaultProbabilities;
	private final int clientID;
	private final String dealDay;
	private final String dealMonth;
	private final String dealYear;
	private final int stageOfNegotiationsID;
	private final int probability;
	private final int value;
	final int mLeadID;
	
	public EditLeadFrame(BackendManager backendMgr, String[] days, String[] months, String[] years, String[] stages, String[] clients, int clientID, String dealDay, String dealMonth, String dealYear, int stageOfNegotiationsID, int probability, int value, int leadID, int[] defaultProbabilities)
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
		this.probability = probability;
		this.value = value;
		mLeadID = leadID;
		mDefaultProbabilities = defaultProbabilities;
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
		
		saveButton = new JButton();
		saveButton.setLocation(470,180);
		saveButton.setSize(100,30);
		saveButton.setText("Save");
		getContentPane().add(saveButton);
		saveButton.addActionListener(this);
		
		newClientCheckBox = new JCheckBox();
		newClientCheckBox.setLocation(345,15);
		newClientCheckBox.setSize(90,20);
		newClientCheckBox.setText("New client");
		getContentPane().add(newClientCheckBox);
		
		clientNameLabel = new JLabel();
		clientNameLabel.setLocation(15,15);
		clientNameLabel.setSize(70,20);
		clientNameLabel.setText("Client name");
		getContentPane().add(clientNameLabel);
		
		clientNameComboBox = new JComboBox(clients);
		clientNameComboBox.setLocation(15,40);
		clientNameComboBox.setSize(420,30);
		clientNameComboBox.setSelectedIndex(clientID);
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
		
		dealDateLabel = new JLabel();
		dealDateLabel.setLocation(270,85);
		dealDateLabel.setSize(60,20);
		dealDateLabel.setText("Deal date");
		getContentPane().add(dealDateLabel);
		
		probabilityPercentageSymbolTextBox = new JLabel();
		probabilityPercentageSymbolTextBox.setLocation(420,185);
		probabilityPercentageSymbolTextBox.setSize(15,20);
		probabilityPercentageSymbolTextBox.setText("%");
		getContentPane().add(probabilityPercentageSymbolTextBox);
		
		dealValueCurrencySymbolTextBox = new JLabel();
		dealValueCurrencySymbolTextBox.setLocation(15,115);
		dealValueCurrencySymbolTextBox.setSize(15,20);
		dealValueCurrencySymbolTextBox.setText(backendManager.getMemory().getSettings().getCurrencySymbol());
		getContentPane().add(dealValueCurrencySymbolTextBox);
		
		//0 to 100
		probabilitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		probabilitySpinner.setLocation(365,180);
		probabilitySpinner.setSize(50,30);
		probabilitySpinner.setValue((Integer) probability);
		getContentPane().add(probabilitySpinner);
		
		probabilityLabel = new JLabel();
		probabilityLabel.setLocation(365,155);
		probabilityLabel.setSize(70,20);
		probabilityLabel.setText("Probability");
		getContentPane().add(probabilityLabel);
		
		stageOfNegotiationsComboBox = new JComboBox(stagesOfNegotiation);
		stageOfNegotiationsComboBox.setLocation(15,180);
		stageOfNegotiationsComboBox.setSize(335,30);
		stageOfNegotiationsComboBox.setSelectedIndex(stageOfNegotiationsID);
		stageOfNegotiationsComboBox.addActionListener(this);
		getContentPane().add(stageOfNegotiationsComboBox);
		
		stageOfNegotiationsLabel = new JLabel();
		stageOfNegotiationsLabel.setLocation(15,155);
		stageOfNegotiationsLabel.setSize(130,20);
		stageOfNegotiationsLabel.setText("Stage of negotiations");
		getContentPane().add(stageOfNegotiationsLabel);
		
		dealValueLabel = new JLabel();
		dealValueLabel.setLocation(15,85);
		dealValueLabel.setSize(65,20);
		dealValueLabel.setText("Deal value");
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
		//If a new stage of negotiations is chosen...
		if(e.getSource() == stageOfNegotiationsComboBox)
			//Select the corresponding probability
			probabilitySpinner.setValue(new Integer(mDefaultProbabilities[stageOfNegotiationsComboBox.getSelectedIndex()]));
		//If 'Cancel' has been pressed...
		if(e.getSource() == cancelButton)
			//Close the window
			dispose();
		//If 'Save' is pressed...
		else if(e.getSource() == saveButton)
		{
			//If editing the lead is successful...
			if(backendManager.editLead(clientNameComboBox.getSelectedIndex(), dealDayComboBox.getSelectedItem(), dealMonthComboBox.getSelectedItem(), dealYearComboBox.getSelectedItem(), dealValueTextBox.getText(), stageOfNegotiationsComboBox.getSelectedIndex(), probabilitySpinner.getValue(), mLeadID, newClientCheckBox.isSelected()))
				dispose();
		}
	}
}  
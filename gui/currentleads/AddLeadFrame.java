package gui.currentleads;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class AddLeadFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton saveButton;
	private JCheckBox reminderCheckBox;
	private JCheckBox newClientCheckBox;
	private JComboBox reminderDayComboBox;
	private JComboBox reminderMonthComboBox;
	private JLabel reminderDateLabel;
	private JLabel reminderMessageLabel;
	private JTextArea reminderMessageTextArea;
	private JScrollPane reminderMessageScrollPane;
	private JComboBox dealDayComboBox;
	private JComboBox reminderYearComboBox;
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
	
	public AddLeadFrame(BackendManager backendMgr, String[] days, String[] months, String[] years, String[] stages, String[] clients, int[] defaultProbabilities)
	{
		backendManager = backendMgr;
		listOfDays = days;
		listOfMonths = months;
		listOfYears = years;
		stagesOfNegotiation = stages;
		this.clients = clients;
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
		cancelButton.setLocation(835,180);
		cancelButton.setSize(100,30);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		saveButton = new JButton();
		saveButton.setLocation(720,180);
		saveButton.setSize(100,30);
		saveButton.setText("Save");
		getContentPane().add(saveButton);
		if(stagesOfNegotiation.length == 0)
		{
			saveButton.setEnabled(false);
			saveButton.setToolTipText("Please add a stage of negotiations (on the Settings tab) first.");
		}
		saveButton.addActionListener(this);
		
		newClientCheckBox = new JCheckBox();
		newClientCheckBox.setLocation(345,15);
		newClientCheckBox.setSize(90,20);
		newClientCheckBox.setText("New client");
		if(clients.length == 0)
		{
			newClientCheckBox.setSelected(true);
			newClientCheckBox.setEnabled(false);
		}
		getContentPane().add(newClientCheckBox);
		
		clientNameLabel = new JLabel();
		clientNameLabel.setLocation(15,15);
		clientNameLabel.setSize(70,20);
		clientNameLabel.setText("Client name");
		getContentPane().add(clientNameLabel);
		
		clientNameComboBox = new JComboBox(clients);
		clientNameComboBox.setLocation(15,40);
		clientNameComboBox.setSize(420,30);
		getContentPane().add(clientNameComboBox);
		
		reminderCheckBox = new JCheckBox();
		reminderCheckBox.setLocation(555,40);
		reminderCheckBox.setSize(90,20);
		reminderCheckBox.setText("Add reminder");
		getContentPane().add(reminderCheckBox);
		
		reminderDayComboBox = new JComboBox(listOfDays);
		reminderDayComboBox.setLocation(770,40);
		reminderDayComboBox.setSize(45,30);
		getContentPane().add(reminderDayComboBox);
		
		reminderMonthComboBox = new JComboBox(listOfMonths);
		reminderMonthComboBox.setLocation(825,40);
		reminderMonthComboBox.setSize(45,30);
		getContentPane().add(reminderMonthComboBox);
		
		reminderYearComboBox = new JComboBox(listOfYears);
		reminderYearComboBox.setLocation(880,40);
		reminderYearComboBox.setSize(55,30);
		getContentPane().add(reminderYearComboBox);
		
		reminderDateLabel = new JLabel();
		reminderDateLabel.setLocation(770,15);
		reminderDateLabel.setSize(90,20);
		reminderDateLabel.setText("Reminder date");
		getContentPane().add(reminderDateLabel);
		
		reminderMessageLabel = new JLabel();
		reminderMessageLabel.setLocation(555,65);
		reminderMessageLabel.setSize(120,20);
		reminderMessageLabel.setText("Reminder message");
		getContentPane().add(reminderMessageLabel);
		
		reminderMessageTextArea = new JTextArea();
		//Set the text area to wrap lines at whitespace before the edge of the text area
		reminderMessageTextArea.setLineWrap(true);
		reminderMessageTextArea.setWrapStyleWord(true);
		
		reminderMessageScrollPane = new JScrollPane(reminderMessageTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		reminderMessageScrollPane.setLocation(555,90);
		reminderMessageScrollPane.setSize(380,75);
		getContentPane().add(reminderMessageScrollPane);
		
		dealDayComboBox = new JComboBox(listOfDays);
		dealDayComboBox.setLocation(270,110);
		dealDayComboBox.setSize(45,30);
		getContentPane().add(dealDayComboBox);
		
		dealYearComboBox = new JComboBox(listOfYears);
		dealYearComboBox.setLocation(380,110);
		dealYearComboBox.setSize(55,30);
		getContentPane().add(dealYearComboBox);
		
		dealMonthComboBox = new JComboBox(listOfMonths);
		dealMonthComboBox.setLocation(325,110);
		dealMonthComboBox.setSize(45,30);
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
		getContentPane().add(probabilitySpinner);
		
		probabilityLabel = new JLabel();
		probabilityLabel.setLocation(365,155);
		probabilityLabel.setSize(70,20);
		probabilityLabel.setText("Probability");
		getContentPane().add(probabilityLabel);
		
		stageOfNegotiationsComboBox = new JComboBox(stagesOfNegotiation);
		stageOfNegotiationsComboBox.setLocation(15,180);
		stageOfNegotiationsComboBox.setSize(335,30);
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
		getContentPane().add(dealValueTextBox);
		
		//Set the window's properties
		setTitle("Add lead");
		setResizable(false);
		setSize(960,255);
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
		else if(e.getSource() == cancelButton)
			//Close the window
			this.dispose();
		//If 'Save' is pressed...
		else if(e.getSource() == saveButton)
		{
			//If saving is successful...
			if(backendManager.addCurrentLead(clientNameComboBox.getSelectedIndex(), dealDayComboBox.getSelectedItem(), dealMonthComboBox.getSelectedItem(), dealYearComboBox.getSelectedItem(), dealValueTextBox.getText(), stageOfNegotiationsComboBox.getSelectedIndex(), probabilitySpinner.getValue(), reminderCheckBox.isSelected(),  reminderDayComboBox.getSelectedItem(), reminderMonthComboBox.getSelectedItem(), reminderYearComboBox.getSelectedItem(), reminderMessageTextArea.getText(), newClientCheckBox.isSelected()))
				this.dispose();
		}
	}
}  
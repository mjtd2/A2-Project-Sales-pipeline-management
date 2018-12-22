package gui.actionreminders;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class EditActionReminderFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton saveButton;
	private JComboBox reminderDayComboBox;
	private JComboBox reminderMonthComboBox;
	private JComboBox reminderYearComboBox;
	JLabel reminderDateLabel;
	JLabel clientNameLabel;
	JLabel dealDateLabel;
	JLabel stageOfNegotiationsLabel;
	JLabel probabilityLabel;
	JLabel dealValueLabel;
	JLabel reminderMessageLabel;
	JTextArea reminderMessageTextArea;
	JScrollPane reminderMessageScrollPane;
	
	final String mReminderDay;
	final String mReminderMonth;
	final String mReminderYear;
	final String mClientName;
	final String mDealDateString;
	final String mStageOfNegotiations;
	final String mProbabilityString;
	final String mDealValueString;
	final String mReminderMessage;
	final BackendManager mBackendManager;
	final String[] mDays;
	final String[] mMonths;
	final String[] mYears;
	final int mReminderIndex;
	
	public EditActionReminderFrame(BackendManager backendManager, String[] days, String[] months, String[] years, String reminderDay, String reminderMonth, String reminderYear, String clientName, String dealDateString, String stageOfNegotiations, String probabilityString, String dealValueString, String reminderMessage, int reminderIndex)
	{
		mReminderDay = reminderDay;
		mReminderMonth = reminderMonth;
		mReminderYear = reminderYear;
		mClientName = clientName;
		mDealDateString = dealDateString;
		mStageOfNegotiations = stageOfNegotiations;
		mProbabilityString = probabilityString;
		mDealValueString = dealValueString;
		mReminderMessage = reminderMessage;
		mBackendManager = backendManager;
		mDays = days;
		mMonths = months;
		mYears = years;
		mReminderIndex = reminderIndex;
		
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
		reminderDateLabel = new JLabel("Reminder date:");
		reminderDateLabel.setLocation(15, 15);
		reminderDateLabel.setSize(290, 20);
		getContentPane().add(reminderDateLabel);
		
		reminderDayComboBox = new JComboBox(mDays);
		reminderDayComboBox.setLocation(15,40);
		reminderDayComboBox.setSize(45,30);
		reminderDayComboBox.setSelectedItem(mReminderDay);
		getContentPane().add(reminderDayComboBox);
		
		reminderMonthComboBox = new JComboBox(mMonths);
		reminderMonthComboBox.setLocation(70,40);
		reminderMonthComboBox.setSize(45,30);
		reminderMonthComboBox.setSelectedItem(mReminderMonth);
		getContentPane().add(reminderMonthComboBox);
		
		reminderYearComboBox = new JComboBox(mYears);
		reminderYearComboBox.setLocation(125,40);
		reminderYearComboBox.setSize(55,30);
		reminderYearComboBox.setSelectedItem(mReminderYear);
		getContentPane().add(reminderYearComboBox);
		
		clientNameLabel = new JLabel("<html>Client name: <b>" + mClientName);
		clientNameLabel.setLocation(15, 90);
		clientNameLabel.setSize(290, 20);
		getContentPane().add(clientNameLabel);
		
		dealDateLabel = new JLabel("<html>Deal date: <b>" + mDealDateString);
		dealDateLabel.setLocation(15, 115);
		dealDateLabel.setSize(290, 20);
		getContentPane().add(dealDateLabel);
		
		dealValueLabel = new JLabel("<html>Deal value: <b>" + mDealValueString);
		dealValueLabel.setLocation(15, 140);
		dealValueLabel.setSize(290, 20);
		getContentPane().add(dealValueLabel);
		
		stageOfNegotiationsLabel = new JLabel("<html>Stage of negotiations:<br><b>" + mStageOfNegotiations);
		stageOfNegotiationsLabel.setLocation(15, 160);
		stageOfNegotiationsLabel.setSize(290, 40);
		getContentPane().add(stageOfNegotiationsLabel);
		
		probabilityLabel = new JLabel("<html>Probability: <b>" + mProbabilityString);
		probabilityLabel.setLocation(15, 195);
		probabilityLabel.setSize(290, 20);
		getContentPane().add(probabilityLabel);
		
		reminderMessageLabel = new JLabel("Reminder message:");
		reminderMessageLabel.setLocation(335, 15);
		reminderMessageLabel.setSize(290, 20);
		getContentPane().add(reminderMessageLabel);
		
		reminderMessageTextArea = new JTextArea();
		//Set the text area to wrap lines at whitespace before the edge of the text area
		reminderMessageTextArea.setLineWrap(true);
		reminderMessageTextArea.setWrapStyleWord(true);
		reminderMessageTextArea.setEditable(true);
		reminderMessageTextArea.setText(mReminderMessage);
		
		reminderMessageScrollPane = new JScrollPane(reminderMessageTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		reminderMessageScrollPane.setLocation(335, 40);
		reminderMessageScrollPane.setSize(290, 125);
		getContentPane().add(reminderMessageScrollPane);
		
		cancelButton = new JButton();
		cancelButton.setLocation(515,180);
		cancelButton.setSize(100,30);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		saveButton = new JButton();
		saveButton.setLocation(400,180);
		saveButton.setSize(100,30);
		saveButton.setText("Save");
		getContentPane().add(saveButton);
		saveButton.addActionListener(this);
		
		//Set the window's properties
		setTitle("Edit action reminder");
		setResizable(false);
		setSize(640,255);
		setVisible(true);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Cancel' has been pressed...
		if(e.getSource() == cancelButton)
		{
			//Close the window
			this.dispose();
		}
		//If 'Save' is pressed...
		else if(e.getSource() == saveButton)
		{
			//If editing is successful...
			if(mBackendManager.editActionReminder(reminderDayComboBox.getSelectedItem(), reminderMonthComboBox.getSelectedItem(), reminderYearComboBox.getSelectedItem(), reminderMessageTextArea.getText(), mReminderIndex))
				dispose();
		}
	}
}
package gui.actionreminders;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class ViewActionReminderFrame extends JDialog
{
	JLabel reminderDateLabel;
	JLabel clientNameLabel;
	JLabel dealDateLabel;
	JLabel stageOfNegotiationsLabel;
	JLabel probabilityLabel;
	JLabel dealValueLabel;
	JLabel reminderMessageLabel;
	JTextArea reminderMessageTextArea;
	JScrollPane reminderMessageScrollPane;
	
	final String mReminderDateString;
	final String mClientName;
	final String mDealDateString;
	final String mStageOfNegotiations;
	final String mProbabilityString;
	final String mDealValueString;
	final String mReminderMessage;
	final BackendManager mBackendManager;
	
	public ViewActionReminderFrame(BackendManager backendManager, String reminderDateString, String clientName, String dealDateString, String stageOfNegotiations, String probabilityString, String dealValueString, String reminderMessage)
	{
		mReminderDateString = reminderDateString;
		mClientName = clientName;
		mDealDateString = dealDateString;
		mStageOfNegotiations = stageOfNegotiations;
		mProbabilityString = probabilityString;
		mDealValueString = dealValueString;
		mReminderMessage = reminderMessage;
		mBackendManager = backendManager;
		
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
		reminderDateLabel = new JLabel("<html>Reminder date: <b>" + mReminderDateString);
		reminderDateLabel.setLocation(15, 15);
		reminderDateLabel.setSize(290, 20);
		getContentPane().add(reminderDateLabel);
		
		clientNameLabel = new JLabel("<html>Client name: <b>" + mClientName);
		clientNameLabel.setLocation(15, 50);
		clientNameLabel.setSize(290, 20);
		getContentPane().add(clientNameLabel);
		
		dealDateLabel = new JLabel("<html>Deal date: <b>" + mDealDateString);
		dealDateLabel.setLocation(15, 75);
		dealDateLabel.setSize(290, 20);
		getContentPane().add(dealDateLabel);
		
		dealValueLabel = new JLabel("<html>Deal value: <b>" + mDealValueString);
		dealValueLabel.setLocation(15, 100);
		dealValueLabel.setSize(290, 20);
		getContentPane().add(dealValueLabel);
		
		stageOfNegotiationsLabel = new JLabel("<html>Stage of negotiations:<br><b>" + mStageOfNegotiations);
		stageOfNegotiationsLabel.setLocation(15, 125);
		stageOfNegotiationsLabel.setSize(290, 40);
		getContentPane().add(stageOfNegotiationsLabel);
		
		probabilityLabel = new JLabel("<html>Probability: <b>" + mProbabilityString);
		probabilityLabel.setLocation(15, 175);
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
		reminderMessageTextArea.setEditable(false);
		reminderMessageTextArea.setText(mReminderMessage);
		
		reminderMessageScrollPane = new JScrollPane(reminderMessageTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		reminderMessageScrollPane.setLocation(335, 40);
		reminderMessageScrollPane.setSize(290, 170);
		getContentPane().add(reminderMessageScrollPane);
		
		//Set the window's properties
		setTitle("View action reminder");
		setResizable(false);
		setSize(640,255);
		setVisible(true);
	}
}
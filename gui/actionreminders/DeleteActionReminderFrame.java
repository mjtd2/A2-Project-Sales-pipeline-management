package gui.actionreminders;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class DeleteActionReminderFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton deleteButton;
	JLabel reminderDateLabel;
	JLabel clientNameLabel;
	JLabel dealDateLabel;
	JLabel stageOfNegotiationsLabel;
	JLabel probabilityLabel;
	JLabel dealValueLabel;
	JLabel reminderMessageLabel;
	JTextArea reminderMessageTextArea;
	JScrollPane reminderMessageScrollPane;
	JLabel areYouSureLabel;
	
	final String mReminderDateString;
	final String mClientName;
	final String mDealDateString;
	final String mStageOfNegotiations;
	final String mProbabilityString;
	final String mDealValueString;
	final String mReminderMessage;
	final BackendManager mBackendManager;
	final int mActionReminderIndex;
	
	public DeleteActionReminderFrame(BackendManager backendManager, String reminderDateString, String clientName, String dealDateString, String stageOfNegotiations, String probabilityString, String dealValueString, String reminderMessage, int actionReminderIndex)
	{
		mReminderDateString = reminderDateString;
		mClientName = clientName;
		mDealDateString = dealDateString;
		mStageOfNegotiations = stageOfNegotiations;
		mProbabilityString = probabilityString;
		mDealValueString = dealValueString;
		mReminderMessage = reminderMessage;
		mBackendManager = backendManager;
		mActionReminderIndex = actionReminderIndex;
		
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
		reminderMessageScrollPane.setSize(290, 90);
		getContentPane().add(reminderMessageScrollPane);
		
		cancelButton = new JButton();
		cancelButton.setLocation(488,180);
		cancelButton.setSize(100,30);
		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		deleteButton = new JButton();
		deleteButton.setLocation(372,180);
		deleteButton.setSize(100,30);
		deleteButton.setText("Delete");
		getContentPane().add(deleteButton);
		deleteButton.addActionListener(this);
		
		areYouSureLabel = new JLabel("<html><p style=\"text-align:center;\">Are you sure you want to delete this<br>action reminder?", SwingConstants.CENTER);
		areYouSureLabel.setLocation(335, 140);
		areYouSureLabel.setSize(290, 35);
		getContentPane().add(areYouSureLabel);
		
		//Set the window's properties
		setTitle("Delete action reminder");
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
			dispose();
		}
		//If 'Delete' is pressed...
		else if(e.getSource() == deleteButton)
		{
			mBackendManager.deleteActionReminder(mActionReminderIndex);
			dispose();
		}
	}
}
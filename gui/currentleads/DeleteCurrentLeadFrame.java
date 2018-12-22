package gui.currentleads;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class DeleteCurrentLeadFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton deleteButton;
	JLabel clientNameLabel;
	JLabel dealDateLabel;
	JLabel stageOfNegotiationsLabel;
	JLabel probabilityLabel;
	JLabel dealValueLabel;
	JLabel areYouSureLabel;
	JLabel willLeadBeSuccessfulLabel;
	JCheckBox sendToHistoricalCheckbox;
	
	final String mClientName;
	final String mDealDateString;
	final String mStageOfNegotiations;
	final String mProbabilityString;
	final String mDealValueString;
	final BackendManager mBackendManager;
	final int mLeadIndex;
	final boolean mLeadWillBeSuccessful;
	final boolean mSendToHistorical;
	
	public DeleteCurrentLeadFrame(BackendManager backendManager, String clientName, String dealDateString, String stageOfNegotiations, String probabilityString, String dealValueString, int leadIndex, boolean leadWillBeSuccessful, boolean sendToHistorical)
	{
		mClientName = clientName;
		mDealDateString = dealDateString;
		mStageOfNegotiations = stageOfNegotiations;
		mProbabilityString = probabilityString;
		mDealValueString = dealValueString;
		mBackendManager = backendManager;
		mLeadIndex = leadIndex;
		mLeadWillBeSuccessful = leadWillBeSuccessful;
		mSendToHistorical = sendToHistorical;
		
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
		
		dealDateLabel = new JLabel("<html>Deal date: <b>" + mDealDateString);
		dealDateLabel.setLocation(15, 40);
		dealDateLabel.setSize(290, 20);
		getContentPane().add(dealDateLabel);
		
		dealValueLabel = new JLabel("<html>Deal value: <b>" + mDealValueString);
		dealValueLabel.setLocation(15, 65);
		dealValueLabel.setSize(290, 20);
		getContentPane().add(dealValueLabel);
		
		stageOfNegotiationsLabel = new JLabel("<html>Stage of negotiations:<br><b>" + mStageOfNegotiations);
		stageOfNegotiationsLabel.setLocation(15, 85);
		stageOfNegotiationsLabel.setSize(290, 40);
		getContentPane().add(stageOfNegotiationsLabel);
		
		probabilityLabel = new JLabel("<html>Probability: <b>" + mProbabilityString);
		probabilityLabel.setLocation(15, 120);
		probabilityLabel.setSize(290, 20);
		getContentPane().add(probabilityLabel);
		
		areYouSureLabel = new JLabel("<html><p style=\"text-align:center;\">Are you sure you want to delete this lead<br> (and associated action reminders)?", SwingConstants.CENTER);
		areYouSureLabel.setLocation(335, 15);
		areYouSureLabel.setSize(290, 35);
		getContentPane().add(areYouSureLabel);
		
		sendToHistoricalCheckbox = new JCheckBox("Send to historical");
		sendToHistoricalCheckbox.setLocation(420, 50);
		sendToHistoricalCheckbox.setSize(150,20);
		sendToHistoricalCheckbox.setSelected(mSendToHistorical);
		getContentPane().add(sendToHistoricalCheckbox);
		
		willLeadBeSuccessfulLabel = new JLabel("<html><p style=\"text-align:center;\">(If moved, this lead will be<br> considered <b>" + (mLeadWillBeSuccessful ? "successful" : "unsuccessful") + "</b>.)", SwingConstants.CENTER);
		willLeadBeSuccessfulLabel.setLocation(335, 70);
		willLeadBeSuccessfulLabel.setSize(290, 35);
		getContentPane().add(willLeadBeSuccessfulLabel);
		
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
		setTitle("Delete lead");
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
			mBackendManager.deleteCurrentLead(mLeadIndex, sendToHistoricalCheckbox.isSelected(), mLeadWillBeSuccessful);
			dispose();
		}
	}
}
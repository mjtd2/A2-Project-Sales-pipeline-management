package gui.historicaldata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class DeleteHistoricalLeadFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton deleteButton;
	JLabel clientNameLabel;
	JLabel dealDateLabel;
	JLabel stageOfNegotiationsLabel;
	JLabel succeededLabel;
	JLabel dealValueLabel;
	JLabel areYouSureLabel;
	JLabel willLeadBeSuccessfulLabel;
	JCheckBox deleteClientCheckbox;
	
	final String mClientName;
	final String mDealDateString;
	final String mDealValueString;
	final String mStageOfNegotiations;
	final BackendManager mBackendManager;
	final int mLeadID;
	final boolean mSucceeded;
	final boolean mAllowDeletingClient;
	
	public DeleteHistoricalLeadFrame(BackendManager backendManager, String clientName, String dealDateString, String dealValueString, String stageOfNegotiations, int leadID, boolean succeeded, boolean allowDeletingClient)
	{
		mClientName = clientName;
		mDealDateString = dealDateString;
		mDealValueString = dealValueString;
		mStageOfNegotiations = stageOfNegotiations;
		mBackendManager = backendManager;
		mLeadID = leadID;
		mSucceeded = succeeded;
		mAllowDeletingClient = allowDeletingClient;
		
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
		
		succeededLabel = new JLabel("<html>Deal: <b>" + (mSucceeded ? "Succeeded" : "Failed"));
		succeededLabel.setLocation(15, 120);
		succeededLabel.setSize(290, 20);
		getContentPane().add(succeededLabel);
		
		areYouSureLabel = new JLabel("<html><p style=\"text-align:center;\">Are you sure you want to permanently<br>delete this lead?", SwingConstants.CENTER);
		areYouSureLabel.setLocation(335, 15);
		areYouSureLabel.setSize(290, 35);
		getContentPane().add(areYouSureLabel);
		
		deleteClientCheckbox = new JCheckBox("Delete client");
		deleteClientCheckbox.setLocation(420, 55);
		deleteClientCheckbox.setSize(150,20);
		deleteClientCheckbox.setEnabled(mAllowDeletingClient);
		getContentPane().add(deleteClientCheckbox);
		
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
			mBackendManager.deleteHistoricalLead(mLeadID, deleteClientCheckbox.isSelected());
			dispose();
		}
	}
}
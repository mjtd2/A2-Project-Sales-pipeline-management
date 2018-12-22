package gui.settings;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class DeleteStageFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton deleteButton;
	private JLabel probabilityLabel;
	private JLabel stageNameLabel;
	JLabel moveLeadsLabel;
	JLabel assumeSuccessfulLabel;
	
	final int mProbability;
	final String mStageName;
	final boolean mMoveLeads;
	final boolean mAssumeSuccessful;
	final int mStageIndex;
	
	private final BackendManager backendManager;
	
	public DeleteStageFrame(BackendManager backendMgr, int probability, String stageName, boolean moveLeads, boolean assumeSuccessful, int stageIndex)
	{
		backendManager = backendMgr;
		mProbability = probability;
		mStageName = stageName;
		mMoveLeads = moveLeads;
		mAssumeSuccessful = assumeSuccessful;
		mStageIndex = stageIndex;
		
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
		cancelButton.setLocation(470,110);
		cancelButton.setSize(100,30);
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		deleteButton = new JButton("Delete");
		deleteButton.setLocation(355,110);
		deleteButton.setSize(100,30);
		getContentPane().add(deleteButton);
		deleteButton.addActionListener(this);
		
		stageNameLabel = new JLabel("<html>Stage of negotiation:<br><b>" + mStageName);
		stageNameLabel.setLocation(15,15);
		stageNameLabel.setSize(260,35);
		getContentPane().add(stageNameLabel);
		
		probabilityLabel = new JLabel("<html>Default probability: <b>" + mProbability + "%");
		probabilityLabel.setLocation(15,65);
		probabilityLabel.setSize(260,20);
		getContentPane().add(probabilityLabel);
		
		moveLeadsLabel = new JLabel("<html><b>" + (mMoveLeads ? "S" : "Don't s") + "end</b> leads to historical");
		moveLeadsLabel.setLocation(15,90);
		moveLeadsLabel.setSize(260,20);
		getContentPane().add(moveLeadsLabel);
		
		assumeSuccessfulLabel = new JLabel("<html>Assuming <b>" + (mAssumeSuccessful ? "successful" : "unsuccessful") + "</b> if sent");
		assumeSuccessfulLabel.setLocation(15,115);
		assumeSuccessfulLabel.setSize(260,20);
		getContentPane().add(assumeSuccessfulLabel);
		
		JLabel areYouSureLabel = new JLabel("<html><p style=\"text-align:center;\">Are you sure you want to delete this<br>stage of negotiation?", SwingConstants.CENTER);
		areYouSureLabel.setLocation(350, 35);
		areYouSureLabel.setSize(225, 35);
		getContentPane().add(areYouSureLabel);
		
		//Set the window's properties
		setTitle("Delete stage of negotiation");
		setResizable(false);
		setSize(585,185);
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
			backendManager.deleteStageOfNegotiation(mStageIndex);
			dispose();
		}
	}
}
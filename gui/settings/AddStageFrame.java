package gui.settings;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class AddStageFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton saveButton;
	private JLabel probabilityLabel;
	private JLabel stageNameLabel;
	JTextField stageNameTextBox;
	JSpinner probabilitySpinner;
	JCheckBox moveLeadsCheckbox;
	JCheckBox assumeSuccessfulCheckbox;
	
	private final BackendManager backendManager;
	
	public AddStageFrame(BackendManager backendMgr)
	{
		backendManager = backendMgr;
		
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
		
		saveButton = new JButton("Save");
		saveButton.setLocation(355,110);
		saveButton.setSize(100,30);
		getContentPane().add(saveButton);
		saveButton.addActionListener(this);
		
		stageNameLabel = new JLabel("Stage of negotiation");
		stageNameLabel.setLocation(15,15);
		stageNameLabel.setSize(100,20);
		getContentPane().add(stageNameLabel);
		
		stageNameTextBox = new JTextField();
		stageNameTextBox.setLocation(15,40);
		stageNameTextBox.setSize(420,30);
		getContentPane().add(stageNameTextBox);
		
		probabilityLabel = new JLabel("Default probability");
		probabilityLabel.setLocation(15,85);
		probabilityLabel.setSize(100,20);
		getContentPane().add(probabilityLabel);
		
		//0 to 100
		probabilitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		probabilitySpinner.setLocation(15,110);
		probabilitySpinner.setSize(50,30);
		getContentPane().add(probabilitySpinner);
		
		JLabel probabilityPercentageSymbolLabel = new JLabel("%");
		probabilityPercentageSymbolLabel.setLocation(70,115);
		probabilityPercentageSymbolLabel.setSize(15,20);
		getContentPane().add(probabilityPercentageSymbolLabel);
		
		moveLeadsCheckbox = new JCheckBox("Send leads to historical");
		moveLeadsCheckbox.setLocation(130,85);
		moveLeadsCheckbox.setSize(200,20);
		getContentPane().add(moveLeadsCheckbox);
		
		assumeSuccessfulCheckbox = new JCheckBox("Assume successful if sent");
		assumeSuccessfulCheckbox.setLocation(130,115);
		assumeSuccessfulCheckbox.setSize(200,20);
		getContentPane().add(assumeSuccessfulCheckbox);
		
		//Set the window's properties
		setTitle("Add stage of negotiation");
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
		else if(e.getSource() == saveButton)
		{
			//If adding the stage is successful...
			if(backendManager.addStageOfNegotiation(stageNameTextBox.getText(), probabilitySpinner.getValue(), moveLeadsCheckbox.isSelected(), assumeSuccessfulCheckbox.isSelected()))
				dispose();
		}
	}
}  
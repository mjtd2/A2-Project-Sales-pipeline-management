package gui.clients;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.BackendManager;

public class AddClientFrame extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton saveButton;
	private JLabel emailAddressLabel;
	private JLabel clientNameLabel;
	private JLabel phoneNumberLabel;
	private JTextField emailAddressTextBox;
	JTextField clientNameTextBox;
	JTextField phoneNumberTextBox;
	
	private final BackendManager backendManager;
	
	public AddClientFrame(BackendManager backendMgr)
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
		cancelButton.setLocation(770,110);
		cancelButton.setSize(100,30);
		getContentPane().add(cancelButton);
		cancelButton.addActionListener(this);
		
		saveButton = new JButton("Save");
		saveButton.setLocation(655,110);
		saveButton.setSize(100,30);
		getContentPane().add(saveButton);
		saveButton.addActionListener(this);
		
		clientNameLabel = new JLabel("Client name");
		clientNameLabel.setLocation(15,15);
		clientNameLabel.setSize(70,20);
		getContentPane().add(clientNameLabel);
		
		clientNameTextBox = new JTextField();
		clientNameTextBox.setLocation(15,40);
		clientNameTextBox.setSize(420,30);
		getContentPane().add(clientNameTextBox);
		
		phoneNumberLabel = new JLabel("Phone number");
		phoneNumberLabel.setLocation(450,15);
		phoneNumberLabel.setSize(70,20);
		getContentPane().add(phoneNumberLabel);
		
		phoneNumberTextBox = new JTextField();
		phoneNumberTextBox.setLocation(450,40);
		phoneNumberTextBox.setSize(420,30);
		getContentPane().add(phoneNumberTextBox);
		
		emailAddressLabel = new JLabel("Email address");
		emailAddressLabel.setLocation(15,85);
		emailAddressLabel.setSize(65,20);
		getContentPane().add(emailAddressLabel);
		
		emailAddressTextBox = new JTextField();
		emailAddressTextBox.setLocation(15,110);
		emailAddressTextBox.setSize(420,30);
		getContentPane().add(emailAddressTextBox);
		
		//Set the window's properties
		setTitle("Add client");
		setResizable(false);
		setSize(885,185);
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
			//If adding the client is successful...
			if(backendManager.addClient(clientNameTextBox.getText(), emailAddressTextBox.getText(), phoneNumberTextBox.getText()))
				dispose();
		}
	}
}  
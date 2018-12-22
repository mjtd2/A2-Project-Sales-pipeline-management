package gui;

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import javax.swing.*;

import gui.currentleads.*;
import gui.actionreminders.*;
import gui.clients.*;
import gui.historicaldata.*;
import gui.graphs.*;
import gui.settings.*;
import backend.BackendListener;
import backend.BackendManager;

//Declare the GUI class
public class GUI extends JFrame implements BackendListener
{
	JTabbedPane tabbedPane = new JTabbedPane();
	GraphsTab graphsTab;
	BackendManager mBackendManager;
	
	public GUI(BackendManager backendManager)
	{
		mBackendManager = backendManager;
		//Make the frame listen to the BackendManager (i.e. when a lead is saved) 
		backendManager.addListener(this);
		addWindowListener(backendManager);
		//Make the program's processes close when the window is closed
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		graphsTab = new GraphsTab(backendManager);
		
		//Add the main frame's JTabs to its JTabbedPane
		tabbedPane.addTab("Current leads", new CurrentLeadsTab(backendManager));
		tabbedPane.addTab("Graphs", graphsTab);
		tabbedPane.addTab("Action reminders", new ActionRemindersTab(backendManager));
		tabbedPane.addTab("Client Details", new ClientsTab(backendManager));
		tabbedPane.addTab("Historical Data", new HistoricalDataTab(backendManager));
		tabbedPane.addTab("Settings", new SettingsTab(backendManager));
		
		//Let the user switch tabs using <Alt> + [numbers 1-6]
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		tabbedPane.setMnemonicAt(4, KeyEvent.VK_5);
		tabbedPane.setMnemonicAt(5, KeyEvent.VK_6);
		
		//Add content to the window.
        this.add(tabbedPane, BorderLayout.CENTER);
        
        //Display the window.
		
		//Set the window's properties
		this.setTitle("Sales Pipeline Management");
		//Set the width to half the screen's width
		this.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2, 500);//This line makes the window 16px too thin on Windows 10.
		this.setVisible(true);
		this.setResizable(true);
	}
	
	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}
	
	public void makeNewGraphsTab()
	{
		graphsTab = new GraphsTab(mBackendManager);
	}
	
	//If a lead has been saved...
	public void backendUpdated()
	{
		this.revalidate();
		this.repaint();
	}
}
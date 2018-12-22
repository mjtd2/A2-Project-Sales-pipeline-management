package gui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import java.awt.Toolkit;

import backend.BackendListener;
import backend.BackendManager;
import gui.YesNoCellRenderer;
import gui.ProbabilityCellRenderer;

public class SettingsTab extends JPanel implements ActionListener, BackendListener, MouseListener
{
	//Construct a button for adding stages of negotiation
	JButton addStageOfNegotiationButton = new JButton("Add stage of negotiation...");
	JTable stagesOfNegotiationTable;
	JMenuItem editStageOfNegotiationButton;
	JMenuItem deleteStageOfNegotiationButton;
	BackendManager backendManager;
	JCheckBox lockHistoricalDataCheckbox = new JCheckBox("Lock historical data");
	JCheckBox automaticallyDeleteOldActionRemindersCheckbox = new JCheckBox("Automatically delete old action reminders");
	JCheckBox automaticallyDeleteOldLeadsCheckbox = new JCheckBox("Automatically delete/move old leads");
	JCheckBox sendToHistoricalCheckbox = new JCheckBox("Send to historical");
	JCheckBox automaticallyDeleteHistoricalLeadsCheckbox = new JCheckBox("Automatically delete historical leads");
	JRadioButton deleteAllButNewestRadioButton = new JRadioButton("Delete all but newest");
	JRadioButton deleteLeadsOlderThanRadioButton = new JRadioButton("Delete leads older than");
	JTextField numberOfLeadsTextBox;
	JTextField numberOfDaysTextBox = new JTextField();
	JLabel leadsLabel = new JLabel("leads");
	JLabel daysLabel = new JLabel("days");
	JLabel currencyLabel = new JLabel("Currency:");
	String[] currencySymbols = {"\u00a3", "\u20ac", "$"};
	JComboBox currencyComboBox = new JComboBox(currencySymbols);
	JButton saveSettingsButton = new JButton("Save settings");
	
	public SettingsTab(BackendManager backendMgr)
	{
		//Construct the JPanel part of this class, with a BorderLayout
		super(new BorderLayout());
		backendManager = backendMgr;
		backendManager.addListener(this);
		//Place the panel's title in the top left...
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.add(new JLabel("Settings"), BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		titlePanel.add(new JPanel(), BorderLayout.CENTER);
		//...and adding the title panel to the top
		this.add(titlePanel, BorderLayout.NORTH);
		
		JPanel settingsPanel = new JPanel(null);
		settingsPanel.setPreferredSize(new Dimension(265,315));
		
		lockHistoricalDataCheckbox.setSize(200,20);
		lockHistoricalDataCheckbox.setLocation(15,15);
		lockHistoricalDataCheckbox.setSelected(backendManager.getMemory().getSettings().getLockHistoricalData());
		settingsPanel.add(lockHistoricalDataCheckbox);
		
		automaticallyDeleteOldActionRemindersCheckbox.setSize(250,20);
		automaticallyDeleteOldActionRemindersCheckbox.setLocation(15,40);
		automaticallyDeleteOldActionRemindersCheckbox.setSelected(backendManager.getMemory().getSettings().getAutomaticallyDeleteActionReminders());
		settingsPanel.add(automaticallyDeleteOldActionRemindersCheckbox);
		
		automaticallyDeleteOldLeadsCheckbox.setSize(200,20);
		automaticallyDeleteOldLeadsCheckbox.setLocation(15,65);
		automaticallyDeleteOldLeadsCheckbox.setSelected(backendManager.getMemory().getSettings().getAutomaticallyDeleteLeads());
		automaticallyDeleteOldLeadsCheckbox.addActionListener(this);
		settingsPanel.add(automaticallyDeleteOldLeadsCheckbox);
		
		sendToHistoricalCheckbox.setSize(200,20);
		sendToHistoricalCheckbox.setLocation(30,90);
		sendToHistoricalCheckbox.setSelected(backendManager.getMemory().getSettings().getSendLeadsToHistorical());
		sendToHistoricalCheckbox.setEnabled(automaticallyDeleteOldLeadsCheckbox.isSelected());
		settingsPanel.add(sendToHistoricalCheckbox);
		
		automaticallyDeleteHistoricalLeadsCheckbox.setSize(200,20);
		automaticallyDeleteHistoricalLeadsCheckbox.setLocation(15,140);
		automaticallyDeleteHistoricalLeadsCheckbox.setSelected(backendManager.getMemory().getSettings().getAutomaticallyDeleteHistoricalLeads());
		automaticallyDeleteHistoricalLeadsCheckbox.addActionListener(this);
		settingsPanel.add(automaticallyDeleteHistoricalLeadsCheckbox);
		
		deleteAllButNewestRadioButton.setSize(138,20);
		deleteAllButNewestRadioButton.setLocation(30,165);
		deleteAllButNewestRadioButton.setSelected(backendManager.getMemory().getSettings().getDeleteAllButNewestNHistoricalLeads());
		deleteAllButNewestRadioButton.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		settingsPanel.add(deleteAllButNewestRadioButton);
		
		deleteLeadsOlderThanRadioButton.setSize(138,20);
		deleteLeadsOlderThanRadioButton.setLocation(30,200);
		deleteLeadsOlderThanRadioButton.setSelected(!backendManager.getMemory().getSettings().getDeleteAllButNewestNHistoricalLeads());
		deleteLeadsOlderThanRadioButton.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		settingsPanel.add(deleteLeadsOlderThanRadioButton);
		
		ButtonGroup group = new ButtonGroup();
		group.add(deleteAllButNewestRadioButton);
		group.add(deleteLeadsOlderThanRadioButton);
		
		numberOfLeadsTextBox = new JTextField(Integer.toString(backendManager.getMemory().getSettings().getNumberOfNewestHistoricalLeadsToKeep()));
		numberOfLeadsTextBox.setSize(40,30);
		numberOfLeadsTextBox.setLocation(170,160);
		numberOfLeadsTextBox.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		settingsPanel.add(numberOfLeadsTextBox);
		
		numberOfDaysTextBox = new JTextField(Integer.toString(backendManager.getMemory().getSettings().getMaximumAgeOfHistoricalLeadsInDays()));
		numberOfDaysTextBox.setSize(40,30);
		numberOfDaysTextBox.setLocation(170,195);
		numberOfDaysTextBox.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		settingsPanel.add(numberOfDaysTextBox);
		
		leadsLabel.setSize(50,20);
		leadsLabel.setLocation(215,165);
		leadsLabel.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		settingsPanel.add(leadsLabel);
		
		daysLabel.setSize(50,20);
		daysLabel.setLocation(215,200);
		daysLabel.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		settingsPanel.add(daysLabel);
		
		currencyLabel.setSize(50,20);
		currencyLabel.setLocation(15,240);
		settingsPanel.add(currencyLabel);
		
		currencyComboBox.setSize(50,30);
		currencyComboBox.setLocation(70,235);
		currencyComboBox.setSelectedItem(backendManager.getMemory().getSettings().getCurrencySymbol());
		settingsPanel.add(currencyComboBox);
		
		saveSettingsButton.setSize(100,30);
		saveSettingsButton.setLocation(150,235);
		saveSettingsButton.addActionListener(this);
		settingsPanel.add(saveSettingsButton);
		
		JLabel changesLabel = new JLabel("<html><p style=\"text-align:center;\"><i>Changes to settings may only take effect<br>once the program has been restarted.", SwingConstants.CENTER);
		changesLabel.setSize(235,35);
		changesLabel.setLocation(15,275);
		settingsPanel.add(changesLabel);
		
		add(new JScrollPane(settingsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.WEST);
		
		TableModel tableModel = new StagesOfNegotiationTableModel(backendManager);
		stagesOfNegotiationTable = new JTable(tableModel);
		stagesOfNegotiationTable.setRowHeight(25);
		int widthOfLongColumns = Math.max(120, (int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 610)));
		stagesOfNegotiationTable.getColumnModel().getColumn(0).setPreferredWidth(widthOfLongColumns);
		stagesOfNegotiationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		stagesOfNegotiationTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		stagesOfNegotiationTable.getColumnModel().getColumn(3).setPreferredWidth(145);
		//Render Booleans as "Yes" or "No"
		stagesOfNegotiationTable.setDefaultRenderer(Boolean.class, new YesNoCellRenderer());
		//Render probabilities so they end with "%"
		stagesOfNegotiationTable.getColumnModel().getColumn(1).setCellRenderer(new ProbabilityCellRenderer());
		stagesOfNegotiationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		stagesOfNegotiationTable.addMouseListener(this);
		stagesOfNegotiationTable.setAutoCreateRowSorter(true);
		JPanel stagesOfNegotiationPanel = new JPanel(new BorderLayout());
		stagesOfNegotiationPanel.add(new JScrollPane(stagesOfNegotiationTable), BorderLayout.CENTER);
		
		//Place the stages of negotiation panel's title in its top left...
		JPanel stagesOfNegotiationTitlePanel = new JPanel(new BorderLayout());
		stagesOfNegotiationTitlePanel.add(new JLabel("Stages of negotiation"), BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		stagesOfNegotiationTitlePanel.add(new JPanel(), BorderLayout.CENTER);
		//...and adding the title panel to the top
		stagesOfNegotiationPanel.add(stagesOfNegotiationTitlePanel, BorderLayout.NORTH);
		
		//Set the 'Add stage of negotiation...' button size
		addStageOfNegotiationButton.setPreferredSize(new Dimension(190, 50));
		//Place the 'Add stage of negotiation...' button in the bottom left...
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(addStageOfNegotiationButton, BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		buttonPanel.add(new JPanel(), BorderLayout.CENTER);
		//...and adding the button panel to the bottom
		stagesOfNegotiationPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		add(stagesOfNegotiationPanel, BorderLayout.CENTER);
		//Register the current stages of negotiation tab as interested in the button
		addStageOfNegotiationButton.addActionListener(this);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Add stage of negotiation...' has been pressed...
		if(e.getSource() == addStageOfNegotiationButton)
			backendManager.createAddStageFrame();
		//If View client is pressed...
		else if(e.getSource() == editStageOfNegotiationButton)
			backendManager.createEditStageFrame(stagesOfNegotiationTable.convertRowIndexToModel(stagesOfNegotiationTable.getSelectedRow()));
		//If View action reminders is pressed...
		else if(e.getSource() == deleteStageOfNegotiationButton)
			backendManager.createDeleteStageFrame(stagesOfNegotiationTable.convertRowIndexToModel(stagesOfNegotiationTable.getSelectedRow()));
		//If "automatically delete old leads" is toggled...
		else if(e.getSource() == automaticallyDeleteOldLeadsCheckbox)
			sendToHistoricalCheckbox.setEnabled(automaticallyDeleteOldLeadsCheckbox.isSelected());
		//If "Automatically delete historical leads" is toggled, enable/disable relevant settings
		else if(e.getSource() == automaticallyDeleteHistoricalLeadsCheckbox)
		{
			deleteAllButNewestRadioButton.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
			deleteLeadsOlderThanRadioButton.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
			numberOfLeadsTextBox.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
			numberOfDaysTextBox.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
			leadsLabel.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
			daysLabel.setEnabled(automaticallyDeleteHistoricalLeadsCheckbox.isSelected());
		}
		else if(e.getSource() == saveSettingsButton)
			backendManager.saveSettings(lockHistoricalDataCheckbox.isSelected(), automaticallyDeleteOldActionRemindersCheckbox.isSelected(), automaticallyDeleteOldLeadsCheckbox.isSelected(), sendToHistoricalCheckbox.isSelected(), automaticallyDeleteHistoricalLeadsCheckbox.isSelected(), deleteAllButNewestRadioButton.isSelected(), numberOfLeadsTextBox.getText(), numberOfDaysTextBox.getText(), (String) currencyComboBox.getSelectedItem());
	}
	
	//If a stage of negotiation is saved...
	public void backendUpdated()
	{
		stagesOfNegotiationTable.revalidate();
		stagesOfNegotiationTable.repaint();
	}
	
	//If the mouse has been released...
	public void mouseReleased(MouseEvent e)
	{
		//If it was a right-click above the table...
		if(SwingUtilities.isRightMouseButton(e) && e.getSource() == stagesOfNegotiationTable)
		{
			//Select the row which was clicked on
			stagesOfNegotiationTable.setRowSelectionInterval(stagesOfNegotiationTable.rowAtPoint(e.getPoint()), stagesOfNegotiationTable.rowAtPoint(e.getPoint()));
			//Prepare a popup menu:
			JPopupMenu rightClickMenu = new JPopupMenu();
			
			//Set the text for the menu's options
			editStageOfNegotiationButton = new JMenuItem("Edit stage of negotiation");
			deleteStageOfNegotiationButton = new JMenuItem("Delete stage of negotiation");
			
			//Make the Current Leads tab listen for when the options are pressed
			editStageOfNegotiationButton.addActionListener(this);
			deleteStageOfNegotiationButton.addActionListener(this);
			
			//Disable deleting stages of negotiation with leads
			if(backendManager.stageHasLeads(stagesOfNegotiationTable.convertRowIndexToModel(stagesOfNegotiationTable.getSelectedRow())))
				deleteStageOfNegotiationButton.setEnabled(false);
			
			//Add the options to the menu
			rightClickMenu.add(editStageOfNegotiationButton);
			rightClickMenu.add(deleteStageOfNegotiationButton);
			
			//Show the menu at the point clicked
			rightClickMenu.show(stagesOfNegotiationTable, e.getX(), e.getY());
		}
	}
	
	public JTable getTable()
	{
		return stagesOfNegotiationTable;
	}
	
	//Reducdant methods due to implementing MouseListener
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
}
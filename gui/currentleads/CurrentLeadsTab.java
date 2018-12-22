package gui.currentleads;

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
import gui.DateCellRenderer;
import gui.ValueCellRenderer;
import gui.ProbabilityCellRenderer;

public class CurrentLeadsTab extends JPanel implements ActionListener, BackendListener, MouseListener
{
	//Construct a button for adding leads
	JButton addLeadButton = new JButton("Add lead...");
	JTextField searchBox;
	JButton quickSearchButton;
	JButton advancedSearchButton;
	JButton unfilterButton;
	JTable currentLeadsTable;
	JMenuItem viewClientButton;
	JMenuItem viewActionRemindersButton;
	JMenuItem addActionReminderButton;
	JMenuItem editLeadButton;
	JMenuItem deleteLeadButton;
	BackendManager backendManager;
	
	public CurrentLeadsTab(BackendManager backendMgr)
	{
		//Construct the JPanel part of this class, with a BorderLayout
		super(new BorderLayout());
		backendManager = backendMgr;
		backendManager.addListener(this);
		//Place the panel's title in the top left...
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Current leads");
		titlePanel.add(titleLabel, BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		titlePanel.add(new JPanel(), BorderLayout.CENTER);
		//...and placing the search tools to the right of the dummy JPanel...
		JPanel searchTools = new JPanel();
		searchBox = new JTextField();
		searchBox.setPreferredSize(new Dimension(200, 25));
		searchTools.add(searchBox);
		
		//Add quick search button
		ImageIcon searchIcon = new ImageIcon("Resources/Search.png");
		quickSearchButton = new JButton(searchIcon);
		quickSearchButton.addActionListener(this);
		searchTools.add(quickSearchButton);
		
		//Add advanced search button
		advancedSearchButton = new JButton("Advanced search...");
		advancedSearchButton.addActionListener(this);
		searchTools.add(advancedSearchButton);
		
		//Add unfilter button
		ImageIcon filterIcon = new ImageIcon("Resources/Filter.png");
		unfilterButton = new JButton(filterIcon);
		unfilterButton.setToolTipText("Undo searches and filters");
		unfilterButton.addActionListener(this);
		searchTools.add(unfilterButton);
		
		titlePanel.add(searchTools, BorderLayout.EAST);
		//...and adding the title panel to the top
		this.add(titlePanel, BorderLayout.NORTH);
		
		TableModel tableModel = new CurrentLeadsTableModel(backendManager);
		currentLeadsTable = new JTable(tableModel);
		currentLeadsTable.setRowHeight(25);
		int widthOfLongColumns = Math.max(120, (int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 300) / 2));
		currentLeadsTable.getColumnModel().getColumn(0).setPreferredWidth(widthOfLongColumns);
		currentLeadsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		currentLeadsTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		currentLeadsTable.getColumnModel().getColumn(3).setPreferredWidth(widthOfLongColumns);
		currentLeadsTable.getColumnModel().getColumn(4).setPreferredWidth(70);
		currentLeadsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
		//Render dates as DD/MM/YYYY
		currentLeadsTable.setDefaultRenderer(Date.class, new DateCellRenderer());
		//Render values so they start with the currency symbol
		currentLeadsTable.getColumnModel().getColumn(2).setCellRenderer(new ValueCellRenderer(backendManager.getMemory().getSettings().getCurrencySymbol()));
		//Render probabilities so they end with "%"
		currentLeadsTable.getColumnModel().getColumn(4).setCellRenderer(new ProbabilityCellRenderer());
		currentLeadsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		currentLeadsTable.addMouseListener(this);
		currentLeadsTable.setAutoCreateRowSorter(true);
		JScrollPane leadsTableScrollPane = new JScrollPane(currentLeadsTable);
		this.add(leadsTableScrollPane, BorderLayout.CENTER);
		
		//Set the 'Add lead...' button size
		addLeadButton.setPreferredSize(new Dimension(150, 50));
		//Place the 'Add lead...' button in the bottom left...
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(addLeadButton, BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		buttonPanel.add(new JPanel(), BorderLayout.CENTER);
		//...and adding the button panel to the bottom
		this.add(buttonPanel, BorderLayout.SOUTH);
		//Register the current leads tab as interested in the button
		addLeadButton.addActionListener(this);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Add lead...' has been pressed...
		if(e.getSource() == addLeadButton)
			backendManager.createAddLeadFrame();
		//If Quick Search has been pressed...
		else if(e.getSource() == quickSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			currentLeadsTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchCurrentLeads(searchBox.getText());
		}
		//If Advanced search is pressed...
		else if(e.getSource() == advancedSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			currentLeadsTable.getRowSorter().setSortKeys(null);
			backendManager.createAdvancedSearchLeadsFrame();
		}
		//If unfilter is pressed...
		else if(e.getSource() == unfilterButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			currentLeadsTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchCurrentLeads("");
		}
		//If View client is pressed...
		else if(e.getSource() == viewClientButton)
			backendManager.viewClientOfCurrentLead(currentLeadsTable.convertRowIndexToModel(currentLeadsTable.getSelectedRow()));
		//If View action reminders is pressed...
		else if(e.getSource() == viewActionRemindersButton)
			backendManager.viewActionReminders(currentLeadsTable.convertRowIndexToModel(currentLeadsTable.getSelectedRow()));
		//If Add action reminder is pressed...
		else if(e.getSource() == addActionReminderButton)
			backendManager.createAddActionReminderFrame(currentLeadsTable.convertRowIndexToModel(currentLeadsTable.getSelectedRow()));
		//If Edit lead is pressed...
		else if(e.getSource() == editLeadButton)
			backendManager.createEditLeadFrame(currentLeadsTable.convertRowIndexToModel(currentLeadsTable.getSelectedRow()));
		//If Delete/move lead is pressed...
		else if(e.getSource() == deleteLeadButton)
			backendManager.createDeleteCurrentLeadFrame(currentLeadsTable.convertRowIndexToModel(currentLeadsTable.getSelectedRow()), backendManager.getMemory().getSettings().getSendLeadsToHistorical());
	}
	
	//If a lead is saved...
	public void backendUpdated()
	{
		currentLeadsTable.revalidate();
		currentLeadsTable.repaint();
	}
	
	//If the mouse has been released...
	public void mouseReleased(MouseEvent e)
	{
		//If it was a right-click above the table...
		if(SwingUtilities.isRightMouseButton(e) && e.getSource() == currentLeadsTable)
		{
			//Select the row which was clicked on
			currentLeadsTable.setRowSelectionInterval(currentLeadsTable.rowAtPoint(e.getPoint()), currentLeadsTable.rowAtPoint(e.getPoint()));
			//Prepare a popup menu:
			JPopupMenu rightClickMenu = new JPopupMenu();
			
			//Set the text for the menu's options
			viewClientButton = new JMenuItem("View client");
			viewActionRemindersButton = new JMenuItem("View action reminders");
			addActionReminderButton = new JMenuItem("Add action reminder");
			editLeadButton = new JMenuItem("Edit lead");
			deleteLeadButton = new JMenuItem("Delete/move lead");
			
			//Make the Current Leads tab listen for when the options are pressed
			viewClientButton.addActionListener(this);
			viewActionRemindersButton.addActionListener(this);
			addActionReminderButton.addActionListener(this);
			editLeadButton.addActionListener(this);
			deleteLeadButton.addActionListener(this);
			
			//Add the options to the menu
			rightClickMenu.add(viewClientButton);
			rightClickMenu.add(viewActionRemindersButton);
			rightClickMenu.add(addActionReminderButton);
			rightClickMenu.add(editLeadButton);
			rightClickMenu.add(deleteLeadButton);
			//Show the menu at the point clicked
			rightClickMenu.show(currentLeadsTable, e.getX(), e.getY());
		}
	}
	
	//Reducdant methods due to implementing MouseListener
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	
	public JTable getTable()
	{
		return currentLeadsTable;
	}
}
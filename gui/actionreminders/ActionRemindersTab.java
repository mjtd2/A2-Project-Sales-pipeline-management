package gui.actionreminders;

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

public class ActionRemindersTab extends JPanel implements ActionListener, BackendListener, MouseListener
{
	JTextField searchBox;
	JButton quickSearchButton;
	JButton advancedSearchButton;
	JButton unfilterButton;
	JTable actionRemindersTable;
	JMenuItem viewActionReminderButton;
	JMenuItem viewLeadButton;
	JMenuItem editActionReminderButton;
	JMenuItem deleteActionReminder;
	BackendManager backendManager;
	
	public ActionRemindersTab(BackendManager backendMgr)
	{
		//Construct the JPanel part of this class, with a BorderLayout
		super(new BorderLayout());
		backendManager = backendMgr;
		backendManager.addListener(this);
		//Place the panel's title in the top left...
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Action reminders");
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
		
		TableModel tableModel = new ActionRemindersTableModel(backendManager);
		actionRemindersTable = new JTable(tableModel);
		actionRemindersTable.setRowHeight(25);
		int widthOfLongColumns = Math.max(120, (int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 300) / 2));
		actionRemindersTable.getColumnModel().getColumn(0).setPreferredWidth(70);
		actionRemindersTable.getColumnModel().getColumn(1).setPreferredWidth(widthOfLongColumns);
		actionRemindersTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		actionRemindersTable.getColumnModel().getColumn(3).setPreferredWidth(70);
		actionRemindersTable.getColumnModel().getColumn(4).setPreferredWidth(90);
		actionRemindersTable.getColumnModel().getColumn(5).setPreferredWidth(widthOfLongColumns);
		//Render dates as DD/MM/YYYY
		actionRemindersTable.setDefaultRenderer(Date.class, new DateCellRenderer());
		//Render values so they start with the currency symbol
		actionRemindersTable.getColumnModel().getColumn(3).setCellRenderer(new ValueCellRenderer(backendManager.getMemory().getSettings().getCurrencySymbol()));
		//Render probabilities so 
		actionRemindersTable.getColumnModel().getColumn(4).setCellRenderer(new ProbabilityCellRenderer());
		actionRemindersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		actionRemindersTable.addMouseListener(this);
		actionRemindersTable.setAutoCreateRowSorter(true);
		JScrollPane tableScrollPane = new JScrollPane(actionRemindersTable);
		this.add(tableScrollPane, BorderLayout.CENTER);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If Quick Search has been pressed...
		if(e.getSource() == quickSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			actionRemindersTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchActionReminders(searchBox.getText());
		}
		//If Advanced search is pressed...
		else if(e.getSource() == advancedSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			actionRemindersTable.getRowSorter().setSortKeys(null);
			backendManager.createAdvancedSearchRemindersFrame();
		}
		//If unfilter is pressed...
		else if(e.getSource() == unfilterButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			actionRemindersTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchActionReminders("");
		}
		//If View action reminder is pressed...
		else if(e.getSource() == viewActionReminderButton)
			backendManager.createViewActionReminderFrame(actionRemindersTable.convertRowIndexToModel(actionRemindersTable.getSelectedRow()));
		//If View lead is pressed...
		else if(e.getSource() == viewLeadButton)
			backendManager.viewLead(actionRemindersTable.convertRowIndexToModel(actionRemindersTable.getSelectedRow()));
		//If Edit reminder is pressed...
		else if(e.getSource() == editActionReminderButton)
			backendManager.createEditActionReminderFrame(actionRemindersTable.convertRowIndexToModel(actionRemindersTable.getSelectedRow()));
		//If Delete reminder is pressed...
		else if(e.getSource() == deleteActionReminder)
			backendManager.createDeleteActionReminderFrame(actionRemindersTable.convertRowIndexToModel(actionRemindersTable.getSelectedRow()));
	}
	
	//If a lead is saved...
	public void backendUpdated()
	{
		actionRemindersTable.revalidate();
		actionRemindersTable.repaint();
	}
	
	//If the mouse has been released...
	public void mouseReleased(MouseEvent e)
	{
		//If it was a right-click above the table...
		if(SwingUtilities.isRightMouseButton(e) && e.getSource() == actionRemindersTable)
		{
			//Select the row which was clicked on
			actionRemindersTable.setRowSelectionInterval(actionRemindersTable.rowAtPoint(e.getPoint()), actionRemindersTable.rowAtPoint(e.getPoint()));
			//Prepare a popup menu:
			JPopupMenu rightClickMenu = new JPopupMenu();
			
			//Set the text for the menu's options
			viewActionReminderButton = new JMenuItem("View action reminder");
			viewLeadButton = new JMenuItem("View lead");
			editActionReminderButton = new JMenuItem("Edit action reminder");
			deleteActionReminder = new JMenuItem("Delete action reminder");
			
			//Make the Action reminders tab listen for when the options are pressed
			viewActionReminderButton.addActionListener(this);
			viewLeadButton.addActionListener(this);
			editActionReminderButton.addActionListener(this);
			deleteActionReminder.addActionListener(this);
			
			//Add the options to the menu
			rightClickMenu.add(viewActionReminderButton);
			rightClickMenu.add(viewLeadButton);
			rightClickMenu.add(editActionReminderButton);
			rightClickMenu.add(deleteActionReminder);
			//Show the menu at the point clicked
			rightClickMenu.show(actionRemindersTable, e.getX(), e.getY());
		}
	}
	
	public JTable getTable()
	{
		return actionRemindersTable;
	}
	
	//Reducdant methods due to implementing MouseListener
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
}
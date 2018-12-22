package gui.clients;

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

public class ClientsTab extends JPanel implements ActionListener, BackendListener, MouseListener
{
	//Construct a button for adding leads
	JButton addClientButton = new JButton("Add client...");
	JTextField searchBox;
	JButton quickSearchButton;
	JButton advancedSearchButton;
	JButton unfilterButton;
	JTable clientsTable;
	JMenuItem viewCurrentLeadsButton;
	JMenuItem viewHistoricalLeadsButton;
	JMenuItem editClientButton;
	JMenuItem deleteClientButton;
	BackendManager backendManager;
	
	public ClientsTab(BackendManager backendMgr)
	{
		//Construct the JPanel part of this class, with a BorderLayout
		super(new BorderLayout());
		backendManager = backendMgr;
		backendManager.addListener(this);
		//Place the panel's title in the top left...
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Clients");
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
		
		TableModel tableModel = new ClientsTableModel(backendManager);
		clientsTable = new JTable(tableModel);
		clientsTable.setRowHeight(25);
		int widthOfLongColumns = Math.max(120, (int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 90) / 4));
		clientsTable.getColumnModel().getColumn(0).setPreferredWidth(widthOfLongColumns);
		clientsTable.getColumnModel().getColumn(1).setPreferredWidth(widthOfLongColumns);
		clientsTable.getColumnModel().getColumn(2).setPreferredWidth(widthOfLongColumns);
		clientsTable.getColumnModel().getColumn(3).setPreferredWidth(90);
		clientsTable.getColumnModel().getColumn(4).setPreferredWidth(widthOfLongColumns);
		//Render dates as DD/MM/YYYY
		clientsTable.setDefaultRenderer(Date.class, new DateCellRenderer());
		//Render values so they start with the currency symbol
		clientsTable.getColumnModel().getColumn(4).setCellRenderer(new ValueCellRenderer(backendManager.getMemory().getSettings().getCurrencySymbol()));
		clientsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		clientsTable.addMouseListener(this);
		clientsTable.setAutoCreateRowSorter(true);
		JScrollPane leadsTableScrollPane = new JScrollPane(clientsTable);
		this.add(leadsTableScrollPane, BorderLayout.CENTER);
		
		//Set the 'Add lead...' button size
		addClientButton.setPreferredSize(new Dimension(150, 50));
		//Place the 'Add lead...' button in the bottom left...
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(addClientButton, BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		buttonPanel.add(new JPanel(), BorderLayout.CENTER);
		//...and adding the button panel to the bottom
		this.add(buttonPanel, BorderLayout.SOUTH);
		//Register the current leads tab as interested in the button
		addClientButton.addActionListener(this);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Add client...' has been pressed...
		if(e.getSource() == addClientButton)
			backendManager.createAddClientFrame();
		//If Quick Search has been pressed...
		else if(e.getSource() == quickSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			clientsTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchClients(searchBox.getText());
		}
		//If Advanced search is pressed...
		else if(e.getSource() == advancedSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			clientsTable.getRowSorter().setSortKeys(null);
			backendManager.createAdvancedSearchClientsFrame();
		}
		//If unfilter is pressed...
		else if(e.getSource() == unfilterButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			clientsTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchClients("");
		}
		//If View client is pressed...
		else if(e.getSource() == viewCurrentLeadsButton)
			backendManager.viewCurrentLeads(clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow()));
		else if(e.getSource() == viewHistoricalLeadsButton)
			backendManager.viewHistoricalLeads(clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow()));
		//If Edit client is pressed...
		else if(e.getSource() == editClientButton)
			backendManager.createEditClientFrame(clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow()));
		//If Delete client is pressed...
		else if(e.getSource() == deleteClientButton)
			backendManager.createDeleteClientFrame(clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow()), false);
	}
	
	//If a lead is saved...
	public void backendUpdated()
	{
		clientsTable.revalidate();
		clientsTable.repaint();
	}
	
	//If the mouse has been released...
	public void mouseReleased(MouseEvent e)
	{
		//If it was a right-click above the table...
		if(SwingUtilities.isRightMouseButton(e) && e.getSource() == clientsTable)
		{
			//Select the row which was clicked on
			clientsTable.setRowSelectionInterval(clientsTable.rowAtPoint(e.getPoint()), clientsTable.rowAtPoint(e.getPoint()));
			//Prepare a popup menu:
			JPopupMenu rightClickMenu = new JPopupMenu();
			
			//Set the text for the menu's options
			viewCurrentLeadsButton = new JMenuItem("View current leads");
			viewHistoricalLeadsButton = new JMenuItem("View historical leads");
			editClientButton = new JMenuItem("Edit client");
			deleteClientButton = new JMenuItem("Delete client");
			
			//Make the Current Leads tab listen for when the options are pressed
			viewCurrentLeadsButton.addActionListener(this);
			viewHistoricalLeadsButton.addActionListener(this);
			editClientButton.addActionListener(this);
			deleteClientButton.addActionListener(this);
			
			//Add the options to the menu
			rightClickMenu.add(viewCurrentLeadsButton);
			rightClickMenu.add(viewHistoricalLeadsButton);
			rightClickMenu.add(editClientButton);
			rightClickMenu.add(deleteClientButton);
			
			boolean clientHasLeads = false;
			
			//If the client has no current leads...
			if(backendManager.clientHasNoCurrentLeads(clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow())))
				viewCurrentLeadsButton.setEnabled(false);
			else
				deleteClientButton.setEnabled(false);
			//If the client has no historical leads...
			if(backendManager.clientHasNoHistoricalLeads(clientsTable.convertRowIndexToModel(clientsTable.getSelectedRow())))
				viewHistoricalLeadsButton.setEnabled(false);
			
			//Show the menu at the point clicked
			rightClickMenu.show(clientsTable, e.getX(), e.getY());
		}
	}
	
	//Reducdant methods due to implementing MouseListener
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	
	public JTable getTable()
	{
		return clientsTable;
	}
}
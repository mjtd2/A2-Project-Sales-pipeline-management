package gui.historicaldata;

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
import gui.SuccededCellRenderer;

public class HistoricalDataTab extends JPanel implements ActionListener, BackendListener, MouseListener
{
	//Construct a button for adding leads
	JButton addLeadButton = new JButton("Add lead...");
	JTextField searchBox;
	JButton quickSearchButton;
	JButton advancedSearchButton;
	JButton unfilterButton;
	JTable historicalDataTable;
	JMenuItem viewClientButton;
	JMenuItem editLeadButton;
	JMenuItem deleteLeadButton;
	JMenuItem restoreToCurrentButton;
	BackendManager backendManager;
	
	public HistoricalDataTab(BackendManager backendMgr)
	{
		//Construct the JPanel part of this class, with a BorderLayout
		super(new BorderLayout());
		backendManager = backendMgr;
		backendManager.addListener(this);
		//Place the panel's title in the top left...
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Historical data");
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
		
		TableModel tableModel = new HistoricalDataTableModel(backendManager);
		historicalDataTable = new JTable(tableModel);
		historicalDataTable.setRowHeight(25);
		int widthOfLongColumns = Math.max(120, (int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 250) / 2));
		historicalDataTable.getColumnModel().getColumn(0).setPreferredWidth(widthOfLongColumns);
		historicalDataTable.getColumnModel().getColumn(1).setPreferredWidth(90);
		historicalDataTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		historicalDataTable.getColumnModel().getColumn(3).setPreferredWidth(widthOfLongColumns);
		historicalDataTable.getColumnModel().getColumn(4).setPreferredWidth(90);
		//Render dates as DD/MM/YYYY
		historicalDataTable.setDefaultRenderer(Date.class, new DateCellRenderer());
		//Render booleans as "Succeeded" or "Failed"
		historicalDataTable.setDefaultRenderer(Boolean.class, new SuccededCellRenderer());
		//Render values so they start with the currency symbol
		historicalDataTable.getColumnModel().getColumn(2).setCellRenderer(new ValueCellRenderer(backendManager.getMemory().getSettings().getCurrencySymbol()));
		historicalDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		historicalDataTable.addMouseListener(this);
		historicalDataTable.setAutoCreateRowSorter(true);
		JScrollPane leadsTableScrollPane = new JScrollPane(historicalDataTable);
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
		//Disable the Add lead button if historical data is locked
		if(backendManager.getMemory().getSettings().getLockHistoricalData())
			addLeadButton.setEnabled(false);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Add client...' has been pressed...
		if(e.getSource() == addLeadButton)
			backendManager.createAddHistoricalLeadFrame();
		//If Quick Search has been pressed...
		else if(e.getSource() == quickSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			historicalDataTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchHistoricalLeads(searchBox.getText());
		}
		//If Advanced search is pressed...
		else if(e.getSource() == advancedSearchButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			historicalDataTable.getRowSorter().setSortKeys(null);
			backendManager.createAdvancedSearchHistoricalLeadsFrame();
		}
		//If unfilter is pressed...
		else if(e.getSource() == unfilterButton)
		{
			//Un-sort the table (to make its order consistent with the List class)
			historicalDataTable.getRowSorter().setSortKeys(null);
			backendManager.quickSearchHistoricalLeads("");
		}
		//If View client is pressed...
		else if(e.getSource() == viewClientButton)
			backendManager.viewClientOfHistoricalLead(historicalDataTable.convertRowIndexToModel(historicalDataTable.getSelectedRow()));
		//If Edit client is pressed...
		else if(e.getSource() == editLeadButton)
			backendManager.createEditHistoricalLeadFrame(historicalDataTable.convertRowIndexToModel(historicalDataTable.getSelectedRow()));
		//If Delete client is pressed...
		else if(e.getSource() == deleteLeadButton)
			backendManager.createDeleteHistoricalLeadFrame(historicalDataTable.convertRowIndexToModel(historicalDataTable.getSelectedRow()));
		//If Restore to current is pressed...
		else if(e.getSource() == restoreToCurrentButton)
			backendManager.createRestoreToCurrentFrame(historicalDataTable.convertRowIndexToModel(historicalDataTable.getSelectedRow()));
	}
	
	//If a lead is saved...
	public void backendUpdated()
	{
		historicalDataTable.revalidate();
		historicalDataTable.repaint();
	}
	
	//If the mouse has been released...
	public void mouseReleased(MouseEvent e)
	{
		//If it was a right-click above the table...
		if(SwingUtilities.isRightMouseButton(e) && e.getSource() == historicalDataTable)
		{
			//Select the row which was clicked on
			historicalDataTable.setRowSelectionInterval(historicalDataTable.rowAtPoint(e.getPoint()), historicalDataTable.rowAtPoint(e.getPoint()));
			//Prepare a popup menu:
			JPopupMenu rightClickMenu = new JPopupMenu();
			
			//Set the text for the menu's options
			viewClientButton = new JMenuItem("View client");
			editLeadButton = new JMenuItem("Edit lead");
			deleteLeadButton = new JMenuItem("Delete lead");
			restoreToCurrentButton = new JMenuItem("Restore to current");
			
			//Make the Historical Data tab listen for when the options are pressed
			viewClientButton.addActionListener(this);
			editLeadButton.addActionListener(this);
			deleteLeadButton.addActionListener(this);
			restoreToCurrentButton.addActionListener(this);
			
			//Add the options to the menu
			rightClickMenu.add(viewClientButton);
			rightClickMenu.add(editLeadButton);
			rightClickMenu.add(deleteLeadButton);
			rightClickMenu.add(restoreToCurrentButton);
			
			//Disable the relevant buttons if historical data is locked
			if(backendManager.getMemory().getSettings().getLockHistoricalData())
			{
				editLeadButton.setEnabled(false);
				deleteLeadButton.setEnabled(false);
				restoreToCurrentButton.setEnabled(false);
			}
			
			//Disable "View client" is the lead has no client
			if(historicalDataTable.getModel().getValueAt(historicalDataTable.convertRowIndexToModel(historicalDataTable.rowAtPoint(e.getPoint())), 0).toString().isEmpty())
				viewClientButton.setEnabled(false);
			
			//Show the menu at the point clicked
			rightClickMenu.show(historicalDataTable, e.getX(), e.getY());
		}
	}
	
	//Reducdant methods due to implementing MouseListener
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	
	public JTable getTable()
	{
		return historicalDataTable;
	}
}
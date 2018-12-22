package gui.clients;

import java.util.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import backend.BackendManager;
import backend.Client;

//This class controls how the table can be accessed and interacted with
public class ClientsTableModel extends AbstractTableModel
{
	private BackendManager backendManager;
	
	public ClientsTableModel(BackendManager backendManager)
	{
		this.backendManager = backendManager;
	}
	
	/**
     * Returns the most specific superclass for all the cell values
     * in the column.  This is used by the <code>JTable</code> to set up a
     * default renderer and editor for the column.
     *
     * @param columnIndex  the index of the column
     * @return the common ancestor class of the object values in the model.
     */
	public Class getColumnClass(int columnIndex)
	{
		//If the column is numeric...
		if(columnIndex == 4)
			//Make the table sort by value
			return Integer.class;
		//If the column contains a date...
		if(columnIndex == 3)
			//Make the table sort in date order
			return Date.class;
		//Otherwise sort in alphabetical order
		return new Object().getClass();
	}
	
	/**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
	public int getColumnCount()
	{
		return 5;
	}
	
	/**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param   columnIndex     the index of the column
     * @return  the name of the column
     */
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return "Client name";
			case 1:
				return "Phone number";
			case 2:
				return "Email address";
			case 3:
				return "Next deal date";
			case 4:
				return "Total expected income";
			default:
				throw new IllegalArgumentException("Column Index: " + columnIndex);
		}
	}
	
	/**
     * Returns the number of rows in the table. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     */
	public int getRowCount()
	{
		return backendManager.getMemory().getFilteredClients().size();
	}
	
	public Object getValueAt(int rowTargetIndex, int columnTargetIndex)
	{
		Client client = backendManager.getMemory().getFilteredClients().get(rowTargetIndex);
		
		switch(columnTargetIndex)
		{
			case 0:
				return client.getName();
			case 1:
				return client.getPhoneNumber();
			case 2:
				return client.getEmailAddress();
			case 3:
				if(client.getCurrentLeadIDs().length == 0)
					return null;
				int lowestID = client.getCurrentLeadIDs()[0];
				for(int currentLeadID : client.getCurrentLeadIDs())
				{
					if(currentLeadID < lowestID)
						lowestID = currentLeadID;
				}
				return backendManager.getMemory().getCurrentLeads().get(lowestID).getDealDateAsDate();
			//case 4 will not work if there are duplicates in the array of current lead IDs.
			case 4:
				int total = 0;
				for(int currentLeadID : client.getCurrentLeadIDs())
				{
					total += (int) (backendManager.getMemory().getCurrentLeads().get(currentLeadID).getValue() * (double) backendManager.getMemory().getCurrentLeads().get(currentLeadID).getProbability() / 100 + 0.5);//Rounds for each lead.
				}
				return total;
			default:
				throw new IllegalArgumentException("Column Index: " + columnTargetIndex + System.getProperty("line.separator") + "Row index: " + rowTargetIndex);
		}
	}
}
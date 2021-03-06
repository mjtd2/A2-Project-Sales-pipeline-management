package gui.actionreminders;

import java.util.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import backend.BackendManager;
import backend.CurrentLead;

//This class controls how the table can be accessed and interacted with
public class ActionRemindersTableModel extends AbstractTableModel
{
	private BackendManager backendManager;
	
	public ActionRemindersTableModel(BackendManager backendManager)
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
		if(columnIndex == 3 || columnIndex == 4)
			//Make the table sort by value
			return Integer.class;
		//If the column contains a date...
		if(columnIndex == 0 || columnIndex == 2)
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
		return 6;
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
				return "Action date";
			case 1:
				return "Client name";
			case 2:
				return "Deal date";
			case 3:
				return "Deal value";
			case 4:
				return "Probability";
			case 5:
				return "Reminder message";
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
		return backendManager.getMemory().getFilteredActionReminders().size();
	}
	
	public Object getValueAt(int rowTargetIndex, int columnTargetIndex)
	{
		//Get the action's lead
		CurrentLead lead = backendManager.getMemory().getCurrentLeads().get(backendManager.getMemory().getFilteredActionReminders().get(rowTargetIndex).getCurrentLeadID());
		
		switch(columnTargetIndex)
		{
			case 0:
				return backendManager.getMemory().getFilteredActionReminders().get(rowTargetIndex).getActionDateAsDate();
			case 1:
				return backendManager.getMemory().getClients().get(lead.getClientID()).getName();
			case 2:
				return lead.getDealDateAsDate();
			case 3:
				return lead.getValue();
			case 4:
				return lead.getProbability();
			case 5:
				return backendManager.getMemory().getFilteredActionReminders().get(rowTargetIndex).getMessage();
			default:
				throw new IllegalArgumentException("Column Index: " + columnTargetIndex + System.getProperty("line.separator") + "Row index: " + rowTargetIndex);
		}
	}
}
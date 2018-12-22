package gui.currentleads;

import java.util.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import backend.BackendManager;
import backend.CurrentLead;

//This class controls how the table can be accessed and interacted with
public class CurrentLeadsTableModel extends AbstractTableModel
{
	private BackendManager backendManager;
	
	public CurrentLeadsTableModel(BackendManager backendManager)
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
		if(columnIndex == 2 || columnIndex == 4)
			//Make the table sort by value
			return Integer.class;
		//If the column contains a date...
		if(columnIndex == 1 || columnIndex == 5)
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
				return "Client name";
			case 1:
				return "Deal date";
			case 2:
				return "Deal value";
			case 3:
				return "Stage of negotiations";
			case 4:
				return "Probability";
			case 5:
				return "Reminder date";
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
		return backendManager.getMemory().getFilteredCurrentLeads().size();
	}
	
	public Object getValueAt(int rowTargetIndex, int columnTargetIndex)
	{
		CurrentLead lead = backendManager.getMemory().getFilteredCurrentLeads().get(rowTargetIndex);
		
		switch(columnTargetIndex)
		{
			case 0:
				return backendManager.getMemory().getClients().get(lead.getClientID()).getName();
			case 1:
				return lead.getDealDateAsDate();
			case 2:
				return lead.getValue();
			case 3:
				return backendManager.getMemory().getStagesOfNegotiation().get(lead.getStageOfNegotiationsID()).getNameOfStage();
			case 4:
				return lead.getProbability();
			case 5:
				if(lead.getActionReminderIDs().length == 0)
					return null;
				int lowestID = lead.getActionReminderIDs()[0];
				for(int actionReminderID : lead.getActionReminderIDs())
				{
					if(actionReminderID < lowestID)
						lowestID = actionReminderID;
				}
				return backendManager.getMemory().getActionReminders().get(lowestID).getActionDateAsDate();
			default:
				throw new IllegalArgumentException("Column Index: " + columnTargetIndex + System.getProperty("line.separator") + "Row index: " + rowTargetIndex);
		}
	}
}
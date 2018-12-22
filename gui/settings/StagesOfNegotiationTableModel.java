package gui.settings;

import java.util.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelListener;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import backend.BackendManager;
import backend.StageOfNegotiation;

//This class controls how the table can be accessed and interacted with
public class StagesOfNegotiationTableModel extends AbstractTableModel
{
	private BackendManager backendManager;
	
	public StagesOfNegotiationTableModel(BackendManager backendManager)
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
		//Sort the first column in alphabetical order
		if(columnIndex == 0)
			return new Object().getClass();
		//Sort the second column by value
		if(columnIndex == 1)
			return Integer.class;
		//Other columns are boolean
		return Boolean.class;
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
		return 4;
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
				return "Stage of negotiation";
			case 1:
				return "Default probability";
			case 2:
				return "Send to historical";
			case 3:
				return "Assume successful if moved";
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
		return backendManager.getMemory().getStagesOfNegotiation().size();
	}
	
	public Object getValueAt(int rowTargetIndex, int columnTargetIndex)
	{
		StageOfNegotiation stage = backendManager.getMemory().getStagesOfNegotiation().get(rowTargetIndex);
		
		switch(columnTargetIndex)
		{
			case 0:
				return stage.getNameOfStage();
			case 1:
				return stage.getDefaultProbability();
			case 2:
				return stage.leadShouldBeMoved();
			case 3:
				return stage.assumingSuccessfulIfMoved();
			default:
				throw new IllegalArgumentException("Column Index: " + columnTargetIndex + System.getProperty("line.separator") + "Row index: " + rowTargetIndex);
		}
	}
}
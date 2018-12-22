package gui;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public void setValue(Object value)
	{
		//If the cell contains a date...
		if(Date.class.isInstance(value))
		{
			//Render the date in the form dd/MM/yyyy
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			setText((value == null) ? "" : dateFormat.format(value));
		}
		//If the cell does not contain a date...
		else
		{
			//Render normally
			if(value == null)
				setText("");
			else
			{
				setText(value.toString());
				//Output a message
				System.out.println("DateCellRenderer used on non-date: " + value.toString());
			}
		}
	}
}
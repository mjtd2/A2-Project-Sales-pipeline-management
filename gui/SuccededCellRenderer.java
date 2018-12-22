package gui;

import javax.swing.table.DefaultTableCellRenderer;

public class SuccededCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public void setValue(Object value)
	{
		//If the cell contains a Boolean...
		if(Boolean.class.isInstance(value))
		{
			if(value == null)
				setText("");
			else
			//Render true as "Succeded" and false as "Failed"
			setText((((Boolean)value).booleanValue()) ? "Succeded" : "Failed");
		}
		//If the cell does not contain a Boolean...
		else
			setText((value == null) ? "" : value.toString());
	}
}
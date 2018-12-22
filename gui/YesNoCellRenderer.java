package gui;

import javax.swing.table.DefaultTableCellRenderer;

public class YesNoCellRenderer extends DefaultTableCellRenderer
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
			//Render true as "Yes" and false as "No"
			setText((((Boolean)value).booleanValue()) ? "Yes" : "No");
		}
		//If the cell does not contain a Boolean...
		else
			setText((value == null) ? "" : value.toString());
	}
}
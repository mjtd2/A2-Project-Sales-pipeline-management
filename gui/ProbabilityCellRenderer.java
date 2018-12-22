package gui;

import javax.swing.table.DefaultTableCellRenderer;

public class ProbabilityCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public void setValue(Object value)
	{
		//Render the probability with a % symbol appended
		setText((value == null) ? "" : value.toString() + "%");
	}
}
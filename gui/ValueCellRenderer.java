package gui;

import javax.swing.table.DefaultTableCellRenderer;

public class ValueCellRenderer extends DefaultTableCellRenderer
{
	static String mCurrencySymbol;
	
	public ValueCellRenderer(String currencySymbol)
	{
		mCurrencySymbol = currencySymbol;
	}
	
	@Override
	public void setValue(Object value)
	{
		//Render the value with a currency symbol in front
		setText((value == null) ? "" : mCurrencySymbol + value.toString());
	}
}
package backend;

import javax.swing.UIManager;

public class Starter
{
	public static void main(String[] args)
	{
		//Set native look & feel
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//Create the BackendManager which will organise the program
		new BackendManager();
	}
}
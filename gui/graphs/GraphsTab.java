package gui.graphs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import java.awt.Toolkit;

import backend.BackendListener;
import backend.BackendManager;

public class GraphsTab extends JPanel implements ActionListener, BackendListener
{
	//Construct a button for adding leads
	JTextField filenameTextBox = new JTextField();
	JButton exportButton = new JButton("Export to .csv file");
	JButton redrawButton = new JButton("Redraw graph");
	GraphPanel graphPanel;
	JComboBox clientComboBox;
	ComboBoxModel clientComboBoxModel;
	JComboBox earliestDealMonthComboBox;
	JComboBox latestDealMonthComboBox;
	JComboBox earliestDealYearComboBox;
	JComboBox latestDealYearComboBox;
	JCheckBox notThisClientCheckBox;
	JRadioButton showGuaranteedIncomeRadioButton;
	JRadioButton showIncomeAboveProbabilityRadioButton;
	JRadioButton showMaximumIncomeRadioButton;
	JRadioButton showOverallIncomeRadioButton;
	JSpinner probabilitySpinner;
	JLabel pastRevenueLabel;
	JLabel expectedRevenueLabel;
	int mPastRevenueThisPeriod;
	int mExpectedRevenueThisPeriod;
	
	JButton unfilterButton;
	JTable historicalDataTable;
	BackendManager backendManager;
	
	public GraphsTab(BackendManager backendMgr)
	{
		//Construct the JPanel part of this class, with a BorderLayout
		super(new BorderLayout());
		backendManager = backendMgr;
		backendManager.addListener(this);
		//Place the panel's title in the top left...
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.add(new JLabel("Graphs"), BorderLayout.WEST);
		//...by placing a dummy JPanel to its right...
		titlePanel.add(new JPanel(), BorderLayout.CENTER);
		//...and placing the unfilter button to the right of the dummy JPanel...
		ImageIcon filterIcon = new ImageIcon("Resources/Filter.png");
		unfilterButton = new JButton(filterIcon);
		unfilterButton.setToolTipText("Undo searches and filters");
		unfilterButton.addActionListener(this);
		titlePanel.add(unfilterButton, BorderLayout.EAST);
		//...and adding the title panel to the top
		this.add(titlePanel, BorderLayout.NORTH);
		
		JPanel graphHalfPanel = new JPanel(new BorderLayout());
		JPanel exportOptionsPanel = new JPanel(new BorderLayout());
		exportOptionsPanel.add(new JLabel("Filename"), BorderLayout.NORTH);
		exportButton.addActionListener(this);
		exportOptionsPanel.add(exportButton, BorderLayout.EAST);
		JPanel filenamePanel = new JPanel();
		filenameTextBox.setPreferredSize(new Dimension(200, 25));
		filenamePanel.add(filenameTextBox);
		filenamePanel.add(new JLabel(".csv"));
		exportOptionsPanel.add(filenamePanel, BorderLayout.WEST);
		exportOptionsPanel.add(new JPanel(), BorderLayout.CENTER);
		graphHalfPanel.add(exportOptionsPanel, BorderLayout.SOUTH);
		graphPanel = new GraphPanel(new ArrayList<>(), backendManager.getMemory().getSettings().getCurrencySymbol());
		graphHalfPanel.add(graphPanel);
		
		JPanel optionsHalfPanel = new JPanel(new BorderLayout());
		JPanel revenueThisPeriodPanel = new JPanel(new GridLayout(1, 2));
		pastRevenueLabel = new JLabel("<html><p style=\"text-align:center;\">Past revenue this period:<br><b>" + backendManager.getMemory().getSettings().getCurrencySymbol() + mPastRevenueThisPeriod, SwingConstants.CENTER);
		expectedRevenueLabel = new JLabel("<html><p style=\"text-align:center;\">Expected revenue this period:<br><b>" + backendManager.getMemory().getSettings().getCurrencySymbol() + mExpectedRevenueThisPeriod, SwingConstants.CENTER);
		revenueThisPeriodPanel.add(pastRevenueLabel);
		revenueThisPeriodPanel.add(expectedRevenueLabel);
		optionsHalfPanel.add(revenueThisPeriodPanel, BorderLayout.NORTH);
		JPanel filtersPanel = new JPanel(null);
		filtersPanel.setPreferredSize(new Dimension(350,360));
		
		JLabel filtersLabel = new JLabel("Filters");
		filtersLabel.setSize(50,20);
		filtersPanel.add(filtersLabel);
		
		JLabel clientNameLabel = new JLabel("Client name");
		clientNameLabel.setSize(75,20);
		clientNameLabel.setLocation(15,30);
		filtersPanel.add(clientNameLabel);
		
		clientComboBox = new JComboBox(new DefaultComboBoxModel(backendManager.getClientNames(true)));
		clientComboBox.setLocation(15,50);
		clientComboBox.setSize(250,30);
		clientComboBox.setEditable(false );
		filtersPanel.add(clientComboBox);
		
		notThisClientCheckBox = new JCheckBox("NOT");
		notThisClientCheckBox.setSize(50,20);
		notThisClientCheckBox.setLocation(275,55);
		filtersPanel.add(notThisClientCheckBox);
		
		String[] earliestMonths = {"01","04","07","10"};
		earliestDealMonthComboBox = new JComboBox(earliestMonths);
		earliestDealMonthComboBox.setLocation(15,110);
		earliestDealMonthComboBox.setSize(45,30);
		filtersPanel.add(earliestDealMonthComboBox);

		String[] latestMonths = {"03","06","09","12"};
		latestDealMonthComboBox = new JComboBox(latestMonths);
		latestDealMonthComboBox.setLocation(200,110);
		latestDealMonthComboBox.setSize(45,30);
		filtersPanel.add(latestDealMonthComboBox);
		
		latestDealYearComboBox = new JComboBox(backendManager.getMemory().getListOfYears());
		latestDealYearComboBox.setLocation(255,110);
		latestDealYearComboBox.setSize(55,30);
		filtersPanel.add(latestDealYearComboBox);
		
		JLabel dealDateLabel = new JLabel("Deal date");
		dealDateLabel.setSize(50,20);
		dealDateLabel.setLocation(15,90);
		filtersPanel.add(dealDateLabel);
		
		earliestDealYearComboBox = new JComboBox(backendManager.getMemory().getListOfYears());
		earliestDealYearComboBox.setLocation(70,110);
		earliestDealYearComboBox.setSize(55,30);
		filtersPanel.add(earliestDealYearComboBox);
		
		JLabel toLabel = new JLabel("to");
		toLabel.setSize(50,20);
		toLabel.setLocation(150,115);
		filtersPanel.add(toLabel);
		
		showGuaranteedIncomeRadioButton = new JRadioButton("Show guaranteed projected income", true);
		showGuaranteedIncomeRadioButton.setSize(200,20);
		showGuaranteedIncomeRadioButton.setLocation(15,165);
		filtersPanel.add(showGuaranteedIncomeRadioButton);
		
		showOverallIncomeRadioButton = new JRadioButton("Show overall projected income");
		showOverallIncomeRadioButton.setSize(200,20);
		showOverallIncomeRadioButton.setLocation(15,195);
		filtersPanel.add(showOverallIncomeRadioButton);
		
		showMaximumIncomeRadioButton = new JRadioButton("Show maximum projected income");
		showMaximumIncomeRadioButton.setSize(200,20);
		showMaximumIncomeRadioButton.setLocation(15,225);
		filtersPanel.add(showMaximumIncomeRadioButton);
		
		showIncomeAboveProbabilityRadioButton = new JRadioButton("Show maximum projected income with at least");
		showIncomeAboveProbabilityRadioButton.setSize(250,20);
		showIncomeAboveProbabilityRadioButton.setLocation(15,255);
		filtersPanel.add(showIncomeAboveProbabilityRadioButton);
		
		//Put the radio buttons in a group so that only one may be selected at once
		ButtonGroup group = new ButtonGroup();
		group.add(showGuaranteedIncomeRadioButton);
		group.add(showOverallIncomeRadioButton);
		group.add(showMaximumIncomeRadioButton);
		group.add(showIncomeAboveProbabilityRadioButton);
		
		//0 to 100
		probabilitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		probabilitySpinner.setLocation(15,280);
		probabilitySpinner.setSize(50,30);
		filtersPanel.add(probabilitySpinner);
		
		JLabel chanceLabel = new JLabel("% chance of being earned");
		chanceLabel.setSize(150,20);
		chanceLabel.setLocation(75,285);
		filtersPanel.add(chanceLabel);
		
		redrawButton.setLocation(229,328);
		redrawButton.setSize(100,30);
		redrawButton.addActionListener(this);
		filtersPanel.add(redrawButton);
		
		optionsHalfPanel.add(new JScrollPane(filtersPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		
		this.add(optionsHalfPanel, BorderLayout.EAST);
		this.add(graphHalfPanel, BorderLayout.CENTER);
	}
	
	//If a button has been pressed...
	public void actionPerformed(ActionEvent e)
	{
		//If 'Add client...' has been pressed...
		if(e.getSource() == redrawButton)
		{
			repaint();
			backendManager.generateNewGraph(clientComboBox.getSelectedIndex() - 1, notThisClientCheckBox.isSelected(), (String) earliestDealMonthComboBox.getSelectedItem(), (String) earliestDealYearComboBox.getSelectedItem(), (String) latestDealMonthComboBox.getSelectedItem(), (String) latestDealYearComboBox.getSelectedItem(), showGuaranteedIncomeRadioButton.isSelected(), showOverallIncomeRadioButton.isSelected(), showMaximumIncomeRadioButton.isSelected(), probabilitySpinner.getValue().toString());
		}
		//If Export has been pressed...
		else if(e.getSource() == exportButton)
			backendManager.exportGraph(filenameTextBox.getText(), graphPanel.getPoints(), graphPanel.getEarliestQuarter(), graphPanel.getEarliestYear());
		//If unfilter is pressed...
		else if(e.getSource() == unfilterButton)
			backendManager.generateNewGraph(-1, false, (String) earliestDealMonthComboBox.getSelectedItem(), (String) earliestDealYearComboBox.getSelectedItem(), (String) latestDealMonthComboBox.getSelectedItem(), (String) latestDealYearComboBox.getSelectedItem(), showGuaranteedIncomeRadioButton.isSelected(), showOverallIncomeRadioButton.isSelected(), showMaximumIncomeRadioButton.isSelected(), Integer.toString(0));
	}
	
	public void backendUpdated()
	{
		revalidate();
		repaint();
	}
	
	public GraphPanel getGraphPanel()
	{
		return graphPanel;
	}
	
	public void updateComboBox()
	{
		String selectedClient = clientComboBox.getSelectedItem().toString();
		clientComboBox.setModel(new DefaultComboBoxModel(backendManager.getClientNames(true)));
		clientComboBox.setSelectedItem(selectedClient);
	}
	
	public void setPastRevenueThisPeriod(int value)
	{
		mPastRevenueThisPeriod = value;
		pastRevenueLabel.setText("<html><p style=\"text-align:center;\">Past revenue this period:<br><b>" + backendManager.getMemory().getSettings().getCurrencySymbol() + mPastRevenueThisPeriod);
	}
	
	public void setExpectedRevenueThisPeriod(int value)
	{
		mExpectedRevenueThisPeriod = value;
		expectedRevenueLabel.setText("<html><p style=\"text-align:center;\">Expected revenue this period:<br><b>" + backendManager.getMemory().getSettings().getCurrencySymbol() + mExpectedRevenueThisPeriod);
	}
}
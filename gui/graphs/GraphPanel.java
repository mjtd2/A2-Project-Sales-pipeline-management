/*
This code is modified , having first been acquired from
http://stackoverflow.com/questions/8693342/drawing-a-simple-line-graph-in-java .
Question asked by -			http://stackoverflow.com/users/1058210/user1058210
Original code written by -	http://stackoverflow.com/users/522444/hovercraft-full-of-eels
Original code edited by -	http://stackoverflow.com/users/753012/rodrigo-castro
The second version of the code was edited further by me.
The second version of the code may be found at
https://gist.github.com/roooodcastro/6325153
*/

package gui.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class GraphPanel extends JPanel
{
	private int padding = 20;//Border around graph
	private int labelPadding = 35;//Amount of space for axis value labels
	int mEarliestQuarter;
	int mEarliestYear;
	private Color lineColor = new Color(44, 102, 230, 180);
	private Color pointColor = new Color(100, 100, 100, 180);
	private Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4;
	private int numberYDivisions;
	private List<Double> values;
	String mCurrencySymbol;

	public GraphPanel(List<Double> values, String currencySymbol)
	{
		this.values = values;
		mCurrencySymbol = currencySymbol;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (values.size() - 1);
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxValue() - getMinValue());

		List<Point> graphPoints = new ArrayList<>();
		for (int i = 0; i < values.size(); i++)
		{
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxValue() - values.get(i)) * yScale + padding);
			graphPoints.add(new Point(x1, y1));
		}
		
		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);
		
		int numberOfSF = String.valueOf((int) getMaxValue()).length();
		numberYDivisions = (int) (Math.ceil(getMaxValue() * Math.pow(10, 1-numberOfSF)));
		
		FontMetrics metrics = g2.getFontMetrics();
		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++)
		{
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (values.size() > 0)
			{
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = mCurrencySymbol + (int) Math.round(((int) ((getMinValue() + (getMaxValue() - getMinValue()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0) + "";
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}
		
		AffineTransform noTransformation = g2.getTransform();
		g2.rotate(-Math.PI/2);
		String incomeLabel = "Income";
		//These values are for a rotated x-y plane.
		g2.drawString(incomeLabel, -getHeight()/2, padding + labelPadding - 40);
		g2.setTransform(noTransformation);
		
		// and for x axis
		for (int i = 0; i < values.size(); i++)
		{
			if (values.size() > 1)
			{
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (values.size() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				int currentQuarter = mEarliestQuarter;
				int currentYear = mEarliestYear;
				if ((i % ((int) (values.size() * 40.0 /(getWidth() - (2 * padding) - labelPadding)) + 1)) == 0)//40.0 is the approximate width of an x-axis label.
				{
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					currentQuarter = i + mEarliestQuarter;
					if(currentQuarter > 3)
					{
						currentYear += currentQuarter / 4;
						currentQuarter = currentQuarter % 4;
					}
					String xLabel = (currentQuarter + 1)*3 + "/" + currentYear;
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}
		
		g2.drawString("Date", getWidth()/2, getHeight() - 20);
		
		// create x and y axes 
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);
		
		Stroke oldStroke = g2.getStroke();
		g2.setColor(lineColor);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++)
		{
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);
		}
		
		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int i = 0; i < graphPoints.size(); i++)
		{
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			g2.fillOval(x, y, ovalW, ovalH);
		}
	}
	
	private double getMinValue()
	{
		return 0.0;
	}
	
	private double getMaxValue()
	{
		double maxValue = Double.MIN_VALUE;
		for (Double value : values)
		{
			maxValue = Math.max(maxValue, value);
		}
		//Round the maximum value to the nearest larger integer
		int maxValueInt = (int) Math.ceil(maxValue);
		//Get the number of significant figures in the integer
		int length = String.valueOf(maxValueInt).length();
		//Round up the maximum value to the nearest larger value, considering only the first significant figure
		maxValue *= Math.pow(10, 1-length);
		maxValue = Math.ceil(maxValue);
		maxValue *= Math.pow(10, length-1);
		return Math.ceil(maxValue);
	}

	public void redraw(List<Double> values, int earliestQuarter, int earliestYear)
	{
		mEarliestQuarter = earliestQuarter;
		mEarliestYear = earliestYear;
		this.values = values;
		invalidate();
		this.repaint();
	}
	
	public ArrayList<Double> getPoints()
	{
		return (ArrayList<Double>)values;
	}
	
	public int getEarliestQuarter()
	{
		return mEarliestQuarter;
	}
	
	public int getEarliestYear()
	{
		return mEarliestYear;
	}
}
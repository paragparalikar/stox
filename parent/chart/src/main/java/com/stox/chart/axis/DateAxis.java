package com.stox.chart.axis;

import javafx.scene.layout.Pane;

import com.stox.chart.view.ChartView;

public class DateAxis extends Pane {

	private final ChartView chartView;
	private double unitWidth = 5;
	private double upperBound;
	private int barCount = 200;
	private int maxBarCount = 800;
	private int minBarCount = 20;
	private int blankBarSpace = 5;

	public DateAxis(final ChartView chartView) {
		this.chartView = chartView;
	}

	@Override
	protected void layoutChildren() {

	}

	public double getUnitWidth() {
		return unitWidth;
	}

	public void reset() {
		unitWidth = getEffectiveWidth() / barCount;
		upperBound = -1 * blankBarSpace * unitWidth;
	}

	public double getDisplayPosition(final int index) {
		return getEffectiveWidth() + upperBound - index * unitWidth;
	}

	private double getEffectiveWidth() {
		return chartView.getPrimaryChart().getArea().getWidth();
	}

	public int getIndexForDisplay(double position) {
		return (int) ((getEffectiveWidth() - position + upperBound) / unitWidth + 1);
	}

	public int getUpperBoundIndex() {
		return getIndexForDisplay(getEffectiveWidth());
	}

	public int getLowerBoundIndex() {
		return getIndexForDisplay(0);
	}

}

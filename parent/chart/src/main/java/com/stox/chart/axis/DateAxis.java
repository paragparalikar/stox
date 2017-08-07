package com.stox.chart.axis;

import javafx.scene.layout.Pane;

import com.stox.chart.axis.helper.DateAxisTickHelper;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.view.ChartView;
import com.stox.core.ui.util.UiUtil;

public class DateAxis extends Pane {
	private static final int MAX = 800;
	private static final int MIN = 20;

	private final ChartView chartView;
	private int barCount = 200;
	private double upperBound;
	private int blankBarSpace = 5;
	private final DateAxisTickHelper dateAxisTickHelper = new DateAxisTickHelper(this);

	public DateAxis(final ChartView chartView) {
		this.chartView = chartView;
		UiUtil.classes(this, "date-axis");
	}

	@Override
	protected void layoutChildren() {

	}

	public double getUnitWidth() {
		return getEffectiveWidth() / barCount;
	}

	public void reset() {
		upperBound = -1 * blankBarSpace * getUnitWidth();
	}

	public double getDisplayPosition(final int index) {
		return getEffectiveWidth() + upperBound - index * getUnitWidth();
	}

	private double getEffectiveWidth() {
		return chartView.getPrimaryChart().getArea().getWidth();
	}

	public int getIndexForDisplay(double position) {
		return (int) ((getEffectiveWidth() - position + upperBound) / getUnitWidth() + 1);
	}

	public int getUpperBoundIndex() {
		return getIndexForDisplay(getEffectiveWidth());
	}

	public int getLowerBoundIndex() {
		return getIndexForDisplay(0);
	}

	public boolean pan(double x, double startX) {
		upperBound += x - startX;
		checkExtraDataNeeded();
		return x != startX;
	}

	private void checkExtraDataNeeded() {
		final PrimaryPricePlot primaryPricePlot = chartView.getPrimaryChart().getPrimaryPricePlot();
		if (primaryPricePlot.getUnits().size() < getLowerBoundIndex()) {
			primaryPricePlot.loadExtra();
		}
	}

	public void addTick(final double x, final long date, final long previousDate) {
		dateAxisTickHelper.addTick(x + getUnitWidth() / 2, date, previousDate);
	}

	public void clear() {
		dateAxisTickHelper.clear();
	}

	public void zoomIn() {
		if (MIN <= 0.9 * barCount) {
			int oldBarCount = barCount;
			barCount = (int) Math.ceil(0.9 * barCount);
			int delta = oldBarCount - barCount;
			upperBound -= (delta * getUnitWidth() / 2);
		}
	}

	public void zoomOut() {
		if (MAX >= 1.1 * barCount) {
			barCount = (int) Math.floor(1.1 * barCount);
			upperBound -= 0.05 * barCount;
			checkExtraDataNeeded();
		}
	}

}

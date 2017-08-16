package com.stox.chart.axis;

import java.util.List;

import javafx.scene.layout.Pane;

import com.stox.chart.axis.helper.DateAxisTickHelper;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.unit.Unit;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.MathUtil;

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

	public double getDisplayPosition(final long date) {
		final List<Unit<Bar>> priceUnits = chartView.getPrimaryChart().getPrimaryPricePlot().getUnits();
		if (!priceUnits.isEmpty()) {
			final int lastBarIndex = MathUtil.limit(0, getUpperBoundIndex(), priceUnits.size() - 1);
			final int firstBarIndex = MathUtil.limit(0, getLowerBoundIndex(), priceUnits.size() - 1);
			final Bar lastBar = priceUnits.get(lastBarIndex).getModel();
			final Bar firstBar = priceUnits.get(firstBarIndex).getModel();
			if (date <= lastBar.getDate().getTime() && date >= firstBar.getDate().getTime()) {
				/*
				 * Datas are already sorted by date Let's make use of this fact by using binary search Internet says it's fastest
				 */
				int low = 0;
				int high = priceUnits.size() - 1;

				while (low <= high) {
					int mid = (low + high) >>> 1;
					if (mid + 1 == priceUnits.size()) {
						return getDisplayPosition(mid);
					}
					final Bar midBar = priceUnits.get(mid).getModel();
					final long midVal = midBar.getDate().getTime();
					final Bar midPrevBar = priceUnits.get(mid + 1).getModel();
					final long midNextVal = midPrevBar.getDate().getTime();
					if (date == midNextVal)
						return getDisplayPosition(mid + 1);
					else if (date == midVal)
						return getDisplayPosition(mid);
					else if (date < midNextVal)
						low = mid + 1;
					else if (date > midVal)
						high = mid - 1;
					else
						return getDisplayPosition(mid); // key found
				}
			} else {
				return (int) MathUtil.praportion(firstBar.getDate().getTime(), date, lastBar.getDate().getTime(), getDisplayPosition(firstBarIndex),
						getDisplayPosition(lastBarIndex));
			}
		}
		return -1;
	}

	private double getEffectiveWidth() {
		return chartView.getPrimaryChart().getArea().getWidth();
	}

	public int getIndexForDisplay(double position) {
		return (int) ((getEffectiveWidth() - position + upperBound) / getUnitWidth() + 1);
	}

	public long getValueForDisplay(final double position) {
		final int index = getIndexForDisplay(position);
		final List<Unit<Bar>> priceUnits = chartView.getPrimaryChart().getPrimaryPricePlot().getUnits();
		final int lastBarIndex = MathUtil.limit(0, getUpperBoundIndex(), priceUnits.size() - 1);
		final int firstBarIndex = MathUtil.limit(0, getLowerBoundIndex(), priceUnits.size() - 1);
		if (index >= lastBarIndex && index <= firstBarIndex) {
			return priceUnits.get(index).getModel().getDate().getTime();
		}
		final Bar lastBar = priceUnits.get(lastBarIndex).getModel();
		final Bar firstBar = priceUnits.get(firstBarIndex).getModel();
		return (long) MathUtil.praportion(getDisplayPosition(lastBarIndex), position, getDisplayPosition(firstBarIndex), lastBar.getDate().getTime(), firstBar.getDate().getTime());
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

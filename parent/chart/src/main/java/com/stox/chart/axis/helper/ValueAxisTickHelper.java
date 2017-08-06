package com.stox.chart.axis.helper;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.text.Text;

import com.stox.chart.axis.ValueAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.core.util.StringUtil;

public class ValueAxisTickHelper {

	protected static final double TEXT_HEIGHT = new Text("1").getLayoutBounds().getHeight();
	protected static final double TICK_LENGTH = 6;
	public static final double[] TICK_UNIT_DEFAULTS = { 1.0E-10d, 2.5E-10d, 5.0E-10d, 1.0E-9d, 2.5E-9d, 5.0E-9d, 1.0E-8d, 2.5E-8d, 5.0E-8d, 1.0E-7d, 2.5E-7d, 5.0E-7d, 1.0E-6d,
			2.5E-6d, 5.0E-6d, 1.0E-5d, 2.5E-5d, 5.0E-5d, 1.0E-4d, 2.5E-4d, 5.0E-4d, 0.0010d, 0.0025d, 0.0050d, 0.01d, 0.025d, 0.05d, 0.1d, 0.25d, 0.5d, 1.0d, 2.5d, 5.0d, 10.0d,
			25.0d, 50.0d, 100.0d, 250.0d, 500.0d, 1000.0d, 2500.0d, 5000.0d, 10000.0d, 25000.0d, 50000.0d, 100000.0d, 250000.0d, 500000.0d, 1000000.0d, 2500000.0d, 5000000.0d,
			1.0E7d, 2.5E7d, 5.0E7d, 1.0E8d, 2.5E8d, 5.0E8d, 1.0E9d, 2.5E9d, 5.0E9d, 1.0E10d, 2.5E10d, 5.0E10d, 1.0E11d, 2.5E11d, 5.0E11d, 1.0E12d, 2.5E12d, 5.0E12d };

	private final ValueAxis valueAxis;
	private final List<Label> labels = new ArrayList<Label>();

	public ValueAxisTickHelper(final ValueAxis valueAxis) {
		this.valueAxis = valueAxis;
	}

	public void layoutTicks() {
		final Chart chart = valueAxis.getChart();
		final double tickRange = getTickRange();
		final double max = chart.getMax();
		final double min = chart.getMin();
		final double highestTickValue = Math.ceil(max / tickRange) * tickRange;
		double lowestTickValue = Math.floor(min / tickRange) * tickRange;
		clear();
		if ((lowestTickValue <= highestTickValue) && (tickRange > 0)) {
			while (lowestTickValue < highestTickValue) {
				final double y = valueAxis.getDisplayPosition(lowestTickValue, min, max);
				if ((y >= valueAxis.getLayoutY()) && (y <= (valueAxis.getLayoutY() + valueAxis.getHeight()))) {
					final String text = StringUtil.stringValueOf(lowestTickValue);
					addTickMark(y, text);
				}
				lowestTickValue += tickRange;
			}
		}
	}

	public void clear() {
		if (valueAxis.getChart() instanceof PrimaryChart) {
			final PrimaryChart primaryChart = (PrimaryChart) valueAxis.getChart();
			primaryChart.getGrid().clearHorizontal();
		}
		valueAxis.getChildren().removeAll(labels);
	}

	public double getTickRange() {
		final double max = valueAxis.getChart().getMax();
		final double min = valueAxis.getChart().getMin();
		final int numOfTickMarks = 8;
		double tickRange = Math.abs(max - min) / numOfTickMarks;
		for (final double tickUnitDefault : TICK_UNIT_DEFAULTS) {
			if (tickRange <= tickUnitDefault) {
				tickRange = tickUnitDefault;
				break;
			}
		}
		return tickRange;
	}

	public void addTickMark(final double y, final String text) {
		final Chart chart = valueAxis.getChart();
		if (chart instanceof PrimaryChart) {
			final PrimaryChart primaryChart = (PrimaryChart) chart;
			primaryChart.getGrid().addHorizontal(y);
		}

		final double labelLayoutY = y - (TEXT_HEIGHT / 2) - 5;
		if (!labels.isEmpty()) {
			final Label previousLabel = labels.get(labels.size() - 1);
			if (labelLayoutY > previousLabel.getLayoutY() && labelLayoutY < previousLabel.getLayoutY() + previousLabel.getHeight()) {
				return;
			}
		}

		final Label label = new Label(text);
		if (labels.add(label)) {
			valueAxis.getChildren().add(label);
			label.resizeRelocate(TICK_LENGTH + 3, labelLayoutY, valueAxis.getWidth() - 2, TEXT_HEIGHT + 10);
		}
	}

}

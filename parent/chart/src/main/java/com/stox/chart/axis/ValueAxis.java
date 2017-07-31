package com.stox.chart.axis;

import javafx.scene.layout.Pane;

import com.stox.chart.chart.Chart;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.MathUtil;

public class ValueAxis extends Pane {

	private final Chart chart;

	public ValueAxis(final Chart chart) {
		this.chart = chart;
		UiUtil.classes(this, "value-axis");
	}

	public double getValueForDisplay(final double position, final double plotMin, final double plotMax) {
		if (chart.getChartView().isSemilog()) {
			final double axisMin = Math.log(plotMin);
			final double axisMax = Math.log(plotMax);
			final double min = getVerticalSpace();
			final double max = chart.getArea().getHeight() - getVerticalSpace();
			return Math.pow(Math.E, axisMin + (min + max - position) * (axisMax - axisMin) / (max - min));
		}
		return plotMin + plotMax - MathUtil.praportion(getMinY(), position, getMaxY(), plotMin, plotMax);
	}

	public double getDisplayPosition(double value, final double plotMin, final double plotMax) {
		if (chart.getChartView().isSemilog()) {
			value = Math.log(value);
			final double min = getVerticalSpace();
			final double max = chart.getArea().getHeight() - getVerticalSpace();
			final double axisMin = Math.log(plotMin);
			final double axisMax = Math.log(plotMax);

			return min + max - ((value - axisMin) / (axisMax - axisMin)) * (max - min);
		}
		return getDisplayPosition(plotMin, value, plotMax, 0, chart.getArea().getHeight(), getVerticalSpace());
	}

	public double getDisplayPosition(double min, double value, double max, double displayMin, double displayMax, double space) {
		return displayMax - MathUtil.praportion(min, value, max, displayMin + space, displayMax - space);
	}

	private double getMaxY() {
		return chart.getArea().getHeight() - getVerticalSpace();
	}

	private double getMinY() {
		return getVerticalSpace();
	}

	private double getVerticalSpace() {
		return chart.getHeight() * 0.025;
	}
}

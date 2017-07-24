package com.stox.chart.chart;

import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.view.ChartView;

public class PrimaryChart extends Chart {

	private final PrimaryPricePlot primaryPricePlot = new PrimaryPricePlot(this);

	public PrimaryChart(final ChartView chartView) {
		super(chartView);
		getArea().getChildren().add(primaryPricePlot);
	}

	public PrimaryPricePlot getPrimaryPricePlot() {
		return primaryPricePlot;
	}

	@Override
	public void setDirty() {
		primaryPricePlot.setDirty();
		super.setDirty();
	}

}

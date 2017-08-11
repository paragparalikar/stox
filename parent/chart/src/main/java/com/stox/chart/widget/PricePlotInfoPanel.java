package com.stox.chart.widget;

import com.stox.chart.plot.PricePlot;
import com.stox.core.model.Bar;

public class PricePlotInfoPanel extends PlotInfoPanel<Bar> {

	private final PricePlot pricePlot;

	public PricePlotInfoPanel(PricePlot pricePlot) {
		super(pricePlot);
		this.pricePlot = pricePlot;
	}

	@Override
	protected void removePlot() {
		pricePlot.getChart().getPricePlots().remove(pricePlot);
	}
}

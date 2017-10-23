package com.stox.chart.indicator;

import java.util.List;

import com.stox.chart.chart.Chart;
import com.stox.chart.plot.Plot;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range;
import com.stox.core.model.Bar;

public class IndicatorPlot<M extends Range> extends Plot<M> {
	
	private final Object config;
	private final ChartIndicator<Object,M> chartIndicator;

	public IndicatorPlot(Chart chart, ChartIndicator<Object,M> chartIndicator) {
		super(chart);
		this.chartIndicator = chartIndicator;
		config = chartIndicator.buildDefaultConfig();
		getPlotInfoPane().setName(getName());
	}

	@Override
	public String getName() {
		return chartIndicator.getName();
	}

	@Override
	public void load() {
		final List<Bar> bars = getChart().getChartView().getPrimaryChart().getPrimaryPricePlot().getModels();
		final List<M> models = chartIndicator.compute(config, bars);
		getModels().setAll(models);
	}

	@Override
	public UnitType getUnitType() {
		return chartIndicator.getUnitType(config);
	}
	
}

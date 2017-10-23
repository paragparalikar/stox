package com.stox.chart.indicator;

import java.util.List;

import com.stox.chart.chart.Chart;
import com.stox.chart.plot.Plot;
import com.stox.chart.unit.UnitType;
import com.stox.chart.widget.PlotInfoPanel;
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
		final Style style = chartIndicator.getStyle();
		
		if(null != style && null != style.getNode()) {
			getChildren().add(style.getNode());
		}
	}
	
	public Object getConfig() {
		return config;
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
		update();
	}

	@Override
	public UnitType getUnitType() {
		return chartIndicator.getUnitType(config);
	}
	
	@Override
	protected PlotInfoPanel<M> createPlotInfoPanel() {
		return new IndicatorPlotInfoPanel<>(this);
	}
	
	@Override
	protected void postLayout() {
		super.postLayout();
		final Style style = chartIndicator.getStyle();
		if(null != style) {
			style.apply(this);
		}
	}
	
}

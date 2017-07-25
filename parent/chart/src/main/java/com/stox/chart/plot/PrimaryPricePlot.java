package com.stox.chart.plot;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.unit.CandlePriceUnit;
import com.stox.chart.unit.PriceUnitType;
import com.stox.chart.unit.Unit;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chart", "priceUnitType" })
public class PrimaryPricePlot extends PricePlot {

	private final PrimaryChart chart;
	private PriceUnitType priceUnitType;

	public PrimaryPricePlot(final PrimaryChart chart) {
		super(chart);
		this.chart = chart;
	}

	@Override
	protected Unit<Bar> create(int index, Bar model) {
		return new CandlePriceUnit(index, model, this);
	}

	@Override
	public void load() {
		super.load();
		chart.getPricePlots().forEach(Plot::load);
	}

	@Override
	protected void addModels(List<Bar> bars) {
		super.addModels(bars);
		final ChartView chartView = chart.getChartView();
		chartView.getCharts().forEach(c -> {
			c.getPlots().forEach(Plot::load);
		});
	}

}

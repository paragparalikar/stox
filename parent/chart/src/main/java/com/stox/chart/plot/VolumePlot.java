package com.stox.chart.plot;

import java.util.List;
import java.util.stream.Collectors;

import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range.DoubleRange;

public class VolumePlot extends Plot<DoubleRange> {

	public VolumePlot(final Chart chart) {
		super(chart);
	}

	@Override
	public String getName() {
		return "Volume"; // TODO I18N here
	}

	@Override
	public void setChart(Chart chart) {
		super.setChart(chart);
		setOpacity(chart instanceof PrimaryChart ? 0.2 : 1);
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.BAR;
	}

	@Override
	public void load() {
		final PrimaryPricePlot primaryPricePlot = getChart().getChartView().getPrimaryChart().getPrimaryPricePlot();
		final List<DoubleRange> models = primaryPricePlot.getModels().stream().map(bar -> new DoubleRange(bar.getVolume())).collect(Collectors.toList());
		getModels().setAll(models);
		update();
	}

	@Override
	protected void updateChartValueBounds() {

	}

}

package com.stox.chart.plot;

import java.util.List;
import java.util.stream.Collectors;

import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.unit.BarUnit;
import com.stox.chart.unit.Unit;
import com.stox.core.intf.Range.DoubleRange;

public class VolumePlot extends Plot<DoubleRange> {

	public VolumePlot(final Chart chart) {
		super(chart);
	}

	@Override
	public void setChart(Chart chart) {
		super.setChart(chart);
		setOpacity(chart instanceof PrimaryChart ? 0.2 : 1);
	}

	@Override
	protected Unit<DoubleRange> create(final int index, final DoubleRange model) {
		return new BarUnit<>(index, model, this);
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

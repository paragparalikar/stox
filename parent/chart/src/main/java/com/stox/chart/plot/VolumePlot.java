package com.stox.chart.plot;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;

import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.unit.BarUnit;
import com.stox.chart.unit.Unit;
import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.Bar;

public class VolumePlot extends Plot<DoubleRange> {

	public VolumePlot(final Chart chart) {
		super(chart);
		final PrimaryPricePlot primaryPricePlot = getChart().getChartView().getPrimaryChart().getPrimaryPricePlot();
		primaryPricePlot.getModels().addListener((ListChangeListener<Bar>) (change) -> {
			while (change.next()) {
				if (change.wasAdded()) {
					final List<DoubleRange> models = change.getAddedSubList().stream().map(bar -> new DoubleRange(bar.getVolume())).collect(Collectors.toList());
					getModels().addAll(models);
				}
				if (change.wasRemoved()) {
					getModels().clear();
				}
			}
		});
	}

	@Override
	public void setChart(Chart chart) {
		super.setChart(chart);
		setOpacity(chart instanceof PrimaryChart ? 0.3 : 1);
	}

	@Override
	protected Unit<DoubleRange> create(final int index, final DoubleRange model) {
		return new BarUnit<>(index, model, this);
	}

	@Override
	public void load() {
		update();
	}

	@Override
	protected void updateChartValueBounds() {

	}

}

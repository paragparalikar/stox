package com.stox.chart.plot;

import javafx.collections.ListChangeListener;

import com.stox.chart.chart.Chart;
import com.stox.chart.unit.LineUnit;
import com.stox.chart.unit.Unit;
import com.stox.core.model.Bar;

public class VolumePlot extends Plot<Bar> {

	public VolumePlot(final Chart chart) {
		super(chart);
		final PrimaryPricePlot primaryPricePlot = getChart().getChartView().getPrimaryChart().getPrimaryPricePlot();
		primaryPricePlot.getModels().addListener((ListChangeListener<Bar>) (change) -> {
			while (change.next()) {
				if (change.wasAdded()) {
					getModels().addAll(change.getAddedSubList());
				}
				if (change.wasRemoved()) {
					getModels().removeAll(change.getRemoved());
				}
			}
		});
	}

	@Override
	protected Unit<Bar> create(final int index, final Bar model) {
		return new LineUnit<>(index, model, this);
	}

	@Override
	public double min(Bar model) {
		return model.getVolume();
	}

	@Override
	public double max(Bar model) {
		return model.getVolume();
	}

	@Override
	public void load() {

	}

}

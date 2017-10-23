package com.stox.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.stox.chart.chart.Chart;
import com.stox.chart.plot.Plot;
import com.stox.chart.unit.Unit;
import com.stox.chart.unit.UnitType;
import com.stox.chart.widget.PlotInfoPanel;
import com.stox.core.model.Bar;
import com.stox.screen.unit.BearishScreenUnit;
import com.stox.screen.unit.BullishScreenUnit;
import com.stox.screen.unit.NeutralScreenUnit;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ScreenPlot extends Plot<Bar> {

	private final Object config;
	private final Screen screen;

	public ScreenPlot(Chart chart, Screen screen) {
		super(chart);
		this.screen = screen;
		config = screen.buildDefaultConfig();
		getPlotInfoPane().setName(getName());
	}

	@Override
	public String getName() {
		return screen.getName();
	}

	public Object getConfig() {
		return config;
	}

	@Override
	public PlotInfoPanel<Bar> getPlotInfoPane() {
		return new ScreenPlotInfoPanel(this);
	}

	@Override
	public void load() {
		final List<Bar> bars = getChart().getChartView().getPrimaryChart().getPrimaryPricePlot().getModels();
		final List<Bar> models = new ArrayList<>(bars.size());
		final int minBarCount = screen.getMinBarCount(config);
		for (int index = 0; index < bars.size(); index++) {
			if (index < bars.size() - minBarCount && screen.isMatch(config, bars.subList(index, index + minBarCount))) {
				models.add(bars.get(index));
			} else {
				models.add(null);
			}
		}
		getModels().setAll(models);
		update();
	}

	@Override
	public UnitType getUnitType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createUnits(int from, int to) {
		IntStream.range(from, to).forEach(index -> {
			final Bar model = getModels().get(index);
			if (null != model) {
				final Unit<Bar> unit = create(index, model);
				getUnits().add(index, unit);
				getChildren().add(unit);
			} else {
				getUnits().add(null);
			}
		});
	}

	@Override
	protected Unit<Bar> create(int index, Bar bar) {
		switch (screen.getScreenType()) {
		case BEARISH:
			return new BearishScreenUnit(index, bar, this);
		case BULLISH:
			return new BullishScreenUnit(index, bar, this);
		case NEUTRAL:
			return new NeutralScreenUnit(index, bar, this);
		default:
			return null;
		}
	}

}

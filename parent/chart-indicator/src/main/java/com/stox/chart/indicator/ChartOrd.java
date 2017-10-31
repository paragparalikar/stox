package com.stox.chart.indicator;

import com.stox.chart.plot.Plot;
import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.Unit;
import com.stox.chart.unit.UnitType;
import com.stox.core.model.Swing;
import com.stox.indicator.Ord;
import com.stox.indicator.Ord.Config;

import javafx.scene.shape.Line;

public class ChartOrd extends Ord implements ChartIndicator<Config, Swing>, LayoutDelegate<Swing> {

	@Override
	public Underlay getUnderlay(Config config) {
		return Underlay.PRICE;
	}

	@Override
	public UnitType getUnitType(Config config) {
		return null;
	}

	@Override
	public Style getStyle() {
		return null;
	}

	@Override
	public void layout(IndicatorPlot<Swing> plot) {
		plot.getUnits().forEach(unit -> unit.setVisible(false));
	}

}

class OrdUnit extends Unit<Swing> {
	
	private final Line line = new Line();

	public OrdUnit(int index, Swing model, Plot<Swing> plot) {
		super(index, model, plot);
	}
	
	@Override
	public void update() {
		super.update();
		line.setStroke(getPlot().getColor());
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Plot<Swing> plot = getPlot();
	}

}

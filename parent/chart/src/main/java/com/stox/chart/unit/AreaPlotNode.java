package com.stox.chart.unit;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import com.stox.chart.plot.Plot;

public class AreaPlotNode extends Polygon implements PlotNode {

	private final Plot<?> plot;

	public AreaPlotNode(final Plot<?> plot) {
		this.plot = plot;
	}
	
	@Override
	public void update() {
		setFill(plot.getColor());
		setOpacity(0.3);
	}

	@Override
	public void preLayout() {
		getPoints().clear();
		final Pane area = plot.getChart().getArea();
		getPoints().addAll(0d, area.getHeight());
	}

	@Override
	public void postLayout() {
		if (2 <= getPoints().size()) {
			final Pane area = plot.getChart().getArea();
			final double lastX = getPoints().get(getPoints().size() - 2);
			getPoints().addAll(lastX, area.getHeight());
		}
	}
}

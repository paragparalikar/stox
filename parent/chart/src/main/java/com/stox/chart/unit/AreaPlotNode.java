package com.stox.chart.unit;

import javafx.scene.shape.Polygon;

import com.stox.chart.plot.Plot;

public class AreaPlotNode extends Polygon {

	private final Plot<?> plot;

	public AreaPlotNode(final Plot<?> plot) {
		this.plot = plot;
		setFill(plot.getColor());
		setOpacity(0.3);
	}

}

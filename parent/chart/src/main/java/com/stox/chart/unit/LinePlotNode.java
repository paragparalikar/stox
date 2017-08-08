package com.stox.chart.unit;

import javafx.scene.shape.Polyline;

import com.stox.chart.plot.Plot;

public class LinePlotNode extends Polyline {

	private final Plot<?> plot;

	public LinePlotNode(final Plot<?> plot) {
		this.plot = plot;
		setStroke(plot.getColor());
		setStrokeWidth(2);
	}

}

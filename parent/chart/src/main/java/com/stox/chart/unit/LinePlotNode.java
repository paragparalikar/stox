package com.stox.chart.unit;

import javafx.scene.shape.Polyline;

import com.stox.chart.plot.Plot;

public class LinePlotNode extends Polyline implements PlotNode {

	private final Plot<?> plot;

	public LinePlotNode(final Plot<?> plot) {
		this.plot = plot;
		setStroke(plot.getColor());
		setStrokeWidth(2);
	}

	@Override
	public void preLayout() {
		getPoints().clear();
	}

	@Override
	public void postLayout() {
		// TODO Auto-generated method stub

	}

}

package com.stox.chart.unit;

import javafx.scene.shape.Line;

import com.stox.chart.plot.Plot;
import com.stox.core.intf.Range;

public class LineUnit<M extends Range> extends Unit<M> {

	private final Line line = new Line();

	public LineUnit(int index, M model, Plot<M> plot) {
		super(index, model, plot);
		getChildren().add(line);
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		if (getIndex() < getPlot().getModels().size() - 1) {
			final M previous = getPlot().getModels().get(getIndex() + 1);
			line.setStartX(x);
			line.setEndX(x + width);
			final M current = getModel();
			line.setStartY(getDisplayPosition(previous.getValue()));
			line.setEndY(getDisplayPosition(current.getValue()));
		}
	}

}

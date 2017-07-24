package com.stox.chart.unit;

import javafx.scene.shape.Line;

import com.stox.chart.plot.Plot;
import com.stox.core.model.Bar;

public class LinePriceUnit extends PriceUnit {

	private final Line line = new Line();

	public LinePriceUnit(final int index, final Bar model, final Plot<Bar> plot) {
		super(index, model, plot);
		getChildren().add(line);
	}

	@Override
	public void layoutChartChildren(final double x, final double width) {
		if (getIndex() < getPlot().getModels().size() - 1) {
			final Bar previous = getPlot().getModels().get(getIndex() + 1);
			line.setStartX(x);
			line.setEndX(x + width);
			final Bar bar = getModel();
			line.setStartY(getDisplayPosition(previous.getClose()));
			line.setEndY(getDisplayPosition(bar.getClose()));
		}
	}

}

package com.stox.chart.unit;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

import com.stox.chart.plot.Plot;
import com.stox.core.intf.Range;

public class AreaUnit<M extends Range> extends Unit<M> {

	private final Polygon polygon = new Polygon();

	public AreaUnit(int index, M model, Plot<M> plot) {
		super(index, model, plot);
		getChildren().add(polygon);
		polygon.setFill(plot.getColor());
		polygon.setOpacity(0.3);
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Plot<M> plot = getPlot();
		if (getIndex() < plot.getModels().size() - 1) {
			final M previous = plot.getModels().get(getIndex() + 1);
			final M current = getModel();
			final Pane area = plot.getChart().getArea();
			polygon.getPoints().setAll(x, getDisplayPosition(previous.getValue()), x, area.getHeight(), x + width, area.getHeight(), x + width,
					getDisplayPosition(current.getValue()));
		}
	}

}

package com.stox.chart.unit;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import com.stox.chart.plot.Plot;
import com.stox.core.intf.Range;
import com.stox.core.ui.util.UiUtil;

public class BarUnit<M extends Range> extends Unit<M> {

	private final Region region = new Region();

	public BarUnit(int index, M model, Plot<M> plot) {
		super(index, model, plot);
		getChildren().add(region);
		region.setStyle("-fx-background-color:" + UiUtil.web(plot.getColor()) + ";");
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Plot<M> plot = getPlot();
		final double y = getDisplayPosition(getModel().getValue());
		final Pane area = plot.getChart().getArea();
		region.resizeRelocate(x, y, width + 0.75 /* why 0.75 ? */, area.getHeight() - y);
	}

}

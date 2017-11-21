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
		final M model = getModel();
		if(null != model) {
			final Plot<M> plot = getPlot();
			final Pane area = plot.getChart().getArea();
			final double value = model.getValue();
			final double y = getDisplayPosition(value);
			if(0 > plot.getMin()) { // This is a histogram
				final double zeroY = getDisplayPosition(0);
				region.resizeRelocate(x, Math.min(y, zeroY), width + 0.75 /* why 0.75 ? */, Math.abs(y - zeroY));
			}else {
				region.resizeRelocate(x, y, width + 0.75 /* why 0.75 ? */, area.getHeight() - y);
			}
		}
	}

}

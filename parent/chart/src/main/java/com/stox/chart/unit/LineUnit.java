package com.stox.chart.unit;

import com.stox.chart.plot.Plot;
import com.stox.core.intf.Range;

public class LineUnit<M extends Range> extends Unit<M> {

	public LineUnit(int index, M model, Plot<M> plot) {
		super(index, model, plot);
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Plot<M> plot = getPlot();
		final PlotNode plotNode = plot.getPlotNodeProperty().get();
		if (getIndex() < plot.getModels().size() - 1) {
			final M current = getModel();
			final M previous = plot.getModels().get(getIndex() + 1);
			if(null != current && null != previous) {
				plotNode.getPoints().addAll(x, getDisplayPosition(previous.getValue()), x + width, getDisplayPosition(current.getValue()));
			}
		}
	}

}

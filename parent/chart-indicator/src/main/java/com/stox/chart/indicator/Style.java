package com.stox.chart.indicator;

import javafx.scene.Node;

public interface Style {

	public Node getNode();
	
	public void apply(final IndicatorPlot<?> plot);
	
}

package com.stox.chart.unit;

import javafx.collections.ObservableList;

public interface PlotNode {

	void preLayout();

	void postLayout();

	ObservableList<Double> getPoints();

}

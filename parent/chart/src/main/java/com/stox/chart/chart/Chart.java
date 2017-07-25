package com.stox.chart.chart;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.axis.ValueAxis;
import com.stox.chart.plot.Plot;
import com.stox.chart.view.ChartView;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chartView", "plots" })
public class Chart extends BorderPane {

	private final ChartView chartView;
	private final Pane area = new Pane();
	private final ValueAxis valueAxis = new ValueAxis(this);
	private final ObservableList<Plot<?>> plots = FXCollections.observableArrayList();

	public Chart(final ChartView chartView) {
		this.chartView = chartView;
		setCenter(area);
		setRight(valueAxis);
		plots.addListener((ListChangeListener<Plot<?>>) (change) -> {
			if (change.wasAdded() && Collections.disjoint(getChildren(), change.getAddedSubList())) {
				area.getChildren().addAll(change.getAddedSubList());
			}
			if (change.wasRemoved()) {
				area.getChildren().removeAll(change.getRemoved());
			}
		});
	}

	public void setDirty() {
		plots.forEach(Plot::setDirty);
	}

	public void update() {
		plots.forEach(Plot::update);
	}

}

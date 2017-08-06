package com.stox.chart.chart;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

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
import com.stox.core.ui.util.UiUtil;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chartView", "plots" })
public class Chart extends BorderPane {

	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	private final ChartView chartView;
	private final Pane area = UiUtil.classes(new Pane(), "content-area");
	private final ValueAxis valueAxis = new ValueAxis(this);
	private final ObservableList<Plot<?>> plots = FXCollections.observableArrayList();

	public Chart(final ChartView chartView) {
		this.chartView = chartView;
		UiUtil.classes(this, "chart");
		setCenter(area);
		setRight(valueAxis);
		plots.addListener((ListChangeListener<Plot<?>>) (change) -> {
			while (change.next()) {
				if (change.wasAdded() && Collections.disjoint(getChildren(), change.getAddedSubList())) {
					final List<? extends Plot<?>> addedPlots = change.getAddedSubList();
					area.getChildren().addAll(addedPlots);
					IntStream.range(0, addedPlots.size()).forEachOrdered(index -> {
						final Plot<?> plot = addedPlots.get(index);
						plot.setColor(chartView.getPlotColors().get(index));
					});
				}
				if (change.wasRemoved()) {
					area.getChildren().removeAll(change.getRemoved());
				}
			}
		});
	}

	public void setDirty() {
		plots.forEach(Plot::setDirty);
	}

	public void update() {
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		plots.forEach(Plot::update);
	}

}

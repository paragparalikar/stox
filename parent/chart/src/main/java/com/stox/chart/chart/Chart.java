package com.stox.chart.chart;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.axis.ValueAxis;
import com.stox.chart.drawing.Drawing;
import com.stox.chart.plot.Plot;
import com.stox.chart.view.ChartView;
import com.stox.core.ui.util.UiUtil;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chartView", "plots" })
public class Chart extends BorderPane {

	private final ChartView chartView;
	private final ValueAxis valueAxis;
	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	private final Pane area = UiUtil.classes(new Pane(), "content-area");
	private final VBox chartInfoPane = UiUtil.classes(new VBox(), "chart-info-pane");
	private final Pane glassPane = new Pane(chartInfoPane);
	private final ObservableList<Plot<?>> plots = FXCollections.observableArrayList();
	private final ObservableList<Drawing<?>> drawings = FXCollections.observableArrayList();

	public Chart(final ChartView chartView) {
		this.chartView = chartView;
		valueAxis = new ValueAxis(this);
		UiUtil.classes(this, "chart");
		setCenter(new StackPane(area, glassPane));
		chartInfoPane.setBackground(null);
		chartInfoPane.setPickOnBounds(false);
		setRight(valueAxis);
		drawings.addListener((ListChangeListener<Drawing<?>>) (change) -> {
			if (Platform.isFxApplicationThread()) {
				handleDrawingsChanged(change);
			} else {
				Platform.runLater(() -> {
					handleDrawingsChanged(change);
				});
			}
		});
		plots.addListener((ListChangeListener<Plot<?>>) (change) -> {
			while (change.next()) {
				if (change.wasAdded() && Collections.disjoint(getChildren(), change.getAddedSubList())) {
					final List<? extends Plot<?>> addedPlots = change.getAddedSubList();
					area.getChildren().addAll(addedPlots);
					IntStream.range(0, addedPlots.size()).forEachOrdered(index -> {
						final Plot<?> plot = addedPlots.get(index);
						plot.setColor(chartView.getPlotColors().get(index));
						chartInfoPane.getChildren().add(plot.getPlotInfoPane());
					});
				}
				if (change.wasRemoved()) {
					area.getChildren().removeAll(change.getRemoved());
					change.getRemoved().forEach(plot -> {
						chartInfoPane.getChildren().remove(plot.getPlotInfoPane());
					});
				}
			}
		});
	}

	private void handleDrawingsChanged(ListChangeListener.Change<? extends Drawing<?>> change) {
		while (change.next()) {
			if (change.wasRemoved()) {
				glassPane.getChildren().removeAll(change.getRemoved());
			}
			if (change.wasAdded()) {
				glassPane.getChildren().addAll(change.getAddedSubList());
			}
		}
	}

	public void setDirty() {
		valueAxis.setDirty();
		plots.forEach(Plot::setDirty);
		drawings.forEach(Drawing::setDirty);
	}

	public void reset() {
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
	}

	public void setMin(final double min) {
		this.min = min;
		valueAxis.setDirty();
	}

	public void setMax(final double max) {
		this.max = max;
		valueAxis.setDirty();
	}

	public void update() {
		reset();
		plots.forEach(Plot::update);
		setDirty();
	}

}

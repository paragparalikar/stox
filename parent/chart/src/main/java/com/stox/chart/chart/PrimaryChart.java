package com.stox.chart.chart;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.EqualsAndHashCode;
import lombok.Value;

import com.stox.chart.plot.Plot;
import com.stox.chart.plot.PricePlot;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.view.ChartView;
import com.stox.chart.widget.Grid;

@Value
@EqualsAndHashCode(callSuper = false, exclude = { "primaryPricePlot", "pricePlots" })
public class PrimaryChart extends Chart {

	private final Grid grid;
	private final PrimaryPricePlot primaryPricePlot;
	private final ObservableList<PricePlot> pricePlots = FXCollections.observableArrayList();

	public PrimaryChart(final ChartView chartView) {
		super(chartView);
		grid = new Grid(this);
		primaryPricePlot = new PrimaryPricePlot(this);

		getArea().getChildren().addAll(grid, primaryPricePlot);
		getChartInfoPane().getChildren().add(primaryPricePlot.getPlotInfoPane());

		pricePlots.addListener((ListChangeListener<PricePlot>) (change) -> {
			while (change.next()) {
				if (change.wasRemoved()) {
					change.getAddedSubList().forEach(plot -> getChartInfoPane().getChildren().add(plot.getPlotInfoPane()));
				}
				if (change.wasAdded()) {
					change.getRemoved().forEach(plot -> getChartInfoPane().getChildren().remove(plot.getPlotInfoPane()));
				}
			}
		});
	}

	public PrimaryPricePlot getPrimaryPricePlot() {
		return primaryPricePlot;
	}

	@Override
	public void setDirty() {
		primaryPricePlot.setDirty();
		pricePlots.forEach(Plot::setDirty);
		super.setDirty();
	}

	@Override
	public void update() {
		reset();
		primaryPricePlot.update();
		pricePlots.forEach(Plot::update);
		getPlots().forEach(Plot::update);
		setDirty();
	}

	@Override
	public String toString() {
		return "";
	}

}

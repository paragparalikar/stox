package com.stox.chart.chart;

import javafx.collections.FXCollections;
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

	private final Grid grid = new Grid(this);
	private final PrimaryPricePlot primaryPricePlot = new PrimaryPricePlot(this);
	private final ObservableList<PricePlot> pricePlots = FXCollections.observableArrayList();

	public PrimaryChart(final ChartView chartView) {
		super(chartView);
		getArea().getChildren().addAll(grid, primaryPricePlot);
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

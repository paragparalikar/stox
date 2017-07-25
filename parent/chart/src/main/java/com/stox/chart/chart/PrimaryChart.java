package com.stox.chart.chart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.EqualsAndHashCode;
import lombok.Value;

import com.stox.chart.plot.Plot;
import com.stox.chart.plot.PricePlot;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.view.ChartView;

@Value
@EqualsAndHashCode(callSuper = false, exclude = { "primaryPricePlot", "pricePlots" })
public class PrimaryChart extends Chart {

	private final PrimaryPricePlot primaryPricePlot = new PrimaryPricePlot(this);
	private final ObservableList<PricePlot> pricePlots = FXCollections.observableArrayList();

	public PrimaryChart(final ChartView chartView) {
		super(chartView);
		getArea().getChildren().add(primaryPricePlot);
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
	public String toString() {
		return "";
	}

}

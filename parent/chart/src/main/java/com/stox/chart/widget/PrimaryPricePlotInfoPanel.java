package com.stox.chart.widget;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import com.stox.chart.chart.Chart;
import com.stox.chart.plot.Plot;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.unit.Unit;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;

public class PrimaryPricePlotInfoPanel extends PricePlotInfoPanel implements EventHandler<MouseEvent> {

	private final PrimaryPricePlot primaryPricePlot;
	private final BarInfoPanel barInfoPanel = new BarInfoPanel();

	public PrimaryPricePlotInfoPanel(PrimaryPricePlot primaryPricePlot) {
		super(primaryPricePlot);
		this.primaryPricePlot = primaryPricePlot;
		getChildren().setAll(barInfoPanel);
	}

	public void attach() {
		primaryPricePlot.getChart().getChartView().getSplitPane().addEventHandler(MouseEvent.MOUSE_MOVED, this);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void handle(MouseEvent event) {
		final ChartView chartView = primaryPricePlot.getChart().getChartView();
		final Unit<Bar> priceUnit = primaryPricePlot.getUnitAt(event.getScreenX(), event.getScreenY());
		barInfoPanel.set(null == priceUnit ? null : priceUnit.getModel());

		for (final Chart chart : chartView.getCharts()) {
			for (final Plot<?> plot : chart.getPlots()) {
				if (plot != primaryPricePlot) {
					final Unit<?> unit = plot.getUnitAt(event.getScreenX(), event.getScreenY());
					final PlotInfoPanel plotInfoPane = plot.getPlotInfoPane();
					plotInfoPane.setModel(null == unit ? null : unit.getModel());
				}
			}
		}
	}

}

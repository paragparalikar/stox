package com.stox.chart.indicator;

import java.util.Collection;

import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.modal.Modal;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class IndicatorModal extends Modal {

	private final ChartView chartView;
	private final ListView<ChartIndicator> listView = new ListView<>();

	public IndicatorModal(final ChartView chartView) {
		this.chartView = chartView;
		setTitle("Indicators");
		setContent(listView);
		listView.setCellFactory(new Callback<ListView<ChartIndicator>, ListCell<ChartIndicator>>() {
			@Override
			public ListCell<ChartIndicator> call(ListView<ChartIndicator> param) {
				return new ListCell<ChartIndicator>() {
					ChartIndicator item;
					final Label label = new Label();
					final Button addButton = UiUtil.classes(new Button(Icon.PLUS), "icon");
					final HBox container = new HBox(label, addButton);

					{
						addButton.addEventHandler(ActionEvent.ACTION, event -> {
							addIndicator(item);
							stop();
						});
					}

					@Override
					protected void updateItem(ChartIndicator item, boolean empty) {
						super.updateItem(item, empty);
						if (null != item && !empty) {
							this.item = item;
							label.setText(item.getName());
							setGraphic(container);
						} else {
							setGraphic(null);
						}
					}
				};
			}
		});
	}

	public void setItems(Collection<ChartIndicator> indicators) {
		listView.getItems().addAll(indicators);
	}

	private void addIndicator(ChartIndicator indicator) {
		final Object config = indicator.buildDefaultConfig();
		// TODO show another modal for user to edit config
		switch (indicator.getUnderlay(config)) {
		case NONE:
			final Chart chart = new Chart(chartView);
			final IndicatorPlot plot = new IndicatorPlot(chart, indicator);
			chart.getPlots().add(plot);
			chartView.getCharts().add(chart);
			plot.load();
			break;
		case PRICE:
			final Chart primaryChart = chartView.getPrimaryChart();
			final IndicatorPlot plot1 = new IndicatorPlot(primaryChart, indicator);
			primaryChart.getPlots().add(plot1);
			plot1.load();
			break;
		case VOLUME:
			final Chart volumeChart = chartView.getVolumePlot().getChart();
			if (null != volumeChart) {
				final IndicatorPlot plot2 = new IndicatorPlot(volumeChart, indicator);
				volumeChart.getPlots().add(plot2);
				plot2.load();
			}
			break;
		}
	}

}

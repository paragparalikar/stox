package com.stox.screen;

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

@SuppressWarnings({"rawtypes"})
public class ScreenModal extends Modal {

	private final ChartView chartView;
	private final ListView<Screen> listView = new ListView<>();
	
	public ScreenModal(final ChartView chartView) {
		this.chartView = chartView;
		getStyleClass().add("primary");
		setTitle("Screens");
		setContent(listView);
		listView.setCellFactory(new Callback<ListView<Screen>, ListCell<Screen>>() {
			@Override
			public ListCell<Screen> call(ListView<Screen> param) {
				return new ListCell<Screen>() {
					Screen item;
					final Label label = new Label();
					final Button addButton = UiUtil.classes(new Button(Icon.PLUS), "icon", "primary");
					final HBox container = new HBox(label, UiUtil.spacer(), addButton);

					{
						addButton.addEventHandler(ActionEvent.ACTION, event -> {
							addScreen(item);
							stop();
						});
					}

					@Override
					protected void updateItem(Screen item, boolean empty) {
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
	
	public void setItems(Collection<Screen> screen) {
		listView.getItems().addAll(screen);
	}
	
	private void addScreen(Screen screen) {
		final Chart primaryChart = chartView.getPrimaryChart();
		final ScreenPlot plot1 = new ScreenPlot(primaryChart, screen);
		primaryChart.getPlots().add(plot1);
		plot1.load();
	}
	
}

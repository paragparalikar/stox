package com.stox.chart.drawing;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.Icon;

public class ClearDrawingsButton extends Button implements EventHandler<ActionEvent> {

	private final ChartingTool chartingButtonBox;

	public ClearDrawingsButton(final ChartingTool chartingButtonBox) {
		super(Icon.CROSS);
		getStyleClass().add("icon");
		this.chartingButtonBox = chartingButtonBox;
		addEventHandler(ActionEvent.ACTION, this);
	}

	@Override
	public void handle(ActionEvent event) {
		final ChartView chartView = chartingButtonBox.getChartView();
		if (null != chartView) {
			chartView.getPrimaryChart().getDrawings().clear();
			for (final Chart chart : chartView.getCharts()) {
				chart.getDrawings().clear();
			}
		}
	}

}

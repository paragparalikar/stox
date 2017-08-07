package com.stox.chart.widget;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import org.springframework.stereotype.Component;

import com.stox.chart.view.ChartView;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

@Component
public class ZoomTool extends ChartingTool {

	private final Button in = UiUtil.classes(new Button(Icon.PLUS), "icon");
	private final Button out = UiUtil.classes(new Button(Icon.MINUS), "icon");
	private final HBox container = UiUtil.box(new HBox(in, out));

	public ZoomTool() {
		in.setTooltip(new Tooltip("Zoom In")); // TODO I18N
		in.addEventHandler(ActionEvent.ACTION, event -> {
			final ChartView chartView = getChartView();
			if (null != chartView) {
				chartView.getDateAxis().zoomIn();
				chartView.update();
			}
		});
		out.setTooltip(new Tooltip("Zoom Out")); // TODO I18N
		out.addEventHandler(ActionEvent.ACTION, event -> {
			final ChartView chartView = getChartView();
			if (null != chartView) {
				chartView.getDateAxis().zoomOut();
				chartView.update();
			}
		});
	}

	@Override
	public void onChartViewSelected(ChartView chartView) {

	}

	@Override
	public Node getNode() {
		return container;
	}

}

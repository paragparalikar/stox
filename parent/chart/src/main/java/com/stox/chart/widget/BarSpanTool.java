package com.stox.chart.widget;

import java.util.Arrays;
import java.util.Date;

import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import org.springframework.stereotype.Component;

import com.stox.chart.util.ChartUtil;
import com.stox.chart.view.ChartView;
import com.stox.core.model.BarSpan;
import com.stox.core.ui.util.UiUtil;

@Component
public class BarSpanTool extends ChartingTool {

	private final HBox container = new HBox();
	private final ToggleGroup toggleGroup = new ToggleGroup();

	public BarSpanTool() {
		Arrays.asList(BarSpan.values()).forEach(barSpan -> {
			final ToggleButton button = UiUtil.classes(new ToggleButton(barSpan.getShortName()), "primary");
			button.setToggleGroup(toggleGroup);
			button.setUserData(barSpan);
			button.setTooltip(new Tooltip(barSpan.getName()));
			container.getChildren().add(button);
		});
		UiUtil.box(container);
		toggleGroup.selectedToggleProperty().addListener((observable, old, toggle) -> {
			final ChartView chartView = getChartView();
			if (null != chartView) {
				final BarSpan barSpan = (BarSpan) toggle.getUserData();
				chartView.setBarSpan(barSpan);
				final Date to = null == chartView.getTo() ? new Date() : chartView.getTo();
				final Date from = ChartUtil.getFrom(to, barSpan);
				chartView.setFrom(from);
				chartView.getPrimaryChart().getPrimaryPricePlot().load();
			}
		});
	}

	@Override
	public Node getNode() {
		return container;
	}

	@Override
	public void onChartViewSelected(ChartView chartView) {
		if (null != chartView) {
			for (final Toggle toggle : toggleGroup.getToggles()) {
				if (toggle.getUserData().equals(chartView.getBarSpan())) {
					toggleGroup.selectToggle(toggle);
					break;
				}
			}
		}
	}
}

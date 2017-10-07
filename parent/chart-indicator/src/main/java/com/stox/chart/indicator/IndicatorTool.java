package com.stox.chart.indicator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.stox.chart.view.ChartView;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;

@Component
public class IndicatorTool extends ChartingTool {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private final Button button = UiUtil.classes(new Button(Icon.LINECHART), "icon");
	
	public IndicatorTool() {
		button.addEventHandler(ActionEvent.ACTION, event -> {
			final ChartView chartView = getChartView();
			if(null != chartView) {
				final IndicatorModal modal = new IndicatorModal(chartView);
				modal.setItems(applicationContext.getBeansOfType(ChartIndicator.class).values());
				modal.show();
			}
		});
	}

	@Override
	public Node getNode() {
		return button;
	}

	@Override
	public void onChartViewSelected(ChartView chartView) {

	}

}

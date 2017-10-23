package com.stox.screen;

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
public class ScreenTool extends ChartingTool {

	@Autowired
	private ApplicationContext applicationContext;
	
	private final Button button = UiUtil.classes(new Button(Icon.FILTER), "icon");
	
	public ScreenTool() {
		button.addEventHandler(ActionEvent.ACTION, event -> {
			final ChartView chartView = getChartView();
			if(null != chartView) {
				final ScreenModal modal = new ScreenModal(chartView);
				modal.setItems(applicationContext.getBeansOfType(Screen.class).values());
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

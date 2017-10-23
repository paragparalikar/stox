package com.stox.screen;

import com.stox.chart.plot.Plot;
import com.stox.chart.widget.PlotInfoPanel;
import com.stox.core.model.Bar;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ScreenPlotInfoPanel extends PlotInfoPanel<Bar> {

	private final Button editButton = UiUtil.classes(new Button(Icon.PENCIL), "icon", "warning");

	public ScreenPlotInfoPanel(Plot<Bar> plot) {
		super(plot);
		getChildren().add(1, editButton);
		editButton.addEventHandler(ActionEvent.ACTION, event -> edit());
	}

	private void edit() {
		final ScreenPlot plot = (ScreenPlot) getPlot();
		final Object config = plot.getConfig();
		final ScreenConfigEditorModal modal = new ScreenConfigEditorModal(plot);
		modal.onHide(() -> setName(config.toString()));
		modal.show();
	}

}

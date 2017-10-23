package com.stox.chart.indicator;

import com.stox.chart.widget.PlotInfoPanel;
import com.stox.core.intf.Range;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class IndicatorPlotInfoPanel<M extends Range> extends PlotInfoPanel<M> {
	
	private final Button editButton = UiUtil.classes(new Button(Icon.PENCIL), "icon","warning");

	public IndicatorPlotInfoPanel(IndicatorPlot<M> plot) {
		super(plot);
		getChildren().add(1, editButton);
		editButton.addEventHandler(ActionEvent.ACTION, event -> edit());
	}
	
	private void edit() {
		final IndicatorPlot<M> plot = (IndicatorPlot<M>)getPlot();
		final Object config = plot.getConfig();
		final IndicatorConfigEditorModal modal = new IndicatorConfigEditorModal(plot);
		modal.onHide(() -> setName(config.toString()));
		modal.show();
	}
	
}

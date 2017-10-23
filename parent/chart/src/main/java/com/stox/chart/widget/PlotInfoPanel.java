package com.stox.chart.widget;

import com.stox.chart.chart.Chart;
import com.stox.chart.plot.Plot;
import com.stox.core.intf.Range;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.Constant;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class PlotInfoPanel<M extends Range> extends HBox {

	private final Plot<M> plot;

	private final Label nameLabel = new Label();
	private final Label valueLabel = new Label();
	private final Button removeButton = UiUtil.classes(new Button(Icon.CROSS), "icon", "danger");

	public PlotInfoPanel(final Plot<M> plot) {
		this.plot = plot;
		UiUtil.classes(this, "plot-info-pane");
		getChildren().addAll(removeButton, nameLabel, valueLabel);
		removeButton.addEventHandler(ActionEvent.ACTION, event -> removePlot());
	}

	public Plot<M> getPlot() {
		return plot;
	}

	protected void removePlot() {
		final Chart chart = plot.getChart();
		chart.getPlots().remove(plot);
	}

	public void setName(final String name) {
		nameLabel.setText(name);
	}

	public void setModel(final M model) {
		valueLabel.setText(null == model ? null : Constant.currencyFormat.format(model.getValue()));
	}
}

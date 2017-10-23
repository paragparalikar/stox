package com.stox.screen.unit;

import com.stox.chart.plot.Plot;
import com.stox.chart.unit.Unit;
import com.stox.core.model.Bar;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

import javafx.scene.control.Label;

public class BullishScreenUnit extends Unit<Bar> {

	private final Label label = UiUtil.classes(new Label(Icon.ARROW_UP), "icon");

	public BullishScreenUnit(int index, Bar model, Plot<Bar> plot) {
		super(index, model, plot);
		getChildren().add(label);
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		width *= 2;
		final Plot<Bar> plot = getPlot();
		final Plot<Bar> pricePlot = plot.getChart().getChartView().getPrimaryChart().getPrimaryPricePlot();
		final double low = getModel().getLow();
		label.setStyle("-fx-text-fill: background-success;" + "-fx-background-color: rgba(0, 0, 0, 0);"
				+ "-fx-padding:0;" + "-fx-font-size:" + width + "px;");
		label.resizeRelocate(x - width / 4,
				plot.getChart().getValueAxis().getDisplayPosition(low, pricePlot.getMin(), pricePlot.getMax()) + width,
				width, width);
	}


}

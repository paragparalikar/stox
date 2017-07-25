package com.stox.chart.unit;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import com.stox.chart.plot.Plot;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;
import com.stox.core.ui.util.UiUtil;

public class CandlePriceUnit extends PriceUnit {

	private final Line line = new Line();
	private final Region body = new Region();

	public CandlePriceUnit(int index, Bar model, Plot<Bar> plot) {
		super(index, model, plot);
		getChildren().addAll(line, body);
		line.endXProperty().bindBidirectional(line.startXProperty());
		final ChartView chartView = plot.getChart().getChartView();
		final Color color = model.getOpen() < model.getClose() ? chartView.getUpBarColor() : chartView.getDownBarColor();
		body.setStyle("-fx-background-color:" + UiUtil.web(color) + ";");
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Bar bar = getModel();
		line.setStrokeWidth(Math.max(1, width / 10));
		line.setStartX(((int) (x + width / 2)) + 0.5);
		line.setStartY(getDisplayPosition(bar.getHigh()));
		line.setEndY(getDisplayPosition(bar.getLow()));
		final double open = getDisplayPosition(bar.getOpen());
		final double close = getDisplayPosition(bar.getClose());
		body.resizeRelocate(x + width / 8, Math.min(open, close), width * 3 / 4, Math.abs(open - close));
	}

}

package com.stox.chart.unit;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import com.stox.chart.plot.Plot;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;

public class HlcPriceUnit extends PriceUnit {

	private final Line line = new Line();
	private final Line close = new Line();

	public HlcPriceUnit(int index, Bar model, Plot<Bar> plot) {
		super(index, model, plot);
		getChildren().addAll(line, close);

		line.endXProperty().bind(line.startXProperty());
		close.startXProperty().bind(line.startXProperty());
		close.endYProperty().bind(close.startYProperty());
		close.strokeProperty().bind(line.strokeProperty());
		close.strokeWidthProperty().bind(line.strokeWidthProperty());

		final ChartView chartView = plot.getChart().getChartView();
		final Color color = model.getPreviousClose() < model.getClose() ? chartView.getUpBarColor() : chartView.getDownBarColor();
		line.setStroke(color);
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Bar bar = getModel();
		line.setStartX(x + width / 4);
		line.setStrokeWidth(Math.max(2, width / 10));
		line.setStartY(getDisplayPosition(bar.getHigh()));
		line.setEndY(getDisplayPosition(bar.getLow()));
		close.setEndX(x + width * 3 / 4);
		close.setStartY(getDisplayPosition(bar.getClose()));
	}

}

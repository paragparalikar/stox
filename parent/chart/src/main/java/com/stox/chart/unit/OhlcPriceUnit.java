package com.stox.chart.unit;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import com.stox.chart.plot.Plot;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;

public class OhlcPriceUnit extends PriceUnit {

	private final Line line = new Line();
	private final Line open = new Line();
	private final Line close = new Line();

	public OhlcPriceUnit(int index, Bar model, Plot<Bar> plot) {
		super(index, model, plot);
		getChildren().addAll(line, open, close);

		line.endXProperty().bind(line.startXProperty());
		open.endXProperty().bind(line.startXProperty());
		open.endYProperty().bind(open.startYProperty());
		open.strokeProperty().bind(line.strokeProperty());
		open.strokeWidthProperty().bind(line.strokeWidthProperty());
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
		line.setStartX(x + width / 2);
		line.setStrokeWidth(Math.max(2, width / 10));
		line.setStartY(getDisplayPosition(bar.getHigh()));
		line.setEndY(getDisplayPosition(bar.getLow()));
		open.setStartX(x + width / 4);
		open.setStartY(getDisplayPosition(bar.getOpen()));
		close.setEndX(x + width * 3 / 4);
		close.setStartY(getDisplayPosition(bar.getClose()));
	}

}

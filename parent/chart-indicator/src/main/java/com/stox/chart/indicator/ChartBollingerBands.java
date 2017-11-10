package com.stox.chart.indicator;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.chart.axis.ValueAxis;
import com.stox.chart.plot.Plot;
import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.Unit;
import com.stox.chart.unit.UnitType;
import com.stox.indicator.BollingerBands;
import com.stox.indicator.BollingerBands.Bollinger;
import com.stox.indicator.BollingerBands.Config;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

@Lazy
@Component
public class ChartBollingerBands extends BollingerBands
		implements ChartIndicator<Config, Bollinger>, UnitFactory<Bollinger> {

	@Override
	public Underlay getUnderlay(Config config) {
		return Underlay.PRICE;
	}

	@Override
	public UnitType getUnitType(Config config) {
		return null;
	}

	@Override
	public Style getStyle() {
		return null;
	}

	@Override
	public Unit<Bollinger> create(int index, Bollinger model, Plot<Bollinger> plot) {
		return new BollingerUnit(index, model, plot);
	}

}

class BollingerUnit extends Unit<Bollinger> {

	private final Line averageLine = new Line();
	private final Polygon area = new Polygon();

	public BollingerUnit(int index, Bollinger model, Plot<Bollinger> plot) {
		super(index, model, plot);
		getChildren().addAll(area, averageLine);
		averageLine.setStroke(Color.GRAY);
		area.setFill(Color.rgb(100, 20, 20, 0.1));
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final int index = getIndex();
		final Plot<Bollinger> plot = getPlot();
		if (index < plot.getModels().size() - 1) {
			final Bollinger currentModel = getModel();
			final Bollinger previousModel = plot.getModels().get(index + 1);
			if(null != previousModel && null != currentModel) {
				final double min = plot.getMin();
				final double max = plot.getMax();
				final ValueAxis valueAxis = plot.getChart().getValueAxis();
				area.getPoints().clear();
				area.getPoints().addAll(x, valueAxis.getDisplayPosition(previousModel.getHigh(), min, max));
				area.getPoints().addAll(x + width, valueAxis.getDisplayPosition(currentModel.getHigh(), min, max));
				area.getPoints().addAll(x + width, valueAxis.getDisplayPosition(currentModel.getLow(), min, max));
				area.getPoints().addAll(x, valueAxis.getDisplayPosition(previousModel.getLow(), min, max));
				averageLine.setStartX(x);
				averageLine.setEndX(x + width);
				averageLine.setStartY(valueAxis.getDisplayPosition(previousModel.getAverage(), min, max));
				averageLine.setEndY(valueAxis.getDisplayPosition(currentModel.getAverage(), min, max));
			}
		}
	}

}
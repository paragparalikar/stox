package com.stox.chart.indicator;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.chart.axis.DateAxis;
import com.stox.chart.axis.ValueAxis;
import com.stox.chart.plot.Plot;
import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.Unit;
import com.stox.chart.unit.UnitType;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;
import com.stox.core.model.Swing;
import com.stox.indicator.Ord;
import com.stox.indicator.Ord.Config;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

@Lazy
@Component
public class ChartOrd extends Ord implements ChartIndicator<Config, Swing>, LayoutDelegate<Swing>, UnitFactory<Swing> {

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
	public Unit<Swing> create(int index, Swing model, Plot<Swing> plot) {
		return new OrdUnit(index, model, plot);
	}

	@Override
	public void layout(IndicatorPlot<Swing> plot) {
		final ChartView chartView = plot.getChart().getChartView();
		final DateAxis dateAxis = chartView.getDateAxis();
		final int minIndex = dateAxis.getUpperBoundIndex();
		final int maxIndex = dateAxis.getLowerBoundIndex();
		final List<Bar> bars = chartView.getPrimaryChart().getPrimaryPricePlot().getModels();
		final long maxDate = minIndex < bars.size() && minIndex >= 0 ? bars.get(minIndex).getDate().getTime() : Long.MAX_VALUE;
		final long minDate = maxIndex < bars.size() && maxIndex >= 0 ? bars.get(maxIndex).getDate().getTime() : Long.MIN_VALUE;
		plot.getUnits().forEach(unit -> {
			final Swing model = unit.getModel();
			final long startDate = model.getStart().getDate().getTime();
			final long endDate = model.getEnd().getDate().getTime();
			showUnit(unit, !(startDate > maxDate || endDate < minDate), dateAxis);
		});
	}
	
	private void showUnit(final Unit<Swing> unit, final boolean visible, final DateAxis dateAxis) {
		unit.setVisible(visible);
		if(visible) {
			final Swing model = unit.getModel();
			final double startX = dateAxis.getDisplayPosition(model.getStart().getDate().getTime());
			final double endX = dateAxis.getDisplayPosition(model.getEnd().getDate().getTime());
			unit.layoutChartChildren(startX, endX - startX);
		}
	}

}

class OrdUnit extends Unit<Swing> {

	private final Line line = new Line();

	public OrdUnit(int index, Swing model, Plot<Swing> plot) {
		super(index, model, plot);
		getChildren().add(line);
		line.setStroke(Color.BLACK);
	}

	@Override
	public void update() {
		super.update();
		
	}

	@Override
	public void layoutChartChildren(double x, double width) {
		final Plot<Swing> plot = getPlot();
		final Plot<Bar> primaryPlot = plot.getChart().getChartView().getPrimaryChart().getPrimaryPricePlot();
		final ValueAxis valueAxis = plot.getChart().getValueAxis();
		
		final Swing model = getModel();
		line.setStartX(x);
		line.setEndX(x+width);
		line.setStartY(valueAxis.getDisplayPosition(model.getStart().getClose(), primaryPlot.getMin(), primaryPlot.getMax()));
		line.setEndY(valueAxis.getDisplayPosition(model.getEnd().getClose(), primaryPlot.getMin(), primaryPlot.getMax()));
	}

}

package com.stox.chart.indicator;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.chart.axis.ValueAxis;
import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range.DoubleRange;
import com.stox.indicator.RelativeStrengthIndex;
import com.stox.indicator.RelativeStrengthIndex.Config;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

@Lazy
@Component
public class ChartRelativeStrengthIndex extends RelativeStrengthIndex implements ChartIndicator<Config, DoubleRange>{
	
	public static class RSIStyle extends Group implements Style{

		private double upperBand = 70;
		private double lowerBand = 30;
		private Color background = Color.rgb(0, 0, 200, 0.2);
		
		private final Rectangle backgroundRectangle = new Rectangle();
		
		public RSIStyle() {
			backgroundRectangle.setX(0);
			getChildren().addAll(backgroundRectangle);
		}
		
		@Override
		public Node getNode() {
			return this;
		}
		
		@Override
		public void apply(IndicatorPlot<?> plot) {	
			final double min = plot.getMin();
			final double max = plot.getMax();
			final ValueAxis valueAxis = plot.getChart().getValueAxis();
			final double upperBandY = valueAxis.getDisplayPosition(upperBand, min, max);
			final double lowerBandY = valueAxis.getDisplayPosition(lowerBand, min, max);
			backgroundRectangle.resizeRelocate(0, upperBandY, plot.getChart().getArea().getWidth(), lowerBandY - upperBandY);
			backgroundRectangle.setFill(background);
		}
	}
	
	private final Style style = new RSIStyle();

	@Override
	public Underlay getUnderlay(Config config) {
		return Underlay.NONE;
	}

	@Override
	public UnitType getUnitType(Config config) {
		return UnitType.LINE;
	}
	
	@Override
	public Style getStyle() {
		return style;
	}

}

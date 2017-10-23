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
		
		private final Line upperBandLine = new Line();
		private final Line lowerBandLine = new Line();
		private final Rectangle backgroundRectangle = new Rectangle();
		
		public RSIStyle() {
			
			upperBandLine.setOpacity(0.3);
			upperBandLine.setStroke(Color.GRAY);
			upperBandLine.getStrokeDashArray().addAll(25d, 10d);
			lowerBandLine.setOpacity(0.3);
			lowerBandLine.setStroke(Color.GRAY);
			lowerBandLine.getStrokeDashArray().addAll(25d, 10d);
			
			upperBandLine.setStartX(0);
			lowerBandLine.setStartX(0);
			backgroundRectangle.setX(0);
			upperBandLine.endYProperty().bind(upperBandLine.startYProperty());
			lowerBandLine.endYProperty().bind(lowerBandLine.startYProperty());
			lowerBandLine.endXProperty().bind(upperBandLine.endXProperty());
			backgroundRectangle.yProperty().bind(upperBandLine.startYProperty());
			backgroundRectangle.heightProperty().bind(lowerBandLine.startYProperty().subtract(upperBandLine.startYProperty()));
			backgroundRectangle.widthProperty().bind(upperBandLine.endXProperty());
			getChildren().addAll(upperBandLine, lowerBandLine, backgroundRectangle);
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
			upperBandLine.setEndX(plot.getChart().getArea().getWidth());
			upperBandLine.setStartY(valueAxis.getDisplayPosition(upperBand, min, max));
			lowerBandLine.setStartY(valueAxis.getDisplayPosition(lowerBand, min, max));
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

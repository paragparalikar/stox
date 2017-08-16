package com.stox.chart.drawing;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import com.stox.chart.chart.Chart;

public class ControlPoint extends Circle {

	private double x;
	private double y;
	private long date;
	private double value;
	private final Chart chart;

	private class ControlPointMouseHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(final MouseEvent event) {
			if (!event.isConsumed()) {
				if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
					x = event.getX();
					y = event.getY();
					event.consume();
				} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
					setCenterX(event.getX() + getCenterX() - x);
					setCenterY(event.getY() + getCenterY() - y);
					x = event.getX();
					y = event.getY();
					event.consume();
					update();
				}
			}
		}
	}

	public void update() {
		value = chart.getValueAxis().getValueForDisplay(getCenterY(), chart.getMin(), chart.getMax());
		date = chart.getChartView().getDateAxis().getValueForDisplay(getCenterX());
	}

	public ControlPoint(final Chart chart) {
		this.chart = chart;
		setRadius(5);
		setManaged(false);
		getStyleClass().add("control-point");

		final ControlPointMouseHandler mouseHandler = new ControlPointMouseHandler();
		addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseHandler);
	}

	public void layoutChartChildren() {
		setCenterX(chart.getChartView().getDateAxis().getDisplayPosition(date));
		setCenterY(chart.getValueAxis().getDisplayPosition(value, chart.getMin(), chart.getMax()));
	}

}

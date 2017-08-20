package com.stox.chart.drawing;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import lombok.Data;

import com.stox.chart.chart.Chart;

public class ControlPoint extends Circle {
	
	@Data
	public static class State {
		
		private long date;
		
		private double value;
		
		public void copy(final State state){
			this.date = state.date;
			this.value = state.value;
		}

	}

	private double x;
	private double y;
	private final Chart chart;
	private final State state = new State();

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
	
	public State getState() {
		return state;
	}

	public void update() {
		state.setValue(chart.getValueAxis().getValueForDisplay(getCenterY(), chart.getMin(), chart.getMax()));
		state.setDate(chart.getChartView().getDateAxis().getValueForDisplay(getCenterX()));
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
		setCenterX(chart.getChartView().getDateAxis().getDisplayPosition(state.getDate()));
		setCenterY(chart.getValueAxis().getDisplayPosition(state.getValue(), chart.getMin(), chart.getMax()));
	}

}

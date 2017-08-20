package com.stox.chart.drawing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stox.chart.chart.Chart;
import com.stox.chart.drawing.Drawing.State;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class Drawing<T extends State<T>> extends Group {
	
	public static interface State<T extends State<T>>{
		
		@JsonIgnore
		String getCode();
		
		void copy(T state);
		
	}
	
	private boolean dirty;
	private final Chart chart;

	private class MouseHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			if (MouseButton.SECONDARY.equals(event.getButton())) {
				chart.getDrawings().remove(Drawing.this);
			}
		}
	}

	public Drawing(final Chart chart) {
		this.chart = chart;
		getStyleClass().add("drawing");
		setManaged(false);
		addEventHandler(MouseEvent.MOUSE_PRESSED, new MouseHandler());
	}

	public Chart getChart() {
		return chart;
	}

	public void setDirty() {
		this.dirty = true;
		requestLayout();
	}

	@Override
	protected final void layoutChildren() {
		if (dirty) {
			dirty = false;
			layoutChartChildren();
		}
	}
	
	public abstract String getCode();

	public abstract T getState();
	
	public abstract void update();
	
	public abstract void layoutChartChildren();
}

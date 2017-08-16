package com.stox.chart.drawing;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.stox.chart.chart.Chart;

public abstract class Drawing extends Group {

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

	public abstract void update();

	public abstract void layoutChartChildren();
}

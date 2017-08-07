package com.stox.chart.view;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ChartViewPanMouseHandler implements MouseHandler {

	private double startX;
	private final ChartView chartView;

	public ChartViewPanMouseHandler(final ChartView chart) {
		this.chartView = chart;
	}

	@Override
	public void attach() {
		chartView.getSplitPane().addEventHandler(MouseEvent.MOUSE_PRESSED, this);
		chartView.getSplitPane().addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
	}

	@Override
	public void detach() {
		chartView.getSplitPane().removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
		chartView.getSplitPane().removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
	}

	@Override
	public void handle(MouseEvent event) {
		if (!event.isConsumed()) {
			if (MouseButton.PRIMARY.equals(event.getButton())) {
				if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
					startX = event.getX();
				} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
					final double x = event.getX();
					chartView.getDateAxis().pan(x, startX);
					chartView.update();
					startX = x;
				}
			}
		}
	}

}

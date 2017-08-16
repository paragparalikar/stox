package com.stox.chart.drawing;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.Callback;

import com.stox.chart.axis.DateAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.chart.view.MouseHandler;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.UiUtil;

public class VerticalLineToggleButton extends ToggleButton implements ChangeListener<Boolean>, Callback<VerticalLine, Void> {

	private final ChartingTool chartingButtonBox;
	private MouseHandler mouseHandler;

	public VerticalLineToggleButton(final ChartingTool chartingButtonBox) {
		this.chartingButtonBox = chartingButtonBox;
		setGraphic(new Group(new Line(8, 2, 8, 14), new Polygon(8, 2, 6, 4, 10, 4), new Polygon(8, 14, 6, 12, 10, 12)));
		selectedProperty().addListener(this);
		UiUtil.classes(this, "icon");
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		final ChartView chartView = chartingButtonBox.getChartView();
		if (null != chartView) {
			if (newValue) {
				mouseHandler = createMouseHandler(chartView);
				chartView.setMouseHandler(mouseHandler);
			} else {
				if (null != mouseHandler) {
					if (mouseHandler.equals(chartView.getMouseHandler())) {
						chartView.setMouseHandler(chartView.getDefaultMouseHandler());
					}
					mouseHandler = null;
				}
			}
		}
	}

	@Override
	public Void call(VerticalLine param) {
		setSelected(false);
		return null;
	}

	protected MouseHandler createMouseHandler(final ChartView chartView) {
		return new VerticalLineMouseHandler(chartView, this);
	}

}

class VerticalLineMouseHandler implements MouseHandler {

	private VerticalLine line;
	private final ChartView chartView;
	private final Callback<VerticalLine, Void> onFinish;

	public VerticalLineMouseHandler(final ChartView chartView, final Callback<VerticalLine, Void> onFinish) {
		this.chartView = chartView;
		this.onFinish = onFinish;
	}

	@Override
	public void handle(MouseEvent event) {
		final Chart chart = chartView.getChartAt(event.getScreenX(), event.getScreenY());
		if (null != chart) {
			final Point2D point = chart.getArea().screenToLocal(event.getScreenX(), event.getScreenY());
			if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
				line = new VerticalLine(chart);
				chart.getDrawings().add(line);
				line.move(point.getX());
			} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
				line.move(point.getX());
			} else if (MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
				onFinish.call(line);
			}
			line.update();
		}
	}

	@Override
	public void attach() {
		chartView.getSplitPane().addEventFilter(MouseEvent.MOUSE_PRESSED, this);
		chartView.getSplitPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
		chartView.getSplitPane().addEventFilter(MouseEvent.MOUSE_RELEASED, this);
	}

	@Override
	public void detach() {
		chartView.getSplitPane().removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
		chartView.getSplitPane().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
		chartView.getSplitPane().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
	}

}

class VerticalLine extends Drawing {

	private long value;
	private final Line line = new Line();
	private final DateAxis dateAxis;

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		private double x;

		@Override
		public void handle(MouseEvent event) {
			if (!event.isConsumed()) {
				if (MouseButton.SECONDARY.equals(event.getButton())) {
					getChart().getChildren().remove(VerticalLine.this);
				} else if (MouseButton.PRIMARY.equals(event.getButton())) {
					if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
						x = event.getX();
						event.consume();
					} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
						move(line.getStartX() + event.getX() - x);
						x = event.getX();
						event.consume();
						update();
					}
				}
			}
		}
	}

	public VerticalLine(final Chart chart) {
		super(chart);
		this.dateAxis = chart.getChartView().getDateAxis();
		getChildren().add(line);
		final Pane parent = chart.getArea();
		line.startYProperty().bind(parent.layoutYProperty());
		line.endYProperty().bind(parent.layoutYProperty().add(parent.heightProperty()));
		line.endXProperty().bind(line.startXProperty());

		final MouseEventHandler mouseEventHandler = new MouseEventHandler();
		addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
	}

	@Override
	public void layoutChartChildren() {
		line.setStartX(dateAxis.getDisplayPosition(value));
	}

	@Override
	public void update() {
		value = dateAxis.getValueForDisplay(line.getStartX());
	}

	public void move(final double x) {
		line.setStartX(x);
	}

}
package com.stox.chart.drawing;

import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.chart.view.MouseHandler;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.UiUtil;

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

public class HorizontalLineToggleButton extends ToggleButton implements ChangeListener<Boolean>, Callback<HorizontalLine, Void> {

	private final ChartingTool chartingButtonBox;
	private MouseHandler mouseHandler;

	public HorizontalLineToggleButton(final ChartingTool chartingButtonBox) {
		this.chartingButtonBox = chartingButtonBox;
		setGraphic(new Group(new Line(2, 8, 14, 8), new Polygon(2, 8, 4, 10, 4, 6), new Polygon(14, 8, 12, 6, 12, 10)));
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
	public Void call(HorizontalLine param) {
		setSelected(false);
		return null;
	}

	protected MouseHandler createMouseHandler(final ChartView chartView) {
		return new HorizontalLineMouseHandler(chartView, this);
	}

}

class HorizontalLineMouseHandler implements MouseHandler {

	private HorizontalLine line;
	private final ChartView chartView;
	private final Callback<HorizontalLine, Void> onFinishedCallback;

	public HorizontalLineMouseHandler(final ChartView chartView, final Callback<HorizontalLine, Void> onFinishedCallback) {
		this.chartView = chartView;
		this.onFinishedCallback = onFinishedCallback;
	}

	@Override
	public void handle(MouseEvent event) {
		final Chart chart = chartView.getChartAt(event.getScreenX(), event.getScreenY());
		if (null != chart) {
			final Point2D point = chart.getArea().screenToLocal(event.getScreenX(), event.getScreenY());
			if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
				line = new HorizontalLine(chart);
				chart.getDrawings().add(line);
				line.move(point.getY());
			} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
				line.move(point.getY());
			} else if (MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
				onFinishedCallback.call(line);
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

class HorizontalLine extends Drawing<HorizontalLine.State> {
	public static final String CODE = "hline";
	
	public static class State implements Drawing.State<State>{
		
		private double value;

		public double getValue() {
			return value;
		}
		
		public void setValue(double value) {
			this.value = value;
		}
		
		@Override
		public String getCode() {
			return CODE;
		}

		@Override
		public void copy(State state) {
			this.value = state.getValue();
		}
		
	}

	
	private final Line line = new Line();
	private final State state = new State();
	
	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public String getCode() {
		return CODE;
	}

	private class MouseHandler implements EventHandler<MouseEvent> {
		private double y;

		@Override
		public void handle(MouseEvent event) {
			if (!event.isConsumed()) {
				if (MouseButton.SECONDARY.equals(event.getButton())) {
					getChart().getChildren().remove(HorizontalLine.this);
				} else if (MouseButton.PRIMARY.equals(event.getButton())) {
					if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
						y = event.getY();
						event.consume();
					} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
						move(line.getStartY() + event.getY() - y);
						y = event.getY();
						event.consume();
						update();
					}
				}
			}
		}
	}

	public HorizontalLine(final Chart chart) {
		super(chart);
		getChildren().add(line);
		final Pane parent = chart.getArea();
		line.startXProperty().bind(parent.layoutXProperty());
		line.endXProperty().bind(parent.layoutXProperty().add(parent.widthProperty()));
		line.endYProperty().bind(line.startYProperty());

		final MouseHandler mouseEventHandler = new MouseHandler();
		addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
	}

	@Override
	public void layoutChartChildren() {
		final Chart chart = getChart();
		line.setStartY(chart.getValueAxis().getDisplayPosition(state.getValue(), chart.getMin(), chart.getMax()));
	}

	@Override
	public void update() {
		final Chart chart = getChart();
		state.setValue(chart.getValueAxis().getValueForDisplay(line.getStartY(), chart.getMin(), chart.getMax()));
	}

	public void move(final double y) {
		line.setStartY(y);
	}

}
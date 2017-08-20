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
import javafx.scene.shape.Line;
import javafx.util.Callback;
import lombok.Value;

public class SegmentToggleButton extends ToggleButton implements ChangeListener<Boolean>, Callback<Segment, Void> {

	private MouseHandler mouseHandler;
	private final ChartingTool chartingButtonBox;

	public SegmentToggleButton(final ChartingTool chartingButtonBox) {
		this.chartingButtonBox = chartingButtonBox;
		setGraphic(new Group(new Line(2, 14, 14, 2)));
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
	public Void call(Segment param) {
		setSelected(false);
		return null;
	}

	protected MouseHandler createMouseHandler(final ChartView chartView) {
		return new SegmentMouseHandler(chartView, this);
	}

}

class SegmentMouseHandler implements MouseHandler {

	private Point2D startPoint;
	private Segment segment;
	private final ChartView chartView;
	private final Callback<Segment, Void> onFinishCallback;

	public SegmentMouseHandler(final ChartView chartView, final Callback<Segment, Void> onFinishCallback) {
		this.chartView = chartView;
		this.onFinishCallback = onFinishCallback;
	}

	@Override
	public void handle(MouseEvent event) {
		final Chart chart = chartView.getChartAt(event.getScreenX(), event.getScreenY());
		if (null != chart) {
			final Point2D point = chart.getArea().screenToLocal(event.getScreenX(), event.getScreenY());
			if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
				segment = createSegment(chart);
				startPoint = point;
				chart.getDrawings().add(segment);
			} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
				segment.move(startPoint.getX(), startPoint.getY(), point.getX(), point.getY());
			} else if (MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
				onFinishCallback.call(segment);
			}
			segment.update();
		}
	}

	protected Segment createSegment(final Chart chart) {
		return new Segment(chart);
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

class Segment extends Drawing<Segment.State> {
	public static final String CODE = "segment";
	
	@Value
	public static class State implements Drawing.State<Segment.State>{
		
		private ControlPoint.State oneState;
		
		private ControlPoint.State twoState;	
		
		@Override
		public String getCode() {
			return CODE;
		}
		
		@Override
		public void copy(State state) {
			oneState.copy(state.getOneState());
			twoState.copy(state.getTwoState());
		}
		
	}

	private final Line line = new Line();
	protected final ControlPoint one;
	protected final ControlPoint two;
	protected final State state;
	

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		private double x;
		private double y;

		@Override
		public void handle(MouseEvent event) {
			if (!event.isConsumed()) {
				if (MouseButton.SECONDARY.equals(event.getButton())) {
					getChart().getDrawings().remove(Segment.this);
				} else if (MouseButton.PRIMARY.equals(event.getButton())) {
					if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
						x = event.getX();
						y = event.getY();
						event.consume();
					} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
						move(event.getX() - x, event.getY() - y);
						x = event.getX();
						y = event.getY();
						event.consume();
						update();
					}
				}
			}
		}
	}
	
	@Override
	public State getState() {
		return state;
	}
	
	@Override
	public String getCode() {
		return CODE;
	}

	protected void move(final double xDelta, final double yDelta) {
		one.setCenterX(one.getCenterX() + xDelta);
		one.setCenterY(one.getCenterY() + yDelta);
		two.setCenterX(two.getCenterX() + xDelta);
		two.setCenterY(two.getCenterY() + yDelta);
	}

	public Segment(final Chart chart) {
		super(chart);
		one = new ControlPoint(chart);
		two = new ControlPoint(chart);
		state = new State(one.getState(), two.getState());
		getChildren().addAll(one, line, two);

		line.startXProperty().bind(one.centerXProperty());
		line.startYProperty().bind(one.centerYProperty());
		line.endXProperty().bind(two.centerXProperty());
		line.endYProperty().bind(two.centerYProperty());

		final MouseEventHandler mouseEventHandler = new MouseEventHandler();
		addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
	}

	public void move(final double startX, final double startY, final double endX, final double endY) {
		one.setCenterX(startX);
		one.setCenterY(startY);
		two.setCenterX(endX);
		two.setCenterY(endY);
	}

	@Override
	public void update() {
		one.update();
		two.update();
	}

	@Override
	public void layoutChartChildren() {
		one.layoutChartChildren();
		two.layoutChartChildren();
	}

}

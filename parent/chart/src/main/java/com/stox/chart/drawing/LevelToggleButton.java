package com.stox.chart.drawing;

import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.chart.view.MouseHandler;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.UiUtil;

public class LevelToggleButton extends ToggleButton implements ChangeListener<Boolean>, Callback<Level, Void> {

	private MouseHandler mouseHandler;
	private final ChartingTool chartingButtonBox;

	public LevelToggleButton(final ChartingTool chartingButtonBox) {
		this.chartingButtonBox = chartingButtonBox;
		final Rectangle graphic = new Rectangle(2, 5, 12, 6);
		graphic.setFill(Color.BURLYWOOD);
		setGraphic(graphic);
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
	public Void call(Level param) {
		setSelected(false);
		return null;
	}

	protected MouseHandler createMouseHandler(final ChartView chartView) {
		return new LevelMouseHandler(chartView, this);
	}

}

class Level extends Drawing {

	private final ControlPoint one;
	private final ControlPoint two;
	private final Rectangle rectangle = new Rectangle();
	private final MouseEventHandler mouseEventHandler = new MouseEventHandler();

	private class MouseEventHandler implements EventHandler<MouseEvent> {
		private double x;
		private double y;

		@Override
		public void handle(MouseEvent event) {
			if (!event.isConsumed()) {
				if (MouseButton.SECONDARY.equals(event.getButton())) {
					getChart().getDrawings().remove(Level.this);
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

	public Level(Chart chart) {
		super(chart);
		one = new ControlPoint(chart);
		two = new ControlPoint(chart);
		rectangle.setOpacity(0.4);
		rectangle.setFill(Color.BURLYWOOD);
		getChildren().addAll(one, two, rectangle);

		rectangle.xProperty().bind(new When(one.centerXProperty().lessThan(two.centerXProperty())).then(one.centerXProperty()).otherwise(two.centerXProperty()));
		rectangle.yProperty().bind(new When(one.centerYProperty().lessThan(two.centerYProperty())).then(one.centerYProperty()).otherwise(two.centerYProperty()));
		final NumberBinding widthBinding = two.centerXProperty().subtract(one.centerXProperty());
		rectangle.widthProperty().bind(new When(widthBinding.lessThan(0)).then(widthBinding.negate()).otherwise(widthBinding));
		final NumberBinding heightBinding = two.centerYProperty().subtract(one.centerYProperty());
		rectangle.heightProperty().bind(new When(heightBinding.lessThan(0)).then(heightBinding.negate()).otherwise(heightBinding));

		addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
		addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
	}

	private void move(final double xDelta, final double yDelta) {
		one.setCenterX(one.getCenterX() + xDelta);
		one.setCenterY(one.getCenterY() + yDelta);
		two.setCenterX(two.getCenterX() + xDelta);
		two.setCenterY(two.getCenterY() + yDelta);
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

class LevelMouseHandler implements MouseHandler {

	private Point2D startPoint;
	private Level level;
	private final ChartView chartView;
	private final Callback<Level, Void> onFinishCallback;

	public LevelMouseHandler(final ChartView chartView, final Callback<Level, Void> onFinishCallback) {
		this.chartView = chartView;
		this.onFinishCallback = onFinishCallback;
	}

	@Override
	public void handle(MouseEvent event) {
		final Chart chart = chartView.getChartAt(event.getScreenX(), event.getScreenY());
		if (null != chart) {
			final Point2D point = chart.getArea().screenToLocal(event.getScreenX(), event.getScreenY());
			if (MouseEvent.MOUSE_PRESSED.equals(event.getEventType())) {
				level = createLevel(chart);
				startPoint = point;
				chart.getDrawings().add(level);
			} else if (MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
				level.move(startPoint.getX(), startPoint.getY(), point.getX(), point.getY());
			} else if (MouseEvent.MOUSE_RELEASED.equals(event.getEventType())) {
				onFinishCallback.call(level);
			}
			level.update();
		}
	}

	protected Level createLevel(final Chart chart) {
		return new Level(chart);
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
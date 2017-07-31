package com.stox.chart.widget;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.view.ChartView;
import com.stox.core.util.StringUtil;

public class Crosshair extends Group implements EventHandler<MouseEvent> {

	private final ChartView chartView;
	private final Line vertical = new Line();
	private final Line horizontal = new Line();
	private final Label valueLabel = new Label();

	public Crosshair(final ChartView chartView) {
		this.chartView = chartView;
		getStyleClass().add("crosshair");
		setMouseTransparent(true);
		setManaged(false);
		getChildren().addAll(vertical, horizontal, valueLabel);

		final SplitPane pane = chartView.getSplitPane();
		pane.addEventHandler(MouseEvent.MOUSE_MOVED, this);
		pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		pane.addEventHandler(MouseEvent.MOUSE_ENTERED, this);
		pane.addEventHandler(MouseEvent.MOUSE_EXITED, this);

		final Pane content = chartView.getContent();
		vertical.startYProperty().bind(content.layoutYProperty());
		vertical.endYProperty().bind(content.layoutYProperty().add(content.heightProperty()));
		vertical.startXProperty().bindBidirectional(vertical.endXProperty());

		horizontal.startXProperty().bind(content.layoutXProperty());
		horizontal.endXProperty().bind(content.layoutXProperty().add(content.widthProperty()));
		horizontal.startYProperty().bindBidirectional(horizontal.endYProperty());
	}

	@Override
	protected void layoutChildren() {

	}

	@Override
	public void handle(MouseEvent event) {
		if (MouseEvent.MOUSE_ENTERED.equals(event.getEventType())) {
			setVisible(true);
		} else if (MouseEvent.MOUSE_EXITED.equals(event.getEventType())) {
			setVisible(false);
		} else if (MouseEvent.MOUSE_MOVED.equals(event.getEventType()) || MouseEvent.MOUSE_DRAGGED.equals(event.getEventType())) {
			setVisible(true);
			final Point2D point = chartView.screenToLocal(event.getScreenX(), event.getScreenY());
			vertical.setStartX(point.getX() + 0.5);
			horizontal.setStartY(point.getY() + 0.5);

			final Chart chart = chartView.getChartAt(event.getScreenX(), event.getScreenY());
			if (null != chart) {
				final Point2D point1 = chart.getArea().screenToLocal(event.getScreenX(), event.getScreenY());

				if (chart instanceof PrimaryChart) {
					final PrimaryChart primaryChart = (PrimaryChart) chart;
					final PrimaryPricePlot primaryPricePlot = primaryChart.getPrimaryPricePlot();
					final double value = chart.getValueAxis().getValueForDisplay(point1.getY(), primaryPricePlot.getMin(), primaryPricePlot.getMax());
					final String text = StringUtil.stringValueOf(value);
					valueLabel.setText(text);
				}

				valueLabel.resizeRelocate(chart.getValueAxis().getLayoutX(), event.getY() + valueLabel.getHeight(), chart.getValueAxis().getWidth(), 24);
			}
		}
	}

}

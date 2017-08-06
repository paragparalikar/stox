package com.stox.chart.widget;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import com.stox.chart.chart.PrimaryChart;

public class Grid extends Group {

	private final PrimaryChart chart;
	private final Group verticalGroup = new Group();
	private final Group horizontalGroup = new Group();

	public Grid(final PrimaryChart chart) {
		this.chart = chart;
		getStyleClass().add("grid");
		setManaged(false);
		setAutoSizeChildren(false);
		getChildren().addAll(verticalGroup, horizontalGroup);
	}

	@Override
	protected void layoutChildren() {

	}

	public void clearHorizontal() {
		horizontalGroup.getChildren().clear();
	}

	public void addHorizontal(final double y) {
		Bounds bounds = chart.getArea().getLayoutBounds();
		Line line = new Line(bounds.getMinX(), y, bounds.getWidth() - 1, y);
		line.setStroke(Color.web("#dddddd"));
		line.setManaged(false);
		horizontalGroup.getChildren().add(line);
	}

	public void clearVertical() {
		chart.getGrid().verticalGroup.getChildren().clear();
	}

	public void addVertical(final double x) {
		final Bounds bounds = chart.getArea().getLayoutBounds();
		final Line line = new Line(x, bounds.getMinY(), x, bounds.getHeight() - 1);
		line.setManaged(false);
		chart.getGrid().verticalGroup.getChildren().add(line);
	}

}

package com.stox.workbench.ui.stage;

import com.stox.workbench.ui.view.Container;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class SnapToGridPane extends Pane implements Container {

	private double xSize;
	private double ySize;
	private boolean initialized;
	private final double boxSize = 30;
	private final Group group = new Group();
	private final Rectangle rectangle = new Rectangle();

	public SnapToGridPane() {
		build();
		bind();
	}
	
	@Override
	public boolean contains(Node content) {
		return getChildren().contains(content);
	}
	
	@Override
	public void add(Node content) {
		if(!contains(content)) {
			getChildren().add(content);
		}
	}
	
	@Override
	public void remove(Node content) {
		getChildren().remove(content);
	}

	private void build() {
		group.setVisible(false);
		group.setManaged(false);
		group.getChildren().add(rectangle);
		getChildren().add(group);

		group.getStyleClass().add("snap-to-grid-group");
		rectangle.getStyleClass().add("snap-to-grid-rectangle");
	}

	private void bind() {
		layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				initialize();
			}
		});
	}

	public void update(Bounds bounds) {
		if (!group.isVisible()) {
			group.setVisible(true);
		}
		if (!initialized) {
			initialize();
			initialized = true;
		}
		double value = snap(bounds.getMinX(), xSize);
		value = value < 0 ? 0 : value;
		rectangle.setLayoutX(value);

		value = snap(bounds.getMinY(), ySize);
		value = value < 0 ? 0 : value;
		rectangle.setLayoutY(value);

		value = snap(bounds.getMaxY(), ySize) - rectangle.getLayoutY();
		value = value > getHeight() ? getHeight() : value;
		rectangle.setHeight(value);

		value = snap(bounds.getMaxX(), xSize) - rectangle.getLayoutX();
		value = value > getWidth() ? getWidth() : value;
		rectangle.setWidth(value);
	}

	private double snap(double value, double size) {
		double rem = value % size;
		return rem < size / 2 ? value - rem : value - rem + size;
	}

	public Bounds end() {
		group.setVisible(false);
		return rectangle.getBoundsInParent();
	}

	public void initialize() {
		if (0 == getHeight() || 0 == getWidth()) {
			return;
		}
		group.getChildren().clear();
		group.getChildren().add(rectangle);

		xSize = getWidth() / new Double(getWidth() / boxSize).intValue();
		ySize = getHeight() / new Double(getHeight() / boxSize).intValue();

		double pointer = 0;
		while (pointer < getWidth()) {
			pointer += xSize;
			Line line = new Line(pointer, 0, pointer, getHeight());
			group.getChildren().add(line);
		}

		pointer = 0;
		while (pointer < getHeight()) {
			pointer += ySize;
			Line line = new Line(0, pointer, getWidth(), pointer);
			group.getChildren().add(line);
		}
	}

	public Bounds adjust(Bounds bounds) {
		if (0 < xSize && 0 < ySize) {
			if (!initialized) {
				initialize();
				initialized = true;
			}

			double x = snap(bounds.getMinX(), xSize);
			x = x < 0 ? 0 : x;

			double y = snap(bounds.getMinY(), ySize);
			y = y < 0 ? 0 : y;

			double height = snap(bounds.getMaxY(), ySize) - y;
			height = height > getHeight() ? getHeight() : height;

			double width = snap(bounds.getMaxX(), xSize) - x;
			width = width > getWidth() ? getWidth() : width;

			return new BoundingBox(x, y, 0, width, height, 0);
		}
		return bounds;
	}

}

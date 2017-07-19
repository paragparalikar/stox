package com.stox.workbench.ui.view.decorator;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.View;

public class MaximizeDecorator implements EventHandler<MouseEvent> {

	private final View view;
	private boolean maximized = false;
	private Rectangle2D backupBounds = null;

	public MaximizeDecorator(final Presenter<?, ?> presenter) {
		this.view = presenter.getView();
	}

	@Override
	public void handle(MouseEvent event) {
		if (MouseEvent.MOUSE_CLICKED.equals(event.getEventType()) && 2 == event.getClickCount() && MouseButton.PRIMARY.equals(event.getButton()) && !event.isConsumed()) {
			if (maximized) {
				restore();
			} else {
				maximize();
			}
			event.consume();
		}
	}

	protected void maximize() {
		maximized = true;
		backupBounds = new Rectangle2D(view.getLayoutX(), view.getLayoutY(), view.getWidth(), view.getHeight());
		final Bounds parentBounds = view.getParent().getBoundsInLocal();
		view.setLayoutX(parentBounds.getMinX());
		view.setLayoutY(parentBounds.getMinY());
		view.setMinHeight(parentBounds.getMaxY() - parentBounds.getMinY());
		view.setMinWidth(parentBounds.getMaxX() - parentBounds.getMinX());
	}

	protected void restore() {
		maximized = false;
		view.setLayoutX(backupBounds.getMinX());
		view.setLayoutY(backupBounds.getMinY());
		view.setMinWidth(backupBounds.getWidth());
		view.setMinHeight(backupBounds.getHeight());
	}

	public void bindNode() {
		final Node node = view.getTitleBar().getMovableNode();
		node.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
	}

	public void unbindNode() {
		final Node node = view.getTitleBar().getMovableNode();
		node.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
	}

}

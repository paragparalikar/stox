package com.stox.workbench.ui.view.decorator;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import com.stox.workbench.ui.view.AbstractDockablePublishingPresenter;
import com.stox.workbench.ui.view.View;

public class RelocationDecorator implements EventHandler<MouseEvent> {

	private double offsetX;
	private double offsetY;
	private final AbstractDockablePublishingPresenter<?, ?> presenter;

	public RelocationDecorator(final AbstractDockablePublishingPresenter<?, ?> presenter) {
		this.presenter = presenter;
	}

	@Override
	public void handle(MouseEvent event) {
		final double x = event.getX();
		final double y = event.getY();
		final View view = presenter.getView();
		final EventType<? extends MouseEvent> type = event.getEventType();
		if (!event.isConsumed()) {
			if (MouseEvent.MOUSE_PRESSED.equals(type)) {
				offsetX = x;
				offsetY = y;
				view.setMode(View.MOVE);
			} else if (View.MOVE == view.getMode() && MouseEvent.MOUSE_DRAGGED.equals(type)) {
				relocateView(x, y, offsetX, offsetY);
			} else if (MouseEvent.MOUSE_RELEASED.equals(type)) {
				offsetX = 0;
				offsetY = 0;
				view.setMode(View.NONE);
				presenter.position();
			}
			event.consume();
		}
	}

	protected void relocateView(double x, double y, double offsetX, double offsetY) {
		final View view = presenter.getView();
		final Bounds bounds = view.getParent().getBoundsInLocal();
		final double newX = view.getLayoutX() + x - offsetX;
		if (newX >= bounds.getMinX() && newX + view.getWidth() <= bounds.getMaxX()) {
			view.setLayoutX(newX);
		}
		final double newY = view.getLayoutY() + y - offsetY;
		if (newY >= bounds.getMinY() && newY + view.getHeight() <= bounds.getMaxY()) {
			view.setLayoutY(newY);
		}
		presenter.updateGrid();
	}

	public void bindMovableNode() {
		final View view = presenter.getView();
		final Node node = view.getTitleBar().getMovableNode();
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
		node.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
	}

	public void unbindMovableNode() {
		final View view = presenter.getView();
		final Node node = view.getTitleBar().getMovableNode();
		node.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
		node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
		node.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
	}

}

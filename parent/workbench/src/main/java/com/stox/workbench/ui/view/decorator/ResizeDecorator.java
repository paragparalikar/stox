package com.stox.workbench.ui.view.decorator;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.View;

public class ResizeDecorator implements EventHandler<MouseEvent> {

	private final Presenter<?, ?> presenter;

	public ResizeDecorator(final Presenter<?, ?> presenter) {
		this.presenter = presenter;
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.isConsumed()) {
			return;
		}
		final double x = event.getX();
		final double y = event.getY();
		final View view = presenter.getView();
		final EventType<? extends MouseEvent> type = event.getEventType();
		if (View.NONE == view.getMode() || View.RESIZE == view.getMode()) {
			if (type.equals(MouseEvent.MOUSE_MOVED) || type.equals(MouseEvent.MOUSE_ENTERED) || type.equals(MouseEvent.MOUSE_EXITED)) {
				if (!event.isPrimaryButtonDown() && !event.isMiddleButtonDown() && !event.isSecondaryButtonDown()) {
					setCursor(x, y);
				}
			} else if (type.equals(MouseEvent.MOUSE_PRESSED) || MouseEvent.MOUSE_DRAGGED.equals(type)) {
				final Cursor cursor = view.getCursor();
				if (!Cursor.DEFAULT.equals(cursor)) {
					resizeView(x, y);
					event.consume();
					view.setMode(View.RESIZE);
				}
			} else if (View.RESIZE == view.getMode() && MouseEvent.MOUSE_RELEASED.equals(type)) {
				view.setMode(View.NONE);
				presenter.position();
			}
		}
	}

	protected void setCursor(final double x, final double y) {
		final Cursor cursor;
		final View view = presenter.getView();
		final double maxX = view.getBoundsInLocal().getMaxX();
		final double maxY = view.getBoundsInLocal().getMaxY();

		if (x < View.BORDER && y < View.BORDER) {
			cursor = Cursor.NW_RESIZE;
		} else if (x > maxX - View.BORDER && y < View.BORDER) {
			cursor = Cursor.NE_RESIZE;
		} else if (x > maxX - View.BORDER && y > maxY - View.BORDER) {
			cursor = Cursor.SE_RESIZE;
		} else if (x < View.BORDER && y > maxY - View.BORDER) {
			cursor = Cursor.SW_RESIZE;
		} else if (x < View.BORDER) {
			cursor = Cursor.W_RESIZE;
		} else if (x > maxX - View.BORDER) {
			cursor = Cursor.E_RESIZE;
		} else if (y < View.BORDER) {
			cursor = Cursor.N_RESIZE;
		} else if (y > maxY - View.BORDER) {
			cursor = Cursor.S_RESIZE;
		} else {
			cursor = Cursor.DEFAULT;
		}
		view.setCursor(cursor);
	}

	protected void resizeView(double x, double y) {
		final View view = presenter.getView();
		final double minWidth = view.getWidth() > view.getMinWidth() ? view.getMinWidth() : 2 * View.BORDER;
		final double maxWidth = view.getParent().getLayoutBounds().getWidth();
		final double minHeight = view.getHeight() > view.getMinHeight() ? view.getMinHeight() : 2 * View.BORDER;
		final double maxHeight = view.getParent().getLayoutBounds().getHeight();
		final Cursor cursor = view.getCursor();
		if (Cursor.W_RESIZE.equals(cursor)) {
			resizeWest(minWidth, maxWidth, x);
		} else if (Cursor.NW_RESIZE.equals(cursor)) {
			resizeWest(minWidth, maxWidth, x);
			resizeNorth(minHeight, maxHeight, y);
		} else if (Cursor.N_RESIZE.equals(cursor)) {
			resizeNorth(minHeight, maxHeight, y);
		} else if (Cursor.NE_RESIZE.equals(cursor)) {
			resizeNorth(minHeight, maxHeight, y);
			resizeEast(minWidth, maxWidth, x);
		} else if (Cursor.E_RESIZE.equals(cursor)) {
			resizeEast(minWidth, maxWidth, x);
		} else if (Cursor.SE_RESIZE.equals(cursor)) {
			resizeEast(minWidth, maxWidth, x);
			resizeSouth(minHeight, maxHeight, y);
		} else if (Cursor.S_RESIZE.equals(cursor)) {
			resizeSouth(minHeight, maxHeight, y);
		} else if (Cursor.SW_RESIZE.equals(cursor)) {
			resizeSouth(minHeight, maxHeight, y);
			resizeWest(minWidth, maxWidth, x);
		}
		view.autosize();
		presenter.updateGrid();
	}

	protected void resizeWest(double minWidth, double maxWidth, double x) {
		final View view = presenter.getView();
		double w = view.getWidth() - x;
		if (w >= minWidth && w <= maxWidth) {
			view.setMinWidth(w);
			view.setMaxWidth(w);
			view.setLayoutX(view.getLayoutX() + x);
		}
	}

	protected void resizeNorth(double minHeight, double maxHeight, double y) {
		final View view = presenter.getView();
		double h = view.getHeight() - y;
		if (h >= minHeight && h <= maxHeight) {
			view.setMinHeight(h);
			view.setMaxHeight(h);
			view.setLayoutY(view.getLayoutY() + y);
		}
	}

	protected void resizeEast(double minWidth, double maxWidth, double x) {
		final View view = presenter.getView();
		if (x >= minWidth && x <= maxWidth) {
			view.setMaxWidth(x);
			view.setMinWidth(x);
		}
	}

	protected void resizeSouth(double minHeight, double maxHeight, double y) {
		final View view = presenter.getView();
		if (y >= minHeight && y <= maxHeight) {
			view.setMinHeight(y);
			view.setMaxHeight(y);
		}
	}

	public void bind() {
		final View view = presenter.getView();
		view.addEventFilter(MouseEvent.MOUSE_ENTERED, this);
		view.addEventFilter(MouseEvent.MOUSE_MOVED, this);
		view.addEventFilter(MouseEvent.MOUSE_EXITED, this);
		view.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
		view.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
		view.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
	}

}

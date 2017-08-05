package com.stox.core.ui;

import java.util.List;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Window;

public class ResizableRelocatableWindowDecorator {

	protected static final int NONE = 0;
	protected static final int MOVE = 1;
	protected static final int RESIZE = 2;
	protected static final double BORDER = 4;

	private final Window window;
	private int mode = NONE;
	private boolean maximized = false;
	private Rectangle2D backupBounds = null;

	private final EventHandler<MouseEvent> relocateHandler = new EventHandler<MouseEvent>() {

		private double offsetX;
		private double offsetY;

		@Override
		public void handle(MouseEvent event) {
			final double x = event.getScreenX();
			final double y = event.getScreenY();
			final EventType<? extends MouseEvent> type = event.getEventType();
			if (!event.isConsumed()) {
				if (MouseEvent.MOUSE_PRESSED.equals(type)) {
					offsetX = x;
					offsetY = y;
					mode = MOVE;
				} else if (MOVE == mode && MouseEvent.MOUSE_DRAGGED.equals(type)) {
					final Rectangle2D bounds = getVisualRectangle();
					final double newX = window.getX() + x - offsetX;
					if (newX >= bounds.getMinX() && newX + window.getWidth() <= bounds.getMaxX()) {
						window.setX(newX);
					}
					final double newY = window.getY() + y - offsetY;
					if (newY >= bounds.getMinY() && newY + window.getHeight() <= bounds.getMaxY()) {
						window.setY(newY);
					}
					offsetX = x;
					offsetY = y;
				} else if (MouseEvent.MOUSE_RELEASED.equals(type)) {
					offsetX = 0;
					offsetY = 0;
					mode = NONE;
				}
				event.consume();
			}
		}
	};

	private final EventHandler<MouseEvent> viewMaximizeHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			if (MouseEvent.MOUSE_CLICKED.equals(event.getEventType()) && 2 == event.getClickCount() && MouseButton.PRIMARY.equals(event.getButton())) {
				toggleMaximized();
				event.consume();
			}
		}
	};

	private final EventHandler<MouseEvent> viewResizeHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			if (event.isConsumed()) {
				return;
			}
			final double x = event.getX();
			final double y = event.getY();
			final EventType<? extends MouseEvent> type = event.getEventType();
			if (NONE == mode || RESIZE == mode) {
				if (type.equals(MouseEvent.MOUSE_MOVED) || type.equals(MouseEvent.MOUSE_ENTERED) || type.equals(MouseEvent.MOUSE_EXITED)) {
					setCursor(x, y);
				} else if (type.equals(MouseEvent.MOUSE_PRESSED) || MouseEvent.MOUSE_DRAGGED.equals(type)) {
					final Cursor cursor = window.getScene().getCursor();
					if (!Cursor.DEFAULT.equals(cursor)) {
						resizeView(x, y);
						event.consume();
						mode = RESIZE;
					}
				}
			}
		}

		private void setCursor(final double x, final double y) {
			final Cursor cursor;
			final double maxX = window.getWidth();
			final double maxY = window.getHeight();
			if (x < BORDER && y < BORDER) {
				cursor = Cursor.NW_RESIZE;
			} else if (x > maxX - BORDER && y < BORDER) {
				cursor = Cursor.NE_RESIZE;
			} else if (x > maxX - BORDER && y > maxY - BORDER) {
				cursor = Cursor.SE_RESIZE;
			} else if (x < BORDER && y > maxY - BORDER) {
				cursor = Cursor.SW_RESIZE;
			} else if (x < BORDER) {
				cursor = Cursor.W_RESIZE;
			} else if (x > maxX - BORDER) {
				cursor = Cursor.E_RESIZE;
			} else if (y < BORDER) {
				cursor = Cursor.N_RESIZE;
			} else if (y > maxY - BORDER) {
				cursor = Cursor.S_RESIZE;
			} else {
				cursor = Cursor.DEFAULT;
			}
			window.getScene().setCursor(cursor);
		}

		protected void resizeView(double x, double y) {
			final Rectangle2D bounds = getVisualRectangle();
			double minWidth = BORDER * 2;
			double maxWidth = bounds.getMaxX() - BORDER * 2;
			double minHeight = BORDER * 2;
			double maxHeight = bounds.getMaxY() - BORDER * 2;
			Cursor cursor = window.getScene().getCursor();
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
		}

		private void resizeWest(double minWidth, double maxWidth, double x) {
			double w = window.getWidth() - x;
			if (w >= minWidth && w <= maxWidth) {
				window.setWidth(w);
				window.setX(window.getX() + x);
			}
		}

		private void resizeNorth(double minHeight, double maxHeight, double y) {
			double h = window.getHeight() - y;
			if (h >= minHeight && h <= maxHeight) {
				window.setHeight(h);
				window.setY(window.getY() + y);
			}
		}

		private void resizeEast(double minWidth, double maxWidth, double x) {
			if (x >= minWidth && x <= maxWidth) {
				window.setWidth(x);
			}
		}

		private void resizeSouth(double minHeight, double maxHeight, double y) {
			if (y >= minHeight && y <= maxHeight) {
				window.setHeight(y);
			}
		}

	};

	public ResizableRelocatableWindowDecorator(final Window window) {
		this.window = window;
		bind();
	}

	private void bind() {
		window.addEventFilter(MouseEvent.MOUSE_ENTERED, viewResizeHandler);
		window.addEventFilter(MouseEvent.MOUSE_MOVED, viewResizeHandler);
		window.addEventFilter(MouseEvent.MOUSE_EXITED, viewResizeHandler);
		window.addEventFilter(MouseEvent.MOUSE_PRESSED, viewResizeHandler);
		window.addEventFilter(MouseEvent.MOUSE_DRAGGED, viewResizeHandler);
	}

	public void bindTitleBar(final Node titleBar) {
		titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED, relocateHandler);
		titleBar.addEventHandler(MouseEvent.MOUSE_MOVED, relocateHandler);
		titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, relocateHandler);
		titleBar.addEventHandler(MouseEvent.MOUSE_CLICKED, relocateHandler);
		titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, relocateHandler);
		titleBar.addEventHandler(MouseEvent.MOUSE_CLICKED, viewMaximizeHandler);
	}

	private Rectangle2D getVisualRectangle() {
		Screen screen = null;
		List<Screen> screens = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getX() + 1, window.getY() + 1);
		if (null == screens || screens.isEmpty()) {
			screen = Screen.getPrimary();
		} else {
			screen = screens.get(0);
		}
		return screen.getVisualBounds();
	}

	public void toggleMaximized() {
		if (maximized) {
			maximized = false;
			window.setX(backupBounds.getMinX());
			window.setY(backupBounds.getMinY());
			window.setWidth(backupBounds.getWidth());
			window.setHeight(backupBounds.getHeight());
		} else {
			maximized = true;
			backupBounds = new Rectangle2D(window.getX(), window.getY(), window.getWidth(), window.getHeight());
			final Rectangle2D parentBounds = getVisualRectangle();
			window.setX(parentBounds.getMinX());
			window.setY(parentBounds.getMinY());
			window.setHeight(parentBounds.getMaxY() - parentBounds.getMinY());
			window.setWidth(parentBounds.getMaxX() - parentBounds.getMinX());
		}
	}

}

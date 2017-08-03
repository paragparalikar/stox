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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ResizableRelocatableStageDecorator {

	protected static final int NONE = 0;
	protected static final int MOVE = 1;
	protected static final int RESIZE = 2;
	protected static final double BORDER = 4;

	private final Stage stage;
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
					final double newX = stage.getX() + x - offsetX;
					if (newX >= bounds.getMinX() && newX + stage.getWidth() <= bounds.getMaxX()) {
						stage.setX(newX);
					}
					final double newY = stage.getY() + y - offsetY;
					if (newY >= bounds.getMinY() && newY + stage.getHeight() <= bounds.getMaxY()) {
						stage.setY(newY);
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
					final Cursor cursor = stage.getScene().getCursor();
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
			final double maxX = stage.getWidth();
			final double maxY = stage.getHeight();
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
			stage.getScene().setCursor(cursor);
		}

		protected void resizeView(double x, double y) {
			final Rectangle2D bounds = getVisualRectangle();
			double minWidth = stage.getWidth() > stage.getMinWidth() ? stage.getMinWidth() : BORDER * 2;
			double maxWidth = bounds.getMaxX() - BORDER * 2;
			double minHeight = stage.getHeight() > stage.getMinHeight() ? stage.getMinHeight() : BORDER * 2;
			double maxHeight = bounds.getMaxY() - BORDER * 2;
			Cursor cursor = stage.getScene().getCursor();
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
			double w = stage.getWidth() - x;
			if (w >= minWidth && w <= maxWidth) {
				stage.setWidth(w);
				stage.setX(stage.getX() + x);
			}
		}

		private void resizeNorth(double minHeight, double maxHeight, double y) {
			double h = stage.getHeight() - y;
			if (h >= minHeight && h <= maxHeight) {
				stage.setHeight(h);
				stage.setY(stage.getY() + y);
			}
		}

		private void resizeEast(double minWidth, double maxWidth, double x) {
			if (x >= minWidth && x <= maxWidth) {
				stage.setWidth(x);
			}
		}

		private void resizeSouth(double minHeight, double maxHeight, double y) {
			if (y >= minHeight && y <= maxHeight) {
				stage.setHeight(y);
			}
		}

	};

	public ResizableRelocatableStageDecorator(final Stage stage) {
		this.stage = stage;
		stage.initStyle(StageStyle.UNDECORATED);
		bind();
	}

	private void bind() {
		stage.addEventFilter(MouseEvent.MOUSE_ENTERED, viewResizeHandler);
		stage.addEventFilter(MouseEvent.MOUSE_MOVED, viewResizeHandler);
		stage.addEventFilter(MouseEvent.MOUSE_EXITED, viewResizeHandler);
		stage.addEventFilter(MouseEvent.MOUSE_PRESSED, viewResizeHandler);
		stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, viewResizeHandler);
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
		List<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getX() + 1, stage.getY() + 1);
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
			stage.setX(backupBounds.getMinX());
			stage.setY(backupBounds.getMinY());
			stage.setWidth(backupBounds.getWidth());
			stage.setHeight(backupBounds.getHeight());
		} else {
			maximized = true;
			backupBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
			final Rectangle2D parentBounds = getVisualRectangle();
			stage.setX(parentBounds.getMinX());
			stage.setY(parentBounds.getMinY());
			stage.setHeight(parentBounds.getMaxY() - parentBounds.getMinY());
			stage.setWidth(parentBounds.getMaxX() - parentBounds.getMinX());
		}
	}

}

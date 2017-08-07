package com.stox.core.ui.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class ResizeRelocateDecorator implements EventHandler<MouseEvent> {

	private static enum Mode {
		NONE, MOVE, RESIZE;
	}

	public static final double BORDER = 4;
	private static final List<Cursor> RESIZE_CURSORS = Collections.unmodifiableList(Arrays.asList(Cursor.NW_RESIZE, Cursor.N_RESIZE, Cursor.NE_RESIZE, Cursor.E_RESIZE,
			Cursor.SE_RESIZE, Cursor.S_RESIZE, Cursor.SW_RESIZE, Cursor.W_RESIZE));

	private Mode mode = Mode.NONE;
	private double offsetX;
	private double offsetY;
	private final Region region;

	public ResizeRelocateDecorator(final Region region) {
		this.region = region;
		region.addEventFilter(MouseEvent.MOUSE_ENTERED, this);
		region.addEventFilter(MouseEvent.MOUSE_MOVED, this);
		region.addEventFilter(MouseEvent.MOUSE_EXITED, this);
		region.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
		region.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
		region.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.isConsumed()) {
			return;
		}

		final double x = event.getX();
		final double y = event.getY();
		final Cursor cursor = region.getCursor();
		final EventType<? extends MouseEvent> type = event.getEventType();

		if (Mode.NONE.equals(mode) || Mode.MOVE.equals(mode)) {
			if (MouseEvent.MOUSE_PRESSED.equals(type)) {
				if (!RESIZE_CURSORS.contains(cursor)) {
					offsetX = x;
					offsetY = y;
					mode = Mode.MOVE;
				}
			} else if (Mode.MOVE.equals(mode) && MouseEvent.MOUSE_DRAGGED.equals(type)) {
				relocateView(x, y, offsetX, offsetY);
			} else if (MouseEvent.MOUSE_RELEASED.equals(type)) {
				offsetX = 0;
				offsetY = 0;
				mode = Mode.NONE;
			}
		}

		if (Mode.NONE.equals(mode) || Mode.RESIZE.equals(mode)) {
			if (type.equals(MouseEvent.MOUSE_MOVED) || type.equals(MouseEvent.MOUSE_ENTERED) || type.equals(MouseEvent.MOUSE_EXITED)) {
				if (!event.isPrimaryButtonDown() && !event.isMiddleButtonDown() && !event.isSecondaryButtonDown()) {
					setCursor(x, y);
				}
			} else if (type.equals(MouseEvent.MOUSE_PRESSED) || MouseEvent.MOUSE_DRAGGED.equals(type)) {
				if (RESIZE_CURSORS.contains(cursor)) {
					resizeView(x, y);
					event.consume();
					mode = Mode.RESIZE;
				}
			} else if (Mode.RESIZE.equals(mode) && MouseEvent.MOUSE_RELEASED.equals(type)) {
				mode = Mode.NONE;
			}
		}
	}

	protected void relocateView(double x, double y, double offsetX, double offsetY) {
		final Bounds bounds = region.getParent().getBoundsInLocal();
		final double newX = region.getLayoutX() + x - offsetX;
		if (newX >= bounds.getMinX() && newX + region.getWidth() <= bounds.getMaxX()) {
			region.setLayoutX(newX);
		}
		final double newY = region.getLayoutY() + y - offsetY;
		if (newY >= bounds.getMinY() && newY + region.getHeight() <= bounds.getMaxY()) {
			region.setLayoutY(newY);
		}
	}

	protected void setCursor(final double x, final double y) {
		final Cursor cursor;
		final double maxX = region.getBoundsInLocal().getMaxX();
		final double maxY = region.getBoundsInLocal().getMaxY();

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
		region.setCursor(cursor);
	}

	protected void resizeView(double x, double y) {
		final double minWidth = region.getWidth() > region.getMinWidth() ? region.getMinWidth() : 2 * BORDER;
		final double maxWidth = region.getParent().getLayoutBounds().getWidth();
		final double minHeight = region.getHeight() > region.getMinHeight() ? region.getMinHeight() : 2 * BORDER;
		final double maxHeight = region.getParent().getLayoutBounds().getHeight();
		final Cursor cursor = region.getCursor();
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
		region.autosize();
	}

	protected void resizeWest(double minWidth, double maxWidth, double x) {
		double w = region.getWidth() - x;
		if (w >= minWidth && w <= maxWidth) {
			region.setMinWidth(w);
			region.setMaxWidth(w);
			region.setLayoutX(region.getLayoutX() + x);
		}
	}

	protected void resizeNorth(double minHeight, double maxHeight, double y) {
		double h = region.getHeight() - y;
		if (h >= minHeight && h <= maxHeight) {
			region.setMinHeight(h);
			region.setMaxHeight(h);
			region.setLayoutY(region.getLayoutY() + y);
		}
	}

	protected void resizeEast(double minWidth, double maxWidth, double x) {
		if (x >= minWidth && x <= maxWidth) {
			region.setMaxWidth(x);
			region.setMinWidth(x);
		}
	}

	protected void resizeSouth(double minHeight, double maxHeight, double y) {
		if (y >= minHeight && y <= maxHeight) {
			region.setMinHeight(y);
			region.setMaxHeight(y);
		}
	}

}

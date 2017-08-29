package com.stox.workbench.ui.view.helper;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

import com.stox.workbench.ui.stage.SnapToGridPane;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.View;

public class ViewPositionHelper {

	private final Presenter<?, ?> presenter;

	public ViewPositionHelper(final Presenter<?, ?> presenter) {
		this.presenter = presenter;
	}

	public void position() {
		final View view = presenter.getView();
		if (view.getParent() instanceof SnapToGridPane) {
			final SnapToGridPane pane = (SnapToGridPane) view.getParent();
			setBounds(pane.end());
		}
	}

	public void setBounds(final Bounds bounds) {
		final View view = presenter.getView();
		if (0 < bounds.getWidth() && 0 < bounds.getHeight()) {
			view.setLayoutX(bounds.getMinX());
			view.setLayoutY(bounds.getMinY());
			view.setMinWidth(bounds.getWidth());
			view.setMaxWidth(bounds.getWidth());
			view.setMinHeight(bounds.getHeight());
			view.setMaxHeight(bounds.getHeight());
		}
	}

	public void setPosition(double x, double y, double width, double height) {
		final View view = presenter.getView();
		final Pane pane = (Pane) view.getParent();
		if (null != pane && pane instanceof SnapToGridPane) {
			final SnapToGridPane snapToGridPane = (SnapToGridPane) pane;
			final Bounds bounds = snapToGridPane.adjust(new BoundingBox(x, y, width, height));
			x = bounds.getMinX();
			y = bounds.getMinY();
			width = bounds.getWidth();
			height = bounds.getHeight();
		}
		view.setLayoutX(x);
		view.setLayoutY(y);
		view.setMinHeight(height);
		view.setMaxHeight(height);
		view.setMinWidth(width);
		view.setMaxWidth(width);
	}

	public void updateGrid() {
		final View view = presenter.getView();
		if (view.getParent() instanceof SnapToGridPane) {
			SnapToGridPane pane = (SnapToGridPane) view.getParent();
			pane.update(view.getBoundsInParent());
		}
	}

}

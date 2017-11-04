package com.stox.workbench.ui.view.helper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import com.stox.core.intf.Persistable;
import com.stox.workbench.ui.view.AbstractDockablePublishingPresenter;
import com.stox.workbench.ui.view.View;

public class ViewPersistanceHelper implements Persistable {

	private final View view;
	private final AbstractDockablePublishingPresenter<?, ?> presenter;

	public ViewPersistanceHelper(final AbstractDockablePublishingPresenter<?, ?> presenter) {
		this.view = presenter.getView();
		this.presenter = presenter;
	}

	@Override
	public void read(DataInput input) throws IOException {
		final Screen screen = Screen.getPrimary();
		final Rectangle2D rectangle = screen.getVisualBounds();
		final double width = rectangle.getWidth();
		final double height = rectangle.getHeight();
		presenter.setPosition(width * input.readDouble(), height * input.readDouble(), width * input.readDouble(), height * input.readDouble());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		final Bounds bounds = view.getBoundsInParent();

		final Screen screen = Screen.getPrimary();
		final Rectangle2D rectangle = screen.getVisualBounds();
		final double width = rectangle.getWidth();
		final double height = rectangle.getHeight();

		output.writeDouble(bounds.getMinX() / width);
		output.writeDouble(bounds.getMinY() / height);
		output.writeDouble(bounds.getWidth() / width);
		output.writeDouble(bounds.getHeight() / height);
	}

}

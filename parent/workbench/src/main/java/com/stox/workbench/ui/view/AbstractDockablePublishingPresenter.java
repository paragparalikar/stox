package com.stox.workbench.ui.view;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.stox.core.ui.Container;
import com.stox.core.ui.widget.titlebar.decorator.CloseDecorator;
import com.stox.workbench.model.ViewState;
import com.stox.workbench.ui.view.decorator.MaximizeDecorator;
import com.stox.workbench.ui.view.decorator.RelocationDecorator;
import com.stox.workbench.ui.view.decorator.ResizeDecorator;
import com.stox.workbench.ui.view.event.RemoveViewRequestEvent;
import com.stox.workbench.ui.view.event.ViewSelectedEvent;
import com.stox.workbench.ui.view.helper.ViewPersistanceHelper;
import com.stox.workbench.ui.view.helper.ViewPositionHelper;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

public abstract class AbstractDockablePublishingPresenter<V extends View, S extends ViewState>
		implements DockablePublishingPresenter<V, S> {

	private boolean selected;
	private CloseDecorator closeDecorator = new CloseDecorator();
	private RelocationDecorator relocateDecorator = new RelocationDecorator(this);
	private MaximizeDecorator maximizeDecorator = new MaximizeDecorator(this);
	private ResizeDecorator resizeDecorator = new ResizeDecorator(this);
	private ViewPositionHelper positionHelper = new ViewPositionHelper(this);
	private ViewPersistanceHelper persistanceHelper = new ViewPersistanceHelper(this);

	@Override
	public void present(Container container, S viewState) {
		final View view = getView();
		Platform.runLater(() -> {
			if (!container.contains(view)) {
				container.add(view);
				start();
				if (null != viewState) {
					setViewSate(viewState);
				} else {
					setDefaultPosition();
				}
			}
		});
	}
	
	@Override
	public void remove(Container container) {
		container.remove(getView());
	}

	@Override
	public void setViewSate(final S viewState) {
		setPosition(viewState.getX(), viewState.getY(), viewState.getWidth(), viewState.getHeight());
	}

	protected void populateViewState(final S viewState) {
		final View view = getView();
		viewState.setCode(view.getCode());
		viewState.setX(view.getLayoutX());
		viewState.setY(view.getLayoutY());
		viewState.setWidth(view.getWidth());
		viewState.setHeight(view.getHeight());
	}

	@Override
	public void setPosition(double x, double y, double width, double height) {
		positionHelper.setPosition(x, y, width, height);
	}

	@Override
	public void start() {
		closeDecorator.setHasLifecycle(this);
		closeDecorator.setTitleBar(getView().getTitleBar());
		relocateDecorator.bindMovableNode();
		maximizeDecorator.bindNode();
		resizeDecorator.bind();

		getView().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> setSelected(true));
	}

	@Override
	public void setSelected(final boolean value) {
		if (!selected && value) {
			publish(new ViewSelectedEvent(this, getView()));
		}
		selected = value;
	}

	@Override
	public void stop() {
		publish(new RemoveViewRequestEvent(this));
	}

	@Override
	public void read(final DataInput input) throws IOException {
		persistanceHelper.read(input);
	}

	@Override
	public void write(final DataOutput output) throws IOException {
		persistanceHelper.write(output);
	}

	@Override
	public void position() {
		positionHelper.position();
	}

	@Override
	public void updateGrid() {
		positionHelper.updateGrid();
	}

}

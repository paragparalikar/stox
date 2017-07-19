package com.stox.navigator.ui;

import javafx.scene.layout.Pane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.stox.core.ui.ToastCallback;
import com.stox.data.DataClient;
import com.stox.workbench.ui.view.PublisherPresenter;

@Component
@Scope("prototype")
public class NavigatorPresenter extends PublisherPresenter<NavigatorView, NavigatorViewState> {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private DataClient dataClient;

	private final NavigatorView view = new NavigatorView();

	@Override
	public NavigatorView getView() {
		return view;
	}

	@Override
	public void start() {
		super.start();
		dataClient.getAllInstruments(new ToastCallback<>(instruments -> {
			view.getListView().getItems().addAll(instruments);
			return null;
		}));
	}

	@Override
	public NavigatorViewState getViewState() {
		final NavigatorViewState viewState = new NavigatorViewState();
		populateViewState(viewState);
		return viewState;
	}

	@Override
	public void setDefaultPosition() {
		if (null != view.getParent()) {
			final Pane pane = (Pane) view.getParent();
			setPosition(0, 0, pane.getWidth() / 6, pane.getHeight());
		}
	}

	@Override
	public void publish(ApplicationEvent event) {
		eventPublisher.publishEvent(event);
	}

}

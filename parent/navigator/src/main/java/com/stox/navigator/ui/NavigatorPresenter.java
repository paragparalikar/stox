package com.stox.navigator.ui;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.ui.ToastCallback;
import com.stox.data.DataClient;
import com.stox.data.InstrumentFilterProvider;
import com.stox.data.event.InstrumentFilterChangedEvent;
import com.stox.workbench.ui.view.PublisherPresenter;

@Component
@Scope("prototype")
public class NavigatorPresenter extends PublisherPresenter<NavigatorView, NavigatorViewState> {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private DataClient dataClient;

	@Autowired
	private InstrumentFilterProvider instrumentFilterProvider;

	private final NavigatorView view = new NavigatorView();
	private Node instrumentFilterView;

	public NavigatorPresenter() {
		view.getFilterButton().selectedProperty().addListener((observable, oldValue, value) -> {
			if (value) {
				if (null == instrumentFilterView) {
					instrumentFilterView = instrumentFilterProvider.getInstrumentFilterView(view.getListView().getItems());
				}
				showInstrumentFilterView();
			} else {
				view.getTitleBar().remove(Side.BOTTOM, instrumentFilterView);
			}
		});
	}

	@EventListener(InstrumentFilterChangedEvent.class)
	public void onInstrumentFilterViewChanged(final InstrumentFilterChangedEvent event) {
		instrumentFilterView = instrumentFilterProvider.getInstrumentFilterView(view.getListView().getItems());
		showInstrumentFilterView();
	}

	private void showInstrumentFilterView() {
		if (null != instrumentFilterView) {
			view.getTitleBar().clear(Side.BOTTOM);
			view.getTitleBar().add(Side.BOTTOM, 0, instrumentFilterView);
		}
	}

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

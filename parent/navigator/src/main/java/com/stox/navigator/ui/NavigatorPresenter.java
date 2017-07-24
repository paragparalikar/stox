package com.stox.navigator.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.layout.Pane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.model.BarSpan;
import com.stox.core.ui.ToastCallback;
import com.stox.data.DataClient;
import com.stox.data.FilterPresenterProvider;
import com.stox.data.event.FilterPresenterChangedEvent;
import com.stox.data.ui.FilterPresenter;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.PublisherPresenter;

@Component
@Scope("prototype")
public class NavigatorPresenter extends PublisherPresenter<NavigatorView, NavigatorViewState> {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private DataClient dataClient;

	@Autowired
	private FilterPresenterProvider filterPresenterProvider;

	private final NavigatorView view = new NavigatorView();
	private FilterPresenter filterPresenter;

	public NavigatorPresenter() {
		view.getFilterButton().addEventHandler(ActionEvent.ACTION, event -> showFilter());
		view.getListView().getSelectionModel().selectedItemProperty().addListener((observable, old, instrument) -> {
			final State state = getLinkState();
			publish(new State(instrument.getSymbol(), null == state ? BarSpan.D : state.getBarSpan(), Long.MAX_VALUE));
		});
		view.getSearchButton().selectedProperty().addListener((observable, oldValue, value) -> {
			if (value) {
				view.getTitleBar().add(Side.BOTTOM, 0, view.getSearchTextField());
			} else {
				view.getTitleBar().remove(Side.BOTTOM, view.getSearchTextField());
			}
		});
	}

	private void showFilter() {
		if (null == filterPresenter) {
			filterPresenter = filterPresenterProvider.getFilterPresenter(view.getListView().getItems());
		}
		if (null != filterPresenter) {
			final FilterModalPresenter filterModalPresenter = new FilterModalPresenter(filterPresenter);
			filterModalPresenter.getModal().show();
		}
	}

	@EventListener(FilterPresenterChangedEvent.class)
	public void onInstrumentFilterViewChanged(final FilterPresenterChangedEvent event) {
		if (null != filterPresenter) {
			filterPresenter = filterPresenterProvider.getFilterPresenter(view.getListView().getItems());
		}
	}

	@Override
	public NavigatorView getView() {
		return view;
	}

	@Override
	public void start() {
		view.showSpinner(true);
		super.start();
		dataClient.getAllInstruments(new ToastCallback<>(instruments -> {
			view.getListView().getItems().addAll(instruments);
			view.showSpinner(false);
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

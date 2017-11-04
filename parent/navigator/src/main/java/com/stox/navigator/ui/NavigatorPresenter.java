package com.stox.navigator.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.core.model.Instrument;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.ui.filter.FilterPresenter;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.StatePublisherPresenter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class NavigatorPresenter extends StatePublisherPresenter<NavigatorView, NavigatorViewState> {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private InstrumentRepository instrumentRepository;

	private final NavigatorView view = new NavigatorView();
	private final List<Instrument> allInstruments = new ArrayList<>(100000);
	private FilterPresenter filterPresenter;

	public NavigatorPresenter() {
		view.getFilterButton().addEventHandler(ActionEvent.ACTION, event -> showFilter());
		view.getListView().getSelectionModel().selectedItemProperty().addListener((observable, old, instrument) -> {
			if (null != instrument) {
				publish(new State(instrument.getId(), null, 0));
			}
		});
		view.getSearchButton().selectedProperty().addListener((observable, oldValue, value) -> {
			if (value) {
				view.getTitleBar().add(Side.BOTTOM, 0, view.getSearchTextField());
			} else {
				view.getTitleBar().remove(Side.BOTTOM, view.getSearchTextField());
			}
		});
	}

	@PostConstruct
	public void postConstruct() {
		filterPresenter = new FilterPresenter(allInstruments, instrumentRepository, taskExecutor);
	}

	private void showFilter() {
		final FilterModalPresenter filterModalPresenter = new FilterModalPresenter(filterPresenter);
		filterModalPresenter.getModal().getFilterButton().addEventHandler(ActionEvent.ACTION, event -> {
			filterModalPresenter.getModal().hide();
			filterPresenter.filter(instruments -> {
				Platform.runLater(() -> {
					view.getListView().getItems().setAll(instruments);
				});
			});
		});
		filterModalPresenter.getModal().show();
	}

	@EventListener(InstrumentsChangedEvent.class)
	public void onInstrumentsChanged(final InstrumentsChangedEvent event) {
		allInstruments.clear();
		allInstruments.addAll(event.getInstruments());
		filterPresenter.filter(instruments -> {
			Platform.runLater(() -> {
				view.getListView().getItems().setAll(instruments);
			});
		});
	}

	@Override
	public NavigatorView getView() {
		return view;
	}

	@Async
	@Override
	public void start() {
		Platform.runLater(() -> {
			view.showSpinner(true);
			super.start();
		});
		
		final List<Instrument> instruments = instrumentRepository.getAllInstruments();
		allInstruments.clear();
		allInstruments.addAll(instruments);
		filterPresenter.filter(i -> {
			Platform.runLater(() -> {
				view.getListView().getItems().setAll(i);
				view.showSpinner(false);
			});
		});
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

package com.stox.watchlist.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.watchlist.model.WatchlistViewState;
import com.stox.workbench.ui.view.PublisherPresenter;

import javafx.geometry.Side;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class WatchlistPresenter extends PublisherPresenter<WatchlistView, WatchlistViewState> {
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private final WatchlistView view = new WatchlistView();
	
	public WatchlistPresenter() {
		view.getSearchButton().selectedProperty().addListener((observable, oldValue, value) -> {
			if (value) {
				view.getTitleBar().add(Side.BOTTOM, 0, view.getSearchTextField());
			} else {
				view.getTitleBar().remove(Side.BOTTOM, view.getSearchTextField());
			}
		});
	}
	
	@Override
	public WatchlistView getView() {
		return view;
	}

	@Override
	public WatchlistViewState getViewState() {
		final WatchlistViewState viewState = new WatchlistViewState();
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



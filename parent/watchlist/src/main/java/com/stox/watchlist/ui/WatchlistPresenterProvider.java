package com.stox.watchlist.ui;

import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.ui.StylesheetProvider;
import com.stox.watchlist.event.WatchlistCreatedEvent;
import com.stox.watchlist.event.WatchlistDeletedEvent;
import com.stox.watchlist.event.WatchlistEditedEvent;
import com.stox.watchlist.event.WatchlistEntryCreatedEvent;
import com.stox.watchlist.event.WatchlistEntryDeletedEvent;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class WatchlistPresenterProvider implements PresenterProvider, StylesheetProvider {
	
	@Autowired
	private BeanFactory beanFactory;
	
	private final Map<WatchlistPresenter,WatchlistPresenter> cache = new WeakHashMap<>();
	
	@Override
	public String[] getStylesheets() {
		return new String[]{"styles/watchlist.css"};
	}

	@Override
	public String getViewCode() {
		return WatchlistUiConstant.CODE;
	}

	@Override
	public String getViewName() {
		return WatchlistUiConstant.NAME;
	}

	@Override
	public String getViewIcon() {
		return WatchlistUiConstant.ICON;
	}

	@Override
	public Presenter<?, ?> create() {
		final WatchlistPresenter watchlistPresenter = beanFactory.getBean(WatchlistPresenter.class);
		cache.put(watchlistPresenter, watchlistPresenter);
		return watchlistPresenter;
	}
	
	@EventListener
	public void onWatchlistCreated(final WatchlistCreatedEvent event) {
		cache.keySet().forEach(watchlistPresenter -> watchlistPresenter.onWatchlistCreated(event));
	}
	
	@EventListener
	public void onWatchlistDeleted(final WatchlistDeletedEvent event) {
		cache.keySet().forEach(watchlistPresenter -> watchlistPresenter.onWatchlistDeleted(event));
	}
	
	@EventListener
	public void onWatchlistEdited(final WatchlistEditedEvent event) {
		cache.keySet().forEach(watchlistPresenter -> watchlistPresenter.onWatchlistEdited(event));
	}

	@EventListener
	public void onWatchlistEntryCreated(final WatchlistEntryCreatedEvent event) {
		cache.keySet().forEach(watchlistPresenter -> watchlistPresenter.onWatchlistEntryCreated(event));
	}
	
	@EventListener
	public void onWatchlistEntryDeleted(final WatchlistEntryDeletedEvent event) {
		cache.keySet().forEach(watchlistPresenter -> watchlistPresenter.onWatchlistEntryDeleted(event));
	}
}

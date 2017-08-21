package com.stox.watchlist.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasInstrument;
import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.watchlist.client.WatchlistClient;
import com.stox.watchlist.client.WatchlistEntryClient;
import com.stox.watchlist.event.WatchlistCreatedEvent;
import com.stox.watchlist.event.WatchlistDeletedEvent;
import com.stox.watchlist.event.WatchlistEditedEvent;
import com.stox.watchlist.model.Watchlist;
import com.stox.watchlist.model.WatchlistEntry;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope("prototype")
public class AddToWatchlistMenu<T extends HasInstrument & HasBarSpan> extends Menu {

	private T entryProvider;

	@Autowired
	private WatchlistClient watchlistClient;

	@Autowired
	private WatchlistEntryClient watchlistEntryClient;

	public AddToWatchlistMenu() {
		super("Add To Watchlist");
	}

	public void setEntryProvider(T entryProvider) {
		this.entryProvider = entryProvider;
	}

	@PostConstruct
	public void postConstruct() {
		watchlistClient.loadAll(new ResponseCallback<List<Watchlist>>() {
			@Override
			public void onSuccess(final Response<List<Watchlist>> response) {
				Platform.runLater(() -> {
					getItems()
							.setAll(response.getPayload().stream().sorted(new HasNameComaparator<>()).map(watchlist -> {
								final MenuItem item = new MenuItem(watchlist.getName());
								item.addEventHandler(ActionEvent.ACTION, event -> addToWatchlist(watchlist));
								return item;
							}).collect(Collectors.toList()));
				});
			}
		});
	}

	private void addToWatchlist(final Watchlist watchlist) {
		final WatchlistEntry entry = new WatchlistEntry();
		final Instrument instrument = entryProvider.getInstrument();
		final BarSpan barSpan = entryProvider.getBarSpan();
		entry.setInstrument(instrument);
		entry.setBarSpan(barSpan);
		watchlistEntryClient.save(entry, new ResponseCallback<WatchlistEntry>() {
			@Override
			public void onSuccess(Response<WatchlistEntry> response) {

			}
		});
	}

	@EventListener
	public void onWatchlistCreated(final WatchlistCreatedEvent event) {
		Platform.runLater(() -> {
			final Watchlist watchlist = event.getWatchlist();
			final MenuItem item = new MenuItem(watchlist.getName());
			item.addEventHandler(ActionEvent.ACTION, e -> addToWatchlist(watchlist));
			getItems().add(item);
			FXCollections.sort(getItems(), new Comparator<MenuItem>() {
				@Override
				public int compare(MenuItem o1, MenuItem o2) {
					return ((Watchlist) o1.getUserData()).getName()
							.compareToIgnoreCase(((Watchlist) o2.getUserData()).getName());
				}
			});
		});
	}

	@EventListener
	public void onWatchlistDeleted(final WatchlistDeletedEvent event) {
		Platform.runLater(() -> {
			getItems().removeIf(item -> ((Watchlist) item.getUserData()).getId().equals(event.getWatchlist().getId()));
		});
	}

	@EventListener
	public void onWatchlistEdited(final WatchlistEditedEvent event) {
		for (final MenuItem item : getItems()) {
			final Watchlist watchlist = (Watchlist) item.getUserData();
			if (watchlist.getId().equals(event.getWatchlist().getId())) {
				Platform.runLater(() -> {
					item.setText(watchlist.getName());
					item.setUserData(watchlist);
				});
				break;
			}
		}
	}
}

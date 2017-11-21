package com.stox.watchlist.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasInstrument;
import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.core.ui.widget.modal.Notification;
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
import javafx.scene.control.Label;
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
				final List<MenuItem> items = response.getPayload().stream().sorted(new HasNameComaparator<>())
						.map(watchlist -> {
							final MenuItem item = new MenuItem(watchlist.getName());
							item.setUserData(watchlist);
							item.addEventHandler(ActionEvent.ACTION, event -> addToWatchlist(watchlist));
							return item;
						}).collect(Collectors.toList());
				Platform.runLater(() -> getItems().setAll(items));
			}
		});
	}

	private void addToWatchlist(final Watchlist watchlist) {
		final Instrument instrument = entryProvider.getInstrument();
		final BarSpan barSpan = entryProvider.getBarSpan();
		if (null != instrument && null != barSpan) {
			final WatchlistEntry entry = new WatchlistEntry();
			entry.setWatchlistId(watchlist.getId());
			entry.setInstrument(instrument);
			entry.setInstrumentId(instrument.getId());
			entry.setBarSpan(barSpan);
			watchlistEntryClient.save(entry, new ResponseCallback<WatchlistEntry>() {
				@Override
				public void onSuccess(Response<WatchlistEntry> response) {
					final StringBuilder stringBuilder = new StringBuilder("WatchlistEntry created with below details:");
					stringBuilder.append("\nInstrument\t" + instrument.getName());
					stringBuilder.append("\nTimeframe\t" + barSpan.getName());
					stringBuilder.append("\nWatchlist\t\t" + watchlist.getName());
					Notification.builder().style("success").graphic(new Label(stringBuilder.toString())).build().show();
				}

				@Override
				public void onFailure(Response<WatchlistEntry> response, Throwable throwable) {
					Notification.builder().style("danger").graphic(new Label(throwable.getMessage())).build().show();
				}
			});
		}
	}

	public void onWatchlistCreated(final WatchlistCreatedEvent event) {
		final Watchlist watchlist = event.getWatchlist();
		final MenuItem item = new MenuItem(watchlist.getName());
		item.setUserData(watchlist);
		item.addEventHandler(ActionEvent.ACTION, e -> addToWatchlist(watchlist));
		getItems().add(item);
		FXCollections.sort(getItems(), new Comparator<MenuItem>() {
			@Override
			public int compare(MenuItem o1, MenuItem o2) {
				return Objects.compare((Watchlist) o1.getUserData(), (Watchlist) o2.getUserData(),
						new HasNameComaparator<>());
			}
		});
	}

	public void onWatchlistDeleted(final WatchlistDeletedEvent event) {
		getItems().removeIf(item -> ((Watchlist) item.getUserData()).getId().equals(event.getWatchlist().getId()));
	}

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

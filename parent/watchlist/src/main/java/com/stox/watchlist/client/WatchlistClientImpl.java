package com.stox.watchlist.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.watchlist.event.WatchlistCreatedEvent;
import com.stox.watchlist.event.WatchlistDeletedEvent;
import com.stox.watchlist.event.WatchlistEditedEvent;
import com.stox.watchlist.model.Watchlist;
import com.stox.watchlist.repository.WatchlistRepository;

@Lazy
@Async
@Component
public class WatchlistClientImpl implements WatchlistClient {

	@Autowired
	private WatchlistRepository watchlistRepository;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public void loadAll(ResponseCallback<List<Watchlist>> callback) {
		try {
			callback.onSuccess(new Response<>(watchlistRepository.loadAll()));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

	@Override
	public void save(Watchlist watchlist, ResponseCallback<Watchlist> callback) {
		try {
			final boolean created = null == watchlist.getId();
			final Watchlist managedWatchlist = watchlistRepository.save(watchlist);
			final ApplicationEvent event = created ? new WatchlistCreatedEvent(this, managedWatchlist) : new WatchlistEditedEvent(this, managedWatchlist); 
			eventPublisher.publishEvent(event);
			callback.onSuccess(new Response<>(managedWatchlist));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

	@Override
	public void delete(Integer watchlistId, ResponseCallback<Watchlist> callback) {
		try {
			final Watchlist watchlist = watchlistRepository.delete(watchlistId);
			eventPublisher.publishEvent(new WatchlistDeletedEvent(this, watchlist));
			callback.onSuccess(new Response<>(watchlist));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

}

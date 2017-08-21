package com.stox.watchlist.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.watchlist.event.WatchlistEntryCreatedEvent;
import com.stox.watchlist.event.WatchlistEntryDeletedEvent;
import com.stox.watchlist.model.WatchlistEntry;
import com.stox.watchlist.repository.WatchlistEntryRepository;

@Lazy
@Async
@Component
public class WatchlistEntryClientImpl implements WatchlistEntryClient {
	
	@Autowired
	private WatchlistEntryRepository watchlistEntryRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Override
	public void load(Integer watchlistId, ResponseCallback<List<WatchlistEntry>> callback) {
		try {
			callback.onSuccess(new Response<>(watchlistEntryRepository.load(watchlistId)));
		}catch(final Exception exception) {
			callback.onFailure(null, exception);
		}finally {
			callback.onDone();
		}
	}

	@Override
	public void save(WatchlistEntry entry, ResponseCallback<WatchlistEntry> callback) {
		try {
			final WatchlistEntry managedEntry = watchlistEntryRepository.save(entry); 
			eventPublisher.publishEvent(new WatchlistEntryCreatedEvent(this, managedEntry));
			callback.onSuccess(new Response<>(managedEntry));
		}catch(final Exception exception) {
			callback.onFailure(null, exception);
		}finally {
			callback.onDone();
		}
	}

	@Override
	public void delete(final Integer watchlistId, final String entryId, ResponseCallback<WatchlistEntry> callback) {
		try {
			final WatchlistEntry entry = watchlistEntryRepository.delete(watchlistId, entryId);
			eventPublisher.publishEvent(new WatchlistEntryDeletedEvent(this, entry));
			callback.onSuccess(new Response<>(entry));
		}catch(final Exception exception) {
			callback.onFailure(null, exception);
		}finally {
			callback.onDone();
		}
	}

}

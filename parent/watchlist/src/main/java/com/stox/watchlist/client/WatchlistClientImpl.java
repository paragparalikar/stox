package com.stox.watchlist.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.watchlist.model.Watchlist;
import com.stox.watchlist.repository.WatchlistRepository;

@Lazy
@Async
@Component
public class WatchlistClientImpl implements WatchlistClient {

	@Autowired
	private WatchlistRepository watchlistRepository;

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
			callback.onSuccess(new Response<>(watchlistRepository.save(watchlist)));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

	@Override
	public void delete(Integer watchlistId, ResponseCallback<Watchlist> callback) {
		try {
			callback.onSuccess(new Response<>(watchlistRepository.delete(watchlistId)));
		} catch (final Exception exception) {
			callback.onFailure(null, exception);
		} finally {
			callback.onDone();
		}
	}

}

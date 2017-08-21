package com.stox.watchlist.client;

import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.watchlist.model.Watchlist;

public interface WatchlistClient {
	
	void loadAll(final ResponseCallback<List<Watchlist>> callback);
	
	void save(final Watchlist watchlist, final ResponseCallback<Watchlist> callback);
	
	void delete(final Integer watchlistId, final ResponseCallback<Watchlist> callback);

}

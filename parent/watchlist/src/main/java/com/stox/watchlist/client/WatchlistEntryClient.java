package com.stox.watchlist.client;

import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.watchlist.model.WatchlistEntry;

public interface WatchlistEntryClient {

	void load(final Integer watchlistId, final ResponseCallback<List<WatchlistEntry>> callback);
	
	void save(final WatchlistEntry entry, final ResponseCallback<WatchlistEntry> callback);
	
	void delete(Integer watchlistId, String entryId, ResponseCallback<WatchlistEntry> callback);
}

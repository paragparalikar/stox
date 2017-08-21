package com.stox.watchlist.repository;

import java.util.List;

import com.stox.watchlist.model.WatchlistEntry;

public interface WatchlistEntryRepository {
	
	List<WatchlistEntry> load(final Integer watchlistId) throws Exception;
	
	WatchlistEntry save(final WatchlistEntry entry) throws Exception;

	WatchlistEntry delete(Integer watchlistId, String entryId) throws Exception;

}

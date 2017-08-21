package com.stox.watchlist.repository;

import java.util.List;

import com.stox.watchlist.model.WatchlistEntry;

public interface WatchlistEntryRepository {
	
	List<WatchlistEntry> load(final Integer watchlistId) throws Exception;
	
	WatchlistEntry save(final WatchlistEntry entry) throws Exception;
	
	WatchlistEntry delete(final Integer entryId) throws Exception;
	
	void deleteByWatchlistId(final Integer watchlistId) throws Exception;

}

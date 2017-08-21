package com.stox.watchlist.repository;

import java.util.List;

import com.stox.watchlist.model.Watchlist;

public interface WatchlistRepository {
	
	List<Watchlist> loadAll() throws Exception;
	
	Watchlist save(final Watchlist watchlist) throws Exception;
	
	Watchlist delete(final Integer watchlistId) throws Exception;

}

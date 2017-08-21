package com.stox.watchlist.repository;

import com.stox.watchlist.util.WatchlistConstant;

public class WatchlistRepositoryUtil {
	
	public static final String getWatchlistFilePath(final Integer watchlistId) {
		return WatchlistConstant.PATH + String.valueOf(watchlistId) + ".csv";
	}

}

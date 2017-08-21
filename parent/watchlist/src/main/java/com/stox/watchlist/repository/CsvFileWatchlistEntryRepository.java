package com.stox.watchlist.repository;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.watchlist.model.WatchlistEntry;

@Lazy
@Component
public class CsvFileWatchlistEntryRepository implements WatchlistEntryRepository {

	@Override
	public List<WatchlistEntry> load(Integer watchlistId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatchlistEntry save(WatchlistEntry entry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatchlistEntry delete(Integer entryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteByWatchlistId(Integer watchlistId) {
		// TODO Auto-generated method stub

	}

}

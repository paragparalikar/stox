package com.stox.watchlist.model;

public class WatchlistEntryExistsException extends RuntimeException {
	private static final long serialVersionUID = 5631930061504364985L;

	private final WatchlistEntry entry;
	
	public WatchlistEntryExistsException(final WatchlistEntry entry) {
		super("WatchlistEntry for \""+entry.getInstrument().getName()+"\" with timeframe \""+entry.getBarSpan().getName()+"\" already exists");
		this.entry = entry;
	}

	public WatchlistEntry getEntry() {
		return entry;
	}
}

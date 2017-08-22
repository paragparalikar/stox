package com.stox.watchlist.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class WatchlistEntryExistsException extends RuntimeException {
	private static final long serialVersionUID = 5631930061504364985L;

	private final WatchlistEntry entry;
	
	@Override
	public String getMessage() {
		return "WatchlistEntry for \""+entry.getInstrument().getName()+"\" with timeframe \""+entry.getBarSpan().getName()+"\" already exists";
	}
	
}

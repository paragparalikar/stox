package com.stox.watchlist.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class WatchlistExistsException extends RuntimeException {
	private static final long serialVersionUID = 4995810143286572603L;
	
	private final Watchlist watchlist;
	
	@Override
	public String getMessage() {
		return "Watchlist with name \""+watchlist.getName()+"\" already exists";
	}
	
}

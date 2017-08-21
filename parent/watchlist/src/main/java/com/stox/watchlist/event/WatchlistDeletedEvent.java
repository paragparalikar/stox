package com.stox.watchlist.event;

import org.springframework.context.ApplicationEvent;

import com.stox.watchlist.model.Watchlist;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class WatchlistDeletedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2626654711689209571L;

	private final Watchlist watchlist;

	public WatchlistDeletedEvent(Object source, final Watchlist watchlist) {
		super(source);
		this.watchlist = watchlist;
	}
	
}

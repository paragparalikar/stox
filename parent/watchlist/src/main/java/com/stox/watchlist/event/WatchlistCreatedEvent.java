package com.stox.watchlist.event;

import org.springframework.context.ApplicationEvent;

import com.stox.watchlist.model.Watchlist;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class WatchlistCreatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -8338988758665039065L;

	private final Watchlist watchlist;
	
	public WatchlistCreatedEvent(Object source, final Watchlist watchlist) {
		super(source);
		this.watchlist = watchlist;
	}

}

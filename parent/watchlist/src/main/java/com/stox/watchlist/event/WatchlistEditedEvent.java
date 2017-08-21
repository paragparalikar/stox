package com.stox.watchlist.event;

import org.springframework.context.ApplicationEvent;

import com.stox.watchlist.model.Watchlist;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class WatchlistEditedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -6095825616781613013L;

	private final Watchlist watchlist;

	public WatchlistEditedEvent(Object source, final Watchlist watchlist) {
		super(source);
		this.watchlist = watchlist;
	}

}

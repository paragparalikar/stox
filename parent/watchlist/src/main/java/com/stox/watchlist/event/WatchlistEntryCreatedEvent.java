package com.stox.watchlist.event;

import org.springframework.context.ApplicationEvent;

import com.stox.watchlist.model.WatchlistEntry;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class WatchlistEntryCreatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = 4217763403413279769L;

	private final WatchlistEntry entry;
	
	public WatchlistEntryCreatedEvent(Object source, final WatchlistEntry entry) {
		super(source);
		this.entry = entry;
	}

}

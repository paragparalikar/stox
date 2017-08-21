package com.stox.watchlist.event;

import org.springframework.context.ApplicationEvent;

import com.stox.watchlist.model.WatchlistEntry;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class WatchlistEntryDeletedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -196292925245318080L;

	private final WatchlistEntry entry;
	
	public WatchlistEntryDeletedEvent(Object source, final WatchlistEntry entry) {
		super(source);
		this.entry = entry;
	}

}

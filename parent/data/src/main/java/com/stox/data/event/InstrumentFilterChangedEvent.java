package com.stox.data.event;

import org.springframework.context.ApplicationEvent;

public class InstrumentFilterChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2995005955610901013L;

	public InstrumentFilterChangedEvent(Object source) {
		super(source);
	}

}

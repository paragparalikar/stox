package com.stox.data.event;

import org.springframework.context.ApplicationEvent;

public class FilterPresenterChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2995005955610901013L;

	public FilterPresenterChangedEvent(Object source) {
		super(source);
	}

}

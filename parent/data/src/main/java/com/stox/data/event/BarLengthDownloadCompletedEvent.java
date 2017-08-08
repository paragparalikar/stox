package com.stox.data.event;

import org.springframework.context.ApplicationEvent;

public class BarLengthDownloadCompletedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -2323791731913312752L;

	public BarLengthDownloadCompletedEvent(Object source) {
		super(source);
	}

}

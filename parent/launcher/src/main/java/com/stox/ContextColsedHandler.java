package com.stox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ContextColsedHandler {

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@EventListener(ContextClosedEvent.class)
	public void onContextClosed(ContextClosedEvent event) {
		taskExecutor.shutdown();
	}

}

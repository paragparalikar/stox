package com.stox.core.ui;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.event.MessageEvent;
import com.stox.core.ui.widget.Toast;

@Component
public class MessagePresenter {

	@EventListener
	public void onMessage(final MessageEvent event) {
		final Toast toast = new Toast(event.getTitle(), event.getMessage(), event.getNode(), event.isCloseable(), event.isAutoClose());
		toast.show();
	}

}

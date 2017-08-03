package com.stox.core.event;

import javafx.scene.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper = false)
public class MessageEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2071850647164278398L;

	private final String title;
	private final String message;
	private final Node node;
	private final boolean closeable;
	private final boolean autoClose;

	public MessageEvent(final Object source, final String message) {
		this(source, null, message);
	}

	public MessageEvent(final Object source, final String title, final String message) {
		this(source, title, message, null);
	}

	public MessageEvent(final Object source, final String title, final String message, final Node node) {
		this(source, title, message, node, true);
	}

	public MessageEvent(final Object source, final String title, final String message, final Node node, final boolean closeable) {
		this(source, title, message, node, closeable, false);
	}

	public MessageEvent(final Object source, final String title, final String message, final Node node, final boolean closeable, final boolean autoClose) {
		super(source);
		this.title = title;
		this.message = message;
		this.node = node;
		this.closeable = closeable;
		this.autoClose = autoClose;
	}

}

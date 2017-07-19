package com.stox.core.model;

import lombok.Data;

@Data
public class Message {

	private String text;

	private String field;

	private MessageType type;

	public Message(final String text, final MessageType type) {
		this(text, null, type);
	}

	public Message(final String text, final String field, final MessageType type) {
		this.text = text;
		this.field = field;
		this.type = type;
	}

}

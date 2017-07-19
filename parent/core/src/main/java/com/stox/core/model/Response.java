package com.stox.core.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class Response<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean success;

	private T payload;

	private List<Message> messages;

	public Response() {
		this(true, null);
	}

	public Response(final T payload) {
		this(true, payload);
	}

	public Response(final boolean success) {
		this(success, null);
	}

	public Response(final boolean success, final T payload) {
		this(success, payload, Collections.emptyList());
	}

	public Response(final boolean success, final T payload, final List<Message> messages) {
		this.payload = payload;
		this.success = success;
		this.messages = messages;
	}

}

package com.stox.core.model;

public enum MessageType {

	ERROR("danger"), WARN("warning"), INFO("info"), SUCCESS("success");

	private final String style;

	private MessageType(final String style) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

}

package com.stox.core.ui.widget;

import javafx.scene.control.Label;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;

public class MessageLabel extends Label {

	public MessageLabel() {
		getStyleClass().add("message");
	}

	public void setMessage(final Message message) {
		setText(null == message ? "" : message.getText());
		setMessageType(null == message ? null : message.getType());
	}

	private void setMessageType(final MessageType messageType) {
		getStyleClass().removeAll("success", "warning", "info", "danger");
		if(null != messageType) {
			getStyleClass().add(messageType.getStyle());
		}
	}
}

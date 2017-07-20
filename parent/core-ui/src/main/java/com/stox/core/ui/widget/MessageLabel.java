package com.stox.core.ui.widget;

import javafx.scene.control.Label;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.util.UiConstant;

public class MessageLabel extends Label {

	public MessageLabel() {
		getStyleClass().add("message");
	}

	public void setMessage(final Message message) {
		setText(null == message ? "" : message.getText());
		setMessageType(null == message ? null : message.getType());
	}

	private void setMessageType(final MessageType messageType) {
		pseudoClassStateChanged(UiConstant.PSEUDO_CLASS_ERROR, MessageType.ERROR.equals(messageType));
		pseudoClassStateChanged(UiConstant.PSEUDO_CLASS_WARN, MessageType.WARN.equals(messageType));
		pseudoClassStateChanged(UiConstant.PSEUDO_CLASS_INFO, MessageType.INFO.equals(messageType));
		pseudoClassStateChanged(UiConstant.PSEUDO_CLASS_SUCCESS, MessageType.SUCCESS.equals(messageType));
	}

}

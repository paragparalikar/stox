package com.stox.core.ui.widget;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

public class MessagePane extends StackPane {

	private final Label label = UiUtil.fullWidth(new Label());
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "icon");
	private final HBox container = UiUtil.classes(UiUtil.fullWidth(UiUtil.box(new HBox(label, closeButton))), "container", "center");

	public MessagePane() {
		getChildren().add(container);
		UiUtil.classes(UiUtil.fullWidth(this), "message-pane");
		closeButton.addEventHandler(ActionEvent.ACTION, event -> {
			final Parent parent = getParent();
			if (null != parent && parent instanceof Pane) {
				final Pane pane = (Pane) parent;
				pane.getChildren().remove(MessagePane.this);
			}
		});
	}

	public void setMessage(final Message message) {
		label.setText(null == message ? "" : message.getText());
		setMessageType(null == message ? null : message.getType());
	}

	private void setMessageType(final MessageType messageType) {
		getStyleClass().removeAll("success", "warning", "info", "danger");
		getStyleClass().add(messageType.getStyle());
	}

}

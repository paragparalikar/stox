package com.stox.core.ui.widget;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.util.UiUtil;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class FormGroup extends VBox {

	public static interface Validator {

		public Message validate();

	}

	private Node node;
	private Validator validator;
	private final MessageLabel messageLabel = UiUtil.classes(UiUtil.fullWidth(new MessageLabel()),"message-sm");
	private final ChangeListener<Boolean> focusChangeListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if (!newValue) {
				validate();
			}
		}
	};

	public FormGroup(final Node label, final Node node, final Validator validator) {
		getStyleClass().add("form-group");
		this.node = node;
		this.validator = validator;
		if (null != label) {
			getChildren().add(label);
		}
		getChildren().addAll(node);
		node.focusedProperty().addListener(focusChangeListener);
	}

	/**
	 * @return true if valid, false otherwise
	 */
	public boolean validate() {
		if (null != validator) {
			final Message message = validator.validate();
			setMessage(message);
			setMessageTypeClasses(null == message ? null : message.getType());
			return null == message;
		}
		return true;
	}

	public void setMessage(final Message message) {
		if (null == message) {
			messageLabel.setMessage(null);
			getChildren().remove(messageLabel);
			setMessageTypeClasses(null);
		} else {
			messageLabel.setMessage(message);
			if (!getChildren().contains(messageLabel)) {
				getChildren().add(messageLabel);
			}
			setMessageTypeClasses(message.getType());
		}
	}

	private void setMessageTypeClasses(final MessageType type) {
		if (null != node) {
			node.getStyleClass().removeAll("danger", "warning", "info", "success");
			if (null != type) {
				String styleClass = null;
				switch (type) {
				case SUCCESS:
					styleClass = "success";
					break;
				case ERROR:
					styleClass = "danger";
					break;
				case WARN:
					styleClass = "warning";
					break;
				case INFO:
				default:
					styleClass = "info";
				}
				if (null != styleClass) {
					node.getStyleClass().add(styleClass);
				}
			}
		}
	}

}

package com.stox.core.ui.widget.validator;

import javafx.scene.control.ToggleGroup;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.widget.FormGroup.Validator;

public class ToggleRequiredValidator implements Validator {

	private final ToggleGroup toggleGroup;
	private String requiredMessage = "Please select a value";

	public ToggleRequiredValidator(final ToggleGroup toggleGroup) {
		this.toggleGroup = toggleGroup;
	}

	@Override
	public Message validate() {
		return null == toggleGroup.getSelectedToggle() ? new Message(requiredMessage, MessageType.ERROR) : null;
	}

	public ToggleRequiredValidator requiredMessage(final String requiredMessage) {
		this.requiredMessage = requiredMessage;
		return this;
	}

}

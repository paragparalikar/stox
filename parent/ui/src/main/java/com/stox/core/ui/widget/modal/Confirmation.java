package com.stox.core.ui.widget.modal;

import com.stox.core.ui.util.UiUtil;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Confirmation extends Modal {
	
	private final Label messageLabel = UiUtil.classes(new Label(), "");
	private final Button actionButton = UiUtil.classes(new Button("Yes"), "primary");
	private final Button cancelButton = UiUtil.classes(new Button("No"), "");
	private final HBox buttonBox = UiUtil.classes(new HBox(cancelButton, actionButton), "button-group", "right");
	
	public Confirmation(final String title, final String message) {
		setContent(messageLabel);
		setButtonGroup(buttonBox);
		cancelButton.addEventHandler(ActionEvent.ACTION, event -> hide());
	}
	
	public Button getCancelButton() {
		return cancelButton;
	}
	
	public Button getActionButton() {
		return actionButton;
	}

}

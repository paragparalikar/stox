package com.stox.example.ui;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.model.Response;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;
import com.stox.core.ui.widget.modal.Modal;
import com.stox.core.ui.widget.validator.TextValidator;
import com.stox.core.util.StringUtil;
import com.stox.example.client.ExampleGroupClient;
import com.stox.example.model.ExampleGroup;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ExampleGroupEditorModal extends Modal {

	private final ExampleGroup exampleGroup;
	private final ExampleGroupClient exampleGroupClient;
	private final TextField nameTextField = UiUtil.classes(new TextField(), "");
	private final FormGroup nameFormGroup = new FormGroup(new Label("Name"), nameTextField,
			new TextValidator(nameTextField, true, 3, 255));
	private final Button actionButton = UiUtil.classes(new Button(), "primary");
	private final Button cancelButton = UiUtil.classes(new Button("Cancel"), "");
	private final HBox buttonBox = UiUtil.classes(new HBox(cancelButton, actionButton), "button-group", "right");

	public ExampleGroupEditorModal(final ExampleGroup exampleGroup, final ExampleGroupClient exampleGroupClient) {
		this.exampleGroupClient = exampleGroupClient;
		this.exampleGroup = null == exampleGroup ? new ExampleGroup() : exampleGroup;
		setContent(nameFormGroup);
		setButtonGroup(buttonBox);
		addStylesheets("styles/example.css");
		getStyleClass().add("primary");
		getStyleClass().add("example-group-editor-modal");
		cancelButton.addEventHandler(ActionEvent.ACTION, event -> hide());
		actionButton.addEventHandler(ActionEvent.ACTION, event -> {
			if (nameFormGroup.validate()) {
				updateModel();
				persistModel();
			}
		});
		nameTextField.setOnAction(event -> {
			actionButton.fire();
		});
		updateView();
	}
	
	private void updateModel() {
		exampleGroup.setName(nameTextField.getText());
	}
	
	private void persistModel() {
		exampleGroupClient.save(exampleGroup, new ResponseCallback<ExampleGroup>() {
			@Override
			public void onSuccess(Response<ExampleGroup> response) {
				hide();
			}

			public void onFailure(Response<ExampleGroup> response, Throwable throwable) {
				final String message = null != throwable && StringUtil.hasText(throwable.getMessage())
						? throwable.getMessage()
						: "Failed to save example group \"" + exampleGroup.getName() + "\"";
				setMessage(new Message(message, MessageType.ERROR));
			};
		});
	}

	private void updateView() {
		nameTextField.setText(exampleGroup.getName());
		actionButton.setText(null == exampleGroup.getId() ? "Create" : "Update");
		setTitle(null == exampleGroup.getId() ? "Create New Example Group" : "Edit \"" + exampleGroup.getName() + "\"");
	}

}

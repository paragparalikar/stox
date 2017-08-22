package com.stox.watchlist.ui;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.model.Response;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;
import com.stox.core.ui.widget.modal.Modal;
import com.stox.core.ui.widget.validator.TextValidator;
import com.stox.core.util.StringUtil;
import com.stox.watchlist.client.WatchlistClient;
import com.stox.watchlist.model.Watchlist;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class WatchlistEditorModal extends Modal {

	private final Watchlist watchlist;
	private final WatchlistClient watchlistClient;
	private final TextField nameTextField = UiUtil.classes(new TextField(), "");
	private final FormGroup nameFormGroup = new FormGroup(new Label("Name"), nameTextField,
			new TextValidator(nameTextField, true, 3, 255));
	private final Button actionButton = UiUtil.classes(new Button(), "primary");
	private final Button cancelButton = UiUtil.classes(new Button("Cancel"), "");
	private final HBox buttonBox = UiUtil.classes(new HBox(cancelButton, actionButton), "button-group", "right");

	public WatchlistEditorModal(final Watchlist watchlist, final WatchlistClient watchlistClient) {
		this.watchlistClient = watchlistClient;
		this.watchlist = null == watchlist ? new Watchlist() : watchlist;
		setContent(nameFormGroup);
		setButtonGroup(buttonBox);
		addStylesheets("styles/watchlist.css");
		getStyleClass().add("primary");
		getStyleClass().add("watchlist-editor-modal");
		cancelButton.addEventHandler(ActionEvent.ACTION, event -> hide());
		actionButton.addEventHandler(ActionEvent.ACTION, event -> {
			if (nameFormGroup.validate()) {
				populateModel();
				persistModel();
			}
		});

		updateView();
	}
	
	private void populateModel() {
		watchlist.setName(nameTextField.getText());
	}
	
	private void persistModel() {
		watchlistClient.save(watchlist, new ResponseCallback<Watchlist>() {
			@Override
			public void onSuccess(Response<Watchlist> response) {
				hide();
			}

			public void onFailure(Response<Watchlist> response, Throwable throwable) {
				final String message = null != throwable && StringUtil.hasText(throwable.getMessage())
						? throwable.getMessage()
						: "Failed to save watchlist \"" + watchlist.getName() + "\"";
				setMessage(new Message(message, MessageType.ERROR));
			};
		});
	}

	private void updateView() {
		actionButton.setText(null == watchlist.getId() ? "Create" : "Update");
		setTitle(null == watchlist.getId() ? "Create New Watchlist" : "Edit \"" + watchlist.getName() + "\"");
	}

}

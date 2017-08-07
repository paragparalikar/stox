package com.stox.data.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import com.stox.core.model.Exchange;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.modal.Notification;
import com.stox.core.util.StringUtil;

public class BarLengthDownloadNotification {

	private final Label titleLabel = UiUtil.fullWidth(new Label("Downloading Data..."));
	private final Label exchangeLabel = UiUtil.fullWidth(new Label());
	private final Label instrumentLabel = UiUtil.fullWidth(new Label());
	private final ProgressBar progressBar = UiUtil.fullWidth(UiUtil.classes(new ProgressBar(), "success"));
	private final VBox container = UiUtil.fullArea(new VBox(titleLabel, exchangeLabel, instrumentLabel, UiUtil.spacer(), progressBar));
	private final Notification notification = Notification.builder().graphic(container).build();

	public BarLengthDownloadNotification(final Exchange exchange) {
		Platform.runLater(() -> {
			exchangeLabel.setText(exchange.getName());
		});
	}

	public void show() {
		notification.show();
	}

	public void hide() {
		notification.hide();
	}

	public void setText(final String text) {
		Platform.runLater(() -> {
			if (StringUtil.hasText(text))
				instrumentLabel.setText(text);
		});
	}

	public void setProgress(final double progress) {
		Platform.runLater(() -> progressBar.setProgress(progress));
	}

}

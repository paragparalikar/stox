package com.stox.data.ui;

import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import com.stox.core.model.Exchange;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.modal.Notification;
import com.stox.core.util.Constant;

public class BarDownloadNotification {

	private final Date start, end;
	private final Label exchangeLabel = UiUtil.fullWidth(new Label());
	private final Label dateLabel = UiUtil.fullWidth(new Label());
	private final VBox labelContainer = UiUtil.classes(new VBox(exchangeLabel, dateLabel), "container");
	private final ProgressBar progressBar = UiUtil.fullWidth(UiUtil.classes(new ProgressBar(), "success"));
	private final VBox container = UiUtil.fullArea(new VBox(labelContainer, progressBar));
	private Notification notification = Notification.builder().graphic(container).build();

	public BarDownloadNotification(final Exchange exchange, final Date start, final Date end) {
		this.start = start;
		this.end = end;
		Platform.runLater(() -> {
			exchangeLabel.setText(exchange.getName());
		});
	}

	public void setDate(final Date date) {
		Platform.runLater(() -> {
			dateLabel.setText(Constant.dateFormat.format(date));
			progressBar.setProgress(((double) (date.getTime() - start.getTime())) / ((double) (end.getTime() - start.getTime())));
		});
	}

	public void show() {
		notification.show();
	}

	public void hide() {
		notification.hide();
	}

}

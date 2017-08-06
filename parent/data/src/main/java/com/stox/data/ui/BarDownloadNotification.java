package com.stox.data.ui;

import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.modal.Notification;
import com.stox.core.util.Constant;

public class BarDownloadNotification {

	private final Date start, end;
	private final Label dateLabel = UiUtil.fullWidth(new Label());
	private final ProgressBar progressBar = UiUtil.fullWidth(new ProgressBar());
	private final VBox container = UiUtil.fullArea(new VBox(dateLabel, progressBar));
	private Notification notification = Notification.builder().graphic(container).style("success").build();

	public BarDownloadNotification(final Date start, final Date end) {
		this.start = start;
		this.end = end;
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
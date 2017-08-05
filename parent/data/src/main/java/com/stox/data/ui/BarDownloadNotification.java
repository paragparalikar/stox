package com.stox.data.ui;

import java.util.Date;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.Constant;

public class BarDownloadNotification {

	private final Date start, end;
	private final Label dateLabel = UiUtil.fullWidth(new Label());
	private final ProgressBar progressBar = UiUtil.fullWidth(new ProgressBar());
	private final VBox container = UiUtil.fullArea(new VBox(dateLabel, progressBar));

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
		Platform.runLater(() -> {
			Notifications.create().darkStyle().position(Pos.TOP_RIGHT).title("Downloading Data...").hideAfter(Duration.INDEFINITE).graphic(container).show(); // TODO
		});
	}

}

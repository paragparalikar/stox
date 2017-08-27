package com.stox.data.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import com.stox.core.intf.HasName;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.modal.Notification;

public class BarLengthDownloadNotification {

	private final Label exchangeLabel = UiUtil.fullWidth(new Label());
	private final Label instrumentLabel = UiUtil.fullWidth(new Label());
	private final Label barSpanLabel = UiUtil.fullWidth(new Label());
	private final VBox labelContainer = UiUtil.classes(new VBox(exchangeLabel, instrumentLabel, barSpanLabel), "container");
	private final ProgressBar progressBar = UiUtil.fullWidth(UiUtil.classes(new ProgressBar(), "success"));
	private final VBox container = UiUtil.fullArea(new VBox(labelContainer, progressBar));
	private final Notification notification = Notification.builder().graphic(container).build();

	public void show() {
		notification.setPrefWidth(400);
		notification.show();
	}

	public void hide() {
		notification.hide();
	}

	private void setValue(final Label label, final HasName hasName) {
		Platform.runLater(() -> {
			if (null != hasName) {
				label.setText(hasName.getName());
			} else {
				label.setText(null);
			}
		});
	}

	public void setInstrument(final Instrument instrument) {
		setValue(instrumentLabel, instrument);
	}

	public void setBarSpan(final BarSpan barSpan) {
		setValue(barSpanLabel, barSpan);
	}

	public void setExchange(final Exchange exchange) {
		setValue(exchangeLabel, exchange);
	}

	public void setProgress(final double progress) {
		Platform.runLater(() -> progressBar.setProgress(progress));
	}

}

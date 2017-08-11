package com.stox.chart.widget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import com.stox.core.model.Bar;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

public class BarInfoPanel extends HBox {

	private final Label open = new Label("O:");
	private final Label openValue = new Label();
	private final Label high = new Label("H:");
	private final Label highValue = new Label();
	private final Label low = new Label("L:");
	private final Label lowValue = new Label();
	private final Label close = new Label("C:");
	private final Label closeValue = new Label();
	private final Label volume = new Label("V:");
	private final Label volumeValue = new Label();
	private final Label date = new Label("D:");
	private final Label dateValue = new Label();
	private final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm");

	public BarInfoPanel() {
		UiUtil.classes(this, "bar-info-panel");
		open.getStyleClass().add("name-label");
		high.getStyleClass().add("name-label");
		low.getStyleClass().add("name-label");
		close.getStyleClass().add("name-label");
		volume.getStyleClass().add("name-label");
		date.getStyleClass().add("name-label");
		openValue.getStyleClass().add("value-label");
		highValue.getStyleClass().add("value-label");
		lowValue.getStyleClass().add("value-label");
		closeValue.getStyleClass().add("value-label");
		volumeValue.getStyleClass().add("value-label");
		dateValue.getStyleClass().add("date-value-label");
		getChildren().addAll(open, openValue, high, highValue, low, lowValue, close, closeValue, volume, volumeValue, date, dateValue);
	}

	public void set(Bar bar) {
		if (null == bar) {
			openValue.setText(null);
			highValue.setText(null);
			lowValue.setText(null);
			closeValue.setText(null);
			volumeValue.setText(null);
			dateValue.setText(null);
		} else {
			openValue.setText(StringUtil.stringValueOf(Math.round(bar.getOpen() * 100) / 100));
			highValue.setText(StringUtil.stringValueOf(Math.round(bar.getHigh() * 100) / 100));
			lowValue.setText(StringUtil.stringValueOf(Math.round(bar.getLow() * 100) / 100));
			closeValue.setText(StringUtil.stringValueOf(Math.round(bar.getClose() * 100) / 100));
			volumeValue.setText(StringUtil.stringValueOf(Math.round(bar.getVolume() * 100) / 100));
			dateValue.setText(dateFormat.format(bar.getDate()));
		}
	}
}

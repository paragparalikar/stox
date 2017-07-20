package com.stox.zerodha.ui;

import java.text.ParseException;
import java.util.Date;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;
import com.stox.core.util.Constant;
import com.stox.core.util.StringUtil;

public class ZerodhaInstrumentFilterView extends VBox {

	private final ChoiceBox<String> exchangeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup exchangeFormGroup = new FormGroup(new Label("Exchange"), exchangeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<String> typeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup typeFormGroup = new FormGroup(new Label("Type"), typeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<Double> strikeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup strikeFormGroup = new FormGroup(new Label("Strike"), strikeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<Date> expiryChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup expiryFormGroup = new FormGroup(new Label("Expiry"), expiryChoiceBox, null); // TODO I18N here

	public ZerodhaInstrumentFilterView() {
		strikeChoiceBox.setConverter(new StrikeStringConverter());
		expiryChoiceBox.setConverter(new ExpiryStringConverter());
		UiUtil.fullArea(this);
		getChildren().addAll(exchangeFormGroup, typeFormGroup, strikeFormGroup, expiryFormGroup);
	}

	public ChoiceBox<String> getExchangeChoiceBox() {
		return exchangeChoiceBox;
	}

	public ChoiceBox<String> getTypeChoiceBox() {
		return typeChoiceBox;
	}

	public FormGroup getTypeFormGroup() {
		return typeFormGroup;
	}

	public ChoiceBox<Double> getStrikeChoiceBox() {
		return strikeChoiceBox;
	}

	public FormGroup getStrikeFormGroup() {
		return strikeFormGroup;
	}

	public ChoiceBox<Date> getExpiryChoiceBox() {
		return expiryChoiceBox;
	}

	public FormGroup getExpiryFormGroup() {
		return expiryFormGroup;
	}
}

class StrikeStringConverter extends StringConverter<Double> {

	@Override
	public String toString(Double object) {
		return null != object ? String.valueOf(object) : "";
	}

	@Override
	public Double fromString(String string) {
		return StringUtil.hasText(string) ? Double.parseDouble(string) : 0;
	}

}

class ExpiryStringConverter extends StringConverter<Date> {

	@Override
	public String toString(Date date) {
		return null != date ? Constant.dateFormat.format(date) : "";
	}

	@Override
	public Date fromString(String string) {
		try {
			return StringUtil.hasText(string) ? Constant.dateFormat.parse(string) : null;
		} catch (ParseException e) {
			e.printStackTrace(); // TODO logging here
		}
		return null;
	}

}

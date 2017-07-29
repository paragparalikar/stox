package com.stox.core.ui.filter;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;

public class FilterView extends VBox {

	private final ChoiceBox<String> exchangeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup exchangeFormGroup = new FormGroup(new Label("Exchange"), exchangeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<String> typeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup typeFormGroup = new FormGroup(new Label("Type"), typeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<String> expiryChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup expiryFormGroup = new FormGroup(new Label("Expiry"), expiryChoiceBox, null); // TODO I18N here

	public FilterView() {
		UiUtil.classes(this, "filter-view");
		getChildren().addAll(exchangeFormGroup, typeFormGroup, expiryFormGroup);
	}

	public ChoiceBox<String> getExchangeChoiceBox() {
		return exchangeChoiceBox;
	}

	public ChoiceBox<String> getTypeChoiceBox() {
		return typeChoiceBox;
	}

	public ChoiceBox<String> getExpiryChoiceBox() {
		return expiryChoiceBox;
	}
}

package com.stox.zerodha.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;
import com.stox.workbench.ui.modal.Modal;

public class ZerodhaInstrumentFilterModal extends Modal {

	private final ChoiceBox<String> exchangeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup exchangeFormGroup = new FormGroup(new Label("Exchange"), exchangeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<String> typeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup typeFormGroup = new FormGroup(new Label("Type"), typeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<String> expiryChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup expiryFormGroup = new FormGroup(new Label("Expiry"), expiryChoiceBox, null); // TODO I18N here

	private final VBox content = UiUtil.classes(new VBox(exchangeFormGroup, typeFormGroup, expiryFormGroup), "");
	private final Button filterButton = UiUtil.classes(new Button("Filter"), "primary"); // TODO I18N here
	private final HBox buttonGroup = UiUtil.classes(new HBox(filterButton), "button-group", "right");

	public ZerodhaInstrumentFilterModal() {
		setTitle("Filter Instruments"); // TODO I18N here
		setContent(content);
		setButtonGroup(buttonGroup);

		addStylesheet("styles/zerodha.css");
		getStyleClass().add("primary");
		getStyleClass().add("zerodha-instrument-filter-modal");
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

	public Button getFilterButton() {
		return filterButton;
	}
}

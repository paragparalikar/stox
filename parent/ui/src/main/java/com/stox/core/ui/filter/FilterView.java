package com.stox.core.ui.filter;

import java.util.Date;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.EqualsAndHashCode;
import lombok.Value;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;

@Value
@EqualsAndHashCode(callSuper = false)
public class FilterView extends VBox {

	private final ChoiceBox<Exchange> exchangeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup exchangeFormGroup = new FormGroup(new Label("Exchange"), exchangeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<InstrumentType> typeChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup typeFormGroup = new FormGroup(new Label("Type"), typeChoiceBox, null); // TODO I18N here
	private final ChoiceBox<Instrument> indexChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup indexFormGroup = new FormGroup(new Label("Index"), indexChoiceBox, null); // TODO I18N here
	private final ChoiceBox<Date> expiryChoiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup expiryFormGroup = new FormGroup(new Label("Expiry"), expiryChoiceBox, null); // TODO I18N here

	public FilterView() {
		UiUtil.classes(this, "filter-view");
		getChildren().addAll(exchangeFormGroup, typeFormGroup);
	}

}

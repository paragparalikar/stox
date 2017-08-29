package com.stox.data.ui;

import java.util.Collection;
import java.util.function.Consumer;

import com.stox.core.ui.HasNameStringConverter;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;
import com.stox.core.ui.widget.modal.Modal;
import com.stox.data.DataProvider;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DataProviderSelectionModal extends Modal {

	private final ChoiceBox<DataProvider> choiceBox = UiUtil.fullWidth(new ChoiceBox<>());
	private final FormGroup formGroup = new FormGroup(new Label("Data Provider"), choiceBox, null); // TODO I18N here
	private final Button selectButton = UiUtil.classes(new Button("Select"), "primary"); // TODO I18N here
	private final Button cancelButton = UiUtil.classes(new Button("Cancel"), ""); // TODO I18N here
	private final HBox buttonGroup = UiUtil.classes(new HBox(cancelButton, selectButton), "button-group", "right");
	private final VBox content = UiUtil.fullArea(UiUtil.classes(new VBox(formGroup, buttonGroup), ""));

	public DataProviderSelectionModal(final Collection<DataProvider> dataProviders, final Consumer<DataProvider> consumer) {
		choiceBox.getItems().addAll(dataProviders);
		choiceBox.setConverter(new HasNameStringConverter<>());
		choiceBox.getSelectionModel().select(0);
		setTitle("Please select a data provider"); // TODO I18N here
		addStylesheets("styles/data.css");
		getStyleClass().add("data-provider-selection-modal");
		setContent(content);

		cancelButton.addEventHandler(ActionEvent.ACTION, event -> {
			hide();
		});
		selectButton.addEventHandler(ActionEvent.ACTION, event -> {
			hide();
			consumer.accept(choiceBox.getValue());
		});
	}
}

package com.stox.data.ui;

import java.util.Collection;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import com.stox.core.intf.Callback;
import com.stox.core.ui.util.UiUtil;
import com.stox.data.DataProvider;
import com.stox.workbench.ui.modal.Modal;

public class DataProviderSelectionModal extends Modal {

	private final Button selectButton = UiUtil.classes(new Button("Select"), ""); // TODO I18N here
	private final ChoiceBox<DataProvider> choiceBox = UiUtil.classes(new ChoiceBox<>());
	private final HBox content = UiUtil.fullArea(UiUtil.box(UiUtil.classes(new HBox(choiceBox, selectButton), "")));

	public DataProviderSelectionModal(final Collection<DataProvider> dataProviders, final Callback<DataProvider, Void> callback) {
		choiceBox.getItems().addAll(dataProviders);
		choiceBox.setConverter(new DataProviderStringConverter());
		choiceBox.getSelectionModel().select(0);
		selectButton.addEventHandler(ActionEvent.ACTION, event -> {
			hide();
			callback.call(choiceBox.getValue());
		});
		setTitle("Please select a data provider"); // TODO I18N here
		setContent(content);
	}
}

package com.stox.navigator.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.modal.Modal;

public class FilterModal extends Modal {

	private final Button cancelButton = new Button("Cancel"); // TODO I18N here
	private final Button filterButton = UiUtil.classes(new Button("Filter"), "primary"); // TODO I18N here
	private final HBox buttonGroup = UiUtil.classes(new HBox(cancelButton, filterButton), "button-group", "right");

	public FilterModal() {
		setTitle("Filter Instruments"); // TODO I18N here
		getStyleClass().add("primary");
		setButtonGroup(buttonGroup);
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Button getFilterButton() {
		return filterButton;
	}

}

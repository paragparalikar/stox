package com.stox.screen;

import com.stox.core.ui.auto.AutoView;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.modal.Modal;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ScreenConfigEditorModal extends Modal {
	
	private final AutoView autoView;
	private final Button actionButton = UiUtil.classes(new Button("Edit"), "primary");
	private final Button cancelButton = UiUtil.classes(new Button("Cancel"), "");
	private final HBox buttonBox = UiUtil.classes(new HBox(cancelButton, actionButton), "button-group", "right");

	public ScreenConfigEditorModal(final ScreenPlot plot) {
		getStyleClass().add("primary");
		setTitle(plot.getName());
		setButtonGroup(buttonBox);
		addStylesheets("styles/screen.css");
		getStyleClass().add("screen-config-modal");
		autoView = new AutoView(plot.getConfig());
		setContent(autoView);
		
		cancelButton.addEventHandler(ActionEvent.ACTION, event -> hide());
		actionButton.addEventHandler(ActionEvent.ACTION, event -> {
			try {
				autoView.updateModel();
				plot.load();
				hide();
			}catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
}

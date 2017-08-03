package com.stox.core.ui.widget;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.stox.core.ui.util.UiUtil;

public class ApplicationStage extends Stage {

	private static ApplicationStage instance;

	public static ApplicationStage getInstance() {
		return instance;
	}

	private final StackPane root = new StackPane();
	private final Pane glass = UiUtil.classes(new Pane(), "glass");

	public ApplicationStage() {
		instance = this;
		setScene(new Scene(root));
	}

	public void showGlass(final boolean value) {
		if (value) {
			if (!root.getChildren().contains(glass)) {
				root.getChildren().add(glass);
			}
		} else {
			root.getChildren().remove(glass);
		}
	}

	public StackPane getRoot() {
		return root;
	}

}

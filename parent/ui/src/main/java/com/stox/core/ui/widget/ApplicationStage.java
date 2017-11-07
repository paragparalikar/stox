package com.stox.core.ui.widget;

import com.stox.core.ui.HasGlass;
import com.stox.core.ui.util.UiUtil;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ApplicationStage extends Stage implements HasGlass{

	private static ApplicationStage instance;

	public static ApplicationStage getInstance() {
		return instance;
	}

	private final Pane glass = UiUtil.classes(new Pane(), "glass");
	private final Pane modalPane = new Pane();
	private final StackPane root = new StackPane(modalPane);

	public ApplicationStage() {
		instance = this;
		initStyle(StageStyle.UNDECORATED);
		modalPane.setBackground(null);
		modalPane.setPickOnBounds(false);
		setScene(new Scene(root));
	}

	public void showModal(final Region node) {
		if (!modalPane.getChildren().contains(node)) {
			modalPane.getChildren().add(node);
		}
	}

	public void hideModal(final Node node) {
		node.layoutXProperty().unbind();
		node.layoutYProperty().unbind();
		modalPane.getChildren().remove(node);
	}

	@Override
	public void showGlass(final boolean value) {
		if (value) {
			if (!root.getChildren().contains(glass)) {
				root.getChildren().add(1, glass);
			}
		} else {
			root.getChildren().remove(glass);
		}
	}

	public StackPane getRoot() {
		return root;
	}

}

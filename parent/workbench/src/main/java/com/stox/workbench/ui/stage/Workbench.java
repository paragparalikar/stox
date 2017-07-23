package com.stox.workbench.ui.stage;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.stox.core.ui.util.UiUtil;

public class Workbench extends Stage {

	private static Workbench instance;

	public static Workbench getInstance() {
		return instance;
	}

	private final SnapToGridPane contentPane = new SnapToGridPane();
	private final WorkbenchTitleBar titleBar = new WorkbenchTitleBar();
	private final WorkbenchToolBar toolBar = new WorkbenchToolBar();
	private final BorderPane container = new BorderPane(contentPane, titleBar, null, toolBar, null);
	private final Pane glass = UiUtil.classes(new Pane(), "glass");
	private final StackPane root = new StackPane(container);
	private final ResizableRelocatableStageDecorator decorator = new ResizableRelocatableStageDecorator(this);

	public Workbench() {
		Workbench.instance = this;
		setScene(new Scene(root));
		decorator.bindTitleBar(titleBar);
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

	public SnapToGridPane getContentPane() {
		return contentPane;
	}

	public WorkbenchTitleBar getTitleBar() {
		return titleBar;
	}

	public WorkbenchToolBar getToolBar() {
		return toolBar;
	}

}

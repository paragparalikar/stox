package com.stox.workbench.ui.stage;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Workbench extends Stage {

	private final SnapToGridPane contentPane = new SnapToGridPane();
	private final WorkbenchTitleBar titleBar = new WorkbenchTitleBar();
	private final WorkbenchToolBar toolBar = new WorkbenchToolBar();
	private final BorderPane root = new BorderPane(contentPane, titleBar, null, toolBar, null);
	private final ResizableRelocatableStageDecorator decorator = new ResizableRelocatableStageDecorator(this);

	public Workbench() {
		setScene(new Scene(root));
		decorator.bindTitleBar(titleBar);
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

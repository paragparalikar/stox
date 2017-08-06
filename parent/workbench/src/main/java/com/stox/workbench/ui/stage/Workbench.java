package com.stox.workbench.ui.stage;

import javafx.scene.layout.BorderPane;

import com.stox.core.ui.ResizableRelocatableWindowDecorator;
import com.stox.core.ui.widget.ApplicationStage;

public class Workbench extends ApplicationStage {

	private static Workbench instance;

	public static Workbench getInstance() {
		return instance;
	}

	private final SnapToGridPane contentPane = new SnapToGridPane();
	private final WorkbenchTitleBar titleBar = new WorkbenchTitleBar();
	private final WorkbenchToolBar toolBar = new WorkbenchToolBar();
	private final BorderPane container = new BorderPane(contentPane, titleBar, null, toolBar, null);
	private final ResizableRelocatableWindowDecorator decorator = new ResizableRelocatableWindowDecorator(this);

	public Workbench() {
		Workbench.instance = this;
		getRoot().getChildren().add(0, container);
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

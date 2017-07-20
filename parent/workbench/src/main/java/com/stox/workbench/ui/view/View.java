package com.stox.workbench.ui.view;

import javafx.scene.layout.BorderPane;

import com.stox.workbench.ui.titlebar.TitleBar;

public abstract class View extends BorderPane {

	public static final int NONE = 0;
	public static final int MOVE = 1;
	public static final int RESIZE = 2;
	public static final double BORDER = 4;

	private int mode = NONE;
	private final String code;
	private final TitleBar titleBar = new TitleBar();

	public View(final String code, final String name, final String icon) {
		this.code = code;
		setTop(titleBar.getNode());
		titleBar.setGraphic(icon);
		titleBar.setTitleText(name);
		titleBar.getNode().getStyleClass().add("primary");
		getStyleClass().addAll("primary", "view");
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getCode() {
		return code;
	}

	public TitleBar getTitleBar() {
		return titleBar;
	}

}

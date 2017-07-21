package com.stox.workbench.ui.view;

import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.titlebar.TitleBar;

public abstract class View extends StackPane {

	public static final int NONE = 0;
	public static final int MOVE = 1;
	public static final int RESIZE = 2;
	public static final double BORDER = 4;

	private int mode = NONE;
	private final String code;
	private final TitleBar titleBar = new TitleBar();
	private final VBox spinner = UiUtil.classes(new VBox(new ProgressIndicator()), "center");
	private final BorderPane container = UiUtil.classes(new BorderPane(null, UiUtil.classes(titleBar.getNode(), "primary"), null, null, null), "primary", "view");

	public View(final String code, final String name, final String icon) {
		this.code = code;
		titleBar.setGraphic(icon);
		titleBar.setTitleText(name);
		getChildren().add(container);
	}

	public void setContent(final Node node) {
		container.setCenter(node);
	}

	public void showSpinner(final boolean value) {
		container.setDisable(value);
		if (value && !getChildren().contains(spinner)) {
			getChildren().add(spinner);
		} else if (!value) {
			getChildren().remove(spinner);
		}
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

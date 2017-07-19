package com.stox.workbench.ui.stage;

import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

public class WorkbenchTitleBar extends HBox {

	private final MenuButton applicationsMenu = UiUtil.classes(new MenuButton("Applications"), "primary"); // TODO I18N here
	private final HBox menuBar = UiUtil.box(UiUtil.classes(new HBox(applicationsMenu), "menu-bar"));
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "last", "fa", "primary");
	private final Button minimizeButton = UiUtil.classes(new Button(Icon.WINDOW_MINIMIZE), "first", "fa", "primary");
	private final Button maximizeButton = UiUtil.classes(new Button(Icon.WINDOW_MAXIMIZE), "middle", "fa", "primary");

	public WorkbenchTitleBar() {
		getStyleClass().addAll("title", "primary");
		getChildren().addAll(menuBar, UiUtil.spacer(), minimizeButton, maximizeButton, closeButton);
	}

	public MenuButton getApplicationsMenu() {
		return applicationsMenu;
	}

	public Button getMinimizeButton() {
		return minimizeButton;
	}

	public Button getMaximizeButton() {
		return maximizeButton;
	}

	public Button getCloseButton() {
		return closeButton;
	}
}

package com.stox.workbench.ui.stage;

import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

public class WorkbenchTitleBar extends HBox {

	private final MenuButton applicationsMenu = UiUtil.classes(new MenuButton("Applications"), "primary"); // TODO I18N here
	private final MenuButton dataProvidersMenu = UiUtil.classes(new MenuButton("Data Providers"), "primary"); // TODO I18N here
	private final MenuButton brokersMenu = UiUtil.classes(new MenuButton("Brokers"), "primary"); // TODO I18N here
	private final HBox menuBar = UiUtil.classes(new HBox(applicationsMenu, dataProvidersMenu, brokersMenu), "menu-bar");
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "last", "icon", "primary");
	private final Button minimizeButton = UiUtil.classes(new Button(Icon.WINDOW_MINIMIZE), "first", "icon", "primary");
	private final Button maximizeButton = UiUtil.classes(new Button(Icon.WINDOW_MAXIMIZE), "middle", "icon", "primary");
	private final HBox buttonGroup = UiUtil.classes(UiUtil.box(new HBox(minimizeButton, maximizeButton, closeButton)), "center");

	public WorkbenchTitleBar() {
		getStyleClass().addAll("title", "primary", "center");
		getChildren().addAll(menuBar, UiUtil.spacer(), buttonGroup);
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

package com.stox.navigator.ui;

import javafx.geometry.Side;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import com.stox.core.model.Instrument;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.View;

public class NavigatorView extends View {

	private final ToggleGroup toggleGroup = new ToggleGroup();
	private final ListView<Instrument> listView = new ListView<Instrument>();
	private final ToggleButton filterButton = UiUtil.classes(new ToggleButton(Icon.FILTER), "icon", "primary");

	public NavigatorView() {
		super(NavigatorUiConstant.CODE, NavigatorUiConstant.NAME, NavigatorUiConstant.ICON);
		setCenter(listView);
		getTitleBar().add(Side.RIGHT, 0, filterButton);
		toggleGroup.getToggles().add(filterButton);
	}

	public ListView<Instrument> getListView() {
		return listView;
	}

	public ToggleButton getFilterButton() {
		return filterButton;
	}

}

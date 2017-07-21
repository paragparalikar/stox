package com.stox.navigator.ui;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import com.stox.core.model.Instrument;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.View;

public class NavigatorView extends View {

	private final ListView<Instrument> listView = new ListView<Instrument>();
	private final Button filterButton = UiUtil.classes(new Button(Icon.FILTER), "icon", "primary");

	public NavigatorView() {
		super(NavigatorUiConstant.CODE, NavigatorUiConstant.NAME, NavigatorUiConstant.ICON);
		setCenter(listView);
		getTitleBar().add(Side.RIGHT, 0, filterButton);
	}

	public ListView<Instrument> getListView() {
		return listView;
	}

	public Button getFilterButton() {
		return filterButton;
	}

}

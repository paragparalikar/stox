package com.stox.navigator.ui;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import com.stox.core.model.Instrument;
import com.stox.core.ui.InstrumentMatcher;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.ListViewSearchTextField;
import com.stox.workbench.ui.view.View;

public class NavigatorView extends View {

	private final ListView<Instrument> listView = new ListView<Instrument>();
	private final Button filterButton = UiUtil.classes(new Button(Icon.FILTER), "icon", "primary");
	private final ToggleButton searchButton = UiUtil.classes(new ToggleButton(Icon.SEARCH), "icon", "primary");
	private final TextField searchTextField = new ListViewSearchTextField<>(listView, new InstrumentMatcher());

	public NavigatorView() {
		super(NavigatorUiConstant.CODE, NavigatorUiConstant.NAME, NavigatorUiConstant.ICON);
		setContent(listView);
		getTitleBar().add(Side.RIGHT, 0, filterButton);
		getTitleBar().add(Side.RIGHT, 0, searchButton);
	}

	public ListView<Instrument> getListView() {
		return listView;
	}

	public Button getFilterButton() {
		return filterButton;
	}

	public ToggleButton getSearchButton() {
		return searchButton;
	}

	public TextField getSearchTextField() {
		return searchTextField;
	}

}

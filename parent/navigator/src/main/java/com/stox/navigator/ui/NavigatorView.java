package com.stox.navigator.ui;

import javafx.scene.control.ListView;

import com.stox.core.model.Instrument;
import com.stox.workbench.ui.view.View;

public class NavigatorView extends View {

	private final ListView<Instrument> listView = new ListView<Instrument>();

	public NavigatorView() {
		super(NavigatorUiConstant.CODE, NavigatorUiConstant.NAME, NavigatorUiConstant.ICON);
		setCenter(listView);
	}

	public ListView<Instrument> getListView() {
		return listView;
	}

}

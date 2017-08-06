package com.stox.core.ui;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;

import com.stox.core.ui.util.UiUtil;

public class BoxChildrenChangeListener implements ListChangeListener<Node> {

	interface Wrapper {
		BoxChildrenChangeListener INSTANCE = new BoxChildrenChangeListener();
	}

	public static BoxChildrenChangeListener getInstance() {
		return Wrapper.INSTANCE;
	}

	private BoxChildrenChangeListener() {

	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends Node> change) {
		UiUtil.box(change.getList());
	}

}

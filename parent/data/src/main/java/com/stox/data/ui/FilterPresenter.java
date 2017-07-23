package com.stox.data.ui;

import javafx.scene.Node;

public interface FilterPresenter {

	Node getView();

	String[] getStylesheets();

	void filter();

}

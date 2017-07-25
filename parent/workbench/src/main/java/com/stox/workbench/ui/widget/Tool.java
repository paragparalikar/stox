package com.stox.workbench.ui.widget;

import java.util.List;

import javafx.scene.Node;

import com.stox.workbench.ui.view.Presenter;

public interface Tool {

	Node getNode();

	void setPresenters(final List<Presenter<?, ?>> presenters);

}

package com.stox.workbench.ui.view;

import javafx.scene.Node;

public interface Container {
	
	boolean contains(final Node content);
	
	void add(final Node content);
	
	void remove(final Node content);

}

package com.stox.core.ui;

import javafx.scene.Node;

public interface Container extends HasSpinner{
	
	boolean contains(final Node content);
	
	void add(final Node content);
	
	void remove(final Node content);

}

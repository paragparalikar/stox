package com.stox.workbench.ui.view;

public interface Dockable {

	void setSelected(boolean value);

	void setDefaultPosition();
	
	void position();

	void updateGrid();

	void setPosition(double x, double y, double width, double height);
	
}

package com.stox.workbench.ui.view;

public interface PresenterProvider {

	String getViewCode();

	String getViewName();

	String getViewIcon();

	Presenter<?, ?> create();

}

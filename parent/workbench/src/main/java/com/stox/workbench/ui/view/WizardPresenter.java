package com.stox.workbench.ui.view;

public interface WizardPresenter {
	
	String getCode();
	
	String getViewState();
	
	void setViewState(final String state);

	void present(final Container container);
	
}

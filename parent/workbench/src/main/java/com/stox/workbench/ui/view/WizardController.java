package com.stox.workbench.ui.view;

public interface WizardController<ID> {

	void next(ID nextPresenterId);
	
	void previous(ID previousPresenterId);
	
}

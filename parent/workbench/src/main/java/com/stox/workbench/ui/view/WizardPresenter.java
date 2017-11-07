package com.stox.workbench.ui.view;

import com.stox.core.intf.HasId;
import com.stox.core.model.Message;
import com.stox.core.ui.Container;

public interface WizardPresenter<ID> extends HasId<ID> {
	
	void present(Container container);
	
	String getTitleText();
	
	Message validate();
	
	ID getNextPresenterId();
	
	ID getPreviousPresenterId();
	
}

package com.stox.core.ui.auto;

import com.stox.core.ui.Container;

public interface AutoPresenter {

	void present(final Container container);
	
	void updateModel();
	
	void updateView();
	
}

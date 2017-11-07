package com.stox.workbench.ui.view;

import com.stox.core.intf.HasLifecycle;
import com.stox.core.intf.Persistable;
import com.stox.core.ui.Container;

public interface Presenter<V, S> extends HasLifecycle, Persistable {
	
	void present(Container container, S viewState);
	
	void remove(Container container);

	V getView();

	S getViewState();
	
	void setViewSate(S viewState);
	
}

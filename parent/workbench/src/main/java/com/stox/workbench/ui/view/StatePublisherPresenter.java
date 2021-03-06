package com.stox.workbench.ui.view;

import com.stox.core.util.StringUtil;
import com.stox.workbench.model.ViewState;
import com.stox.workbench.ui.view.Link.State;

public abstract class StatePublisherPresenter<V extends View, S extends ViewState> extends LinkedPresenter<V, S> {

	public void publish(final State state) {
		if (StringUtil.hasText(state.getInstrumentId())) {
			getLinkButton().getLink().setState(state);
		}
	}

}

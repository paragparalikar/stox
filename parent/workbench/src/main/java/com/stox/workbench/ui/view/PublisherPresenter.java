package com.stox.workbench.ui.view;

import com.stox.workbench.model.ViewState;
import com.stox.workbench.ui.view.Link.State;

public abstract class PublisherPresenter<V extends View, S extends ViewState> extends LinkedPresenter<V, S> {

	public void publish(final State state) {
		if (null != state.getInstrumentCode() && null != state.getBarSpan()) {
			getLinkButton().getLink().setState(state);
		}
	}

}

package com.stox.workbench.ui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import com.stox.workbench.model.ViewState;
import com.stox.workbench.ui.view.Link.State;

public abstract class SubscriberPresenter<V extends View, S extends ViewState> extends LinkedPresenter<V, S> {

	private ChangeListener<State> stateChangeListener = new ChangeListener<State>() {
		@Override
		public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
			setLinkState(newValue);
		}
	};

	@Override
	public void start() {
		super.start();
		final LinkButton linkButton = getLinkButton();
		setLinkState(linkButton.getLink().getState());
		linkButton.getLink().stateProperty().addListener(stateChangeListener);
		linkButton.linkProperty().addListener((observable, oldValue, newValue) -> {
			oldValue.stateProperty().removeListener(stateChangeListener);
			newValue.stateProperty().addListener(stateChangeListener);
			setLinkState(newValue.getState());
		});
	}

	public abstract void setLinkState(final State state);

	@Override
	protected void populateViewState(S viewState) {
		super.populateViewState(viewState);
		viewState.setLinkOrdinal(getLinkButton().getLink().ordinal());
	}

}

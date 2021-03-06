package com.stox.workbench.ui.view;

import javafx.geometry.Side;

import com.stox.workbench.model.ViewState;
import com.stox.workbench.ui.view.Link.State;

public abstract class LinkedPresenter<V extends View, S extends ViewState> extends AbstractDockablePublishingPresenter<V, S> {

	private final LinkButton linkButton = new LinkButton();

	protected LinkButton getLinkButton() {
		return linkButton;
	}

	@Override
	public void start() {
		super.start();
		getView().getTitleBar().add(Side.LEFT, 0, linkButton);
	}

	@Override
	protected void populateViewState(S viewState) {
		super.populateViewState(viewState);
		viewState.setLinkOrdinal(linkButton.getLink().ordinal());
	}

	@Override
	public void setViewSate(S viewState) {
		super.setViewSate(viewState);
		linkButton.setLink(Link.values()[viewState.getLinkOrdinal()]);
	}

	public State getLinkState() {
		return linkButton.getLink().getState();
	}

}
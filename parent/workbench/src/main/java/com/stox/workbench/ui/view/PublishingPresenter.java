package com.stox.workbench.ui.view;

import org.springframework.context.ApplicationEvent;

import com.stox.workbench.model.ViewState;

public interface PublishingPresenter<V extends View, S extends ViewState> extends Presenter<V,S> {

	void publish(final ApplicationEvent event);
	
}

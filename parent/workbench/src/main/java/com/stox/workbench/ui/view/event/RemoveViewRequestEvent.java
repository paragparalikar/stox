package com.stox.workbench.ui.view.event;

import org.springframework.context.ApplicationEvent;

import com.stox.workbench.ui.view.Presenter;

import lombok.Getter;

@Getter
public class RemoveViewRequestEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	private final Presenter<?,?> presenter;

	public RemoveViewRequestEvent(final Presenter<?,?> presenter) {
		super(presenter);
		this.presenter = presenter;
	}

}

package com.stox.workbench.ui.view.event;

import org.springframework.context.ApplicationEvent;

import com.stox.workbench.ui.view.View;

public class RemoveViewRequestEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	private final View view;

	public RemoveViewRequestEvent(final Object source, final View view) {
		super(source);
		this.view = view;
	}

	public View getView() {
		return view;
	}

}

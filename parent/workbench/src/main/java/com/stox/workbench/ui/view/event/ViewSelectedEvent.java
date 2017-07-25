package com.stox.workbench.ui.view.event;

import lombok.EqualsAndHashCode;
import lombok.Value;

import org.springframework.context.ApplicationEvent;

import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.View;

@Value
@EqualsAndHashCode(callSuper = false, exclude = { "view", "presenter" })
public class ViewSelectedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final View view;
	private final Presenter<?, ?> presenter;

	public ViewSelectedEvent(final Presenter<?, ?> presenter, final View view) {
		super(view);
		this.view = view;
		this.presenter = presenter;
	}

}

package com.stox.screener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.workbench.ui.view.StatePublisherPresenter;

import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class ScreenerPresenter extends StatePublisherPresenter<ScreenerView, ScreenerViewState>{

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private TaskExecutor taskExecutor;
	
	private final ScreenerView view = new ScreenerView();
	private final ScreenerViewState screenerViewState = new ScreenerViewState();
	
	@Override
	public ScreenerView getView() {
		return view;
	}
	
	@Override
	public ScreenerViewState getViewState() {
		populateViewState(screenerViewState);
		return screenerViewState;
	}

	@Override
	public void setDefaultPosition() {
		if (null != view.getParent()) {
			final Pane pane = (Pane) view.getParent();
			setPosition(0, 0, pane.getWidth() / 6, pane.getHeight());
		}
	}

	@Override
	public void publish(ApplicationEvent event) {
		eventPublisher.publishEvent(event);
	}

}

package com.stox.chart.view;

import javafx.scene.layout.Pane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.SubscriberPresenter;

@Component
@Scope("prototype")
public class ChartPresenter extends SubscriberPresenter<ChartView, ChartViewState> {

	private final ChartView view = new ChartView();

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public void setLinkState(State state) {

	}

	@Override
	public ChartView getView() {
		return view;
	}

	@Override
	public ChartViewState getViewState() {
		final ChartViewState viewState = new ChartViewState();
		populateViewState(viewState);
		return viewState;
	}

	@Override
	public void setDefaultPosition() {
		if (null != view.getParent()) {
			final Pane pane = (Pane) view.getParent();
			setPosition(pane.getWidth() / 4, pane.getHeight() / 4, pane.getWidth() / 2, pane.getHeight() / 2);
		}
	}

	@Override
	public void publish(ApplicationEvent event) {
		eventPublisher.publishEvent(event);
	}

}

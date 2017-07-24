package com.stox.chart.view;

import java.util.Date;
import java.util.List;

import javafx.scene.layout.Pane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.Response;
import com.stox.data.DataClient;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.SubscriberPresenter;

@Component
@Scope("prototype")
public class ChartPresenter extends SubscriberPresenter<ChartView, ChartViewState> {

	private final ChartView view = new ChartView();

	@Autowired
	private DataClient dataClient;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	public void setLinkState(State state) {
		if (null != state) {
			dataClient.loadBars(state.getInstrumentCode(), state.getBarSpan(), new Date(), new Date(), new ResponseCallback<List<Bar>>() {
				@Override
				public void onSuccess(Response<List<Bar>> response) {
					view.getPrimaryChart().getPrimaryPricePlot().getModels().setAll(response.getPayload());
				}

				@Override
				public void onFailure(Response<List<Bar>> response, Throwable throwable) {
					if (null != throwable) {
						throwable.printStackTrace();
					}
				}
			});
		}
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

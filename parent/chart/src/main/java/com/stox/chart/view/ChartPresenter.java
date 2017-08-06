package com.stox.chart.view;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import javafx.scene.layout.Pane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.chart.event.BarRequestEvent;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.util.ChartUtil;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.model.Response;
import com.stox.core.util.StringUtil;
import com.stox.data.DataClient;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.SubscriberPresenter;

@Component("chartPresenter")
@Scope("prototype")
public class ChartPresenter extends SubscriberPresenter<ChartView, ChartViewState> {

	private final ChartView view = new ChartView();

	@Autowired
	private DataClient dataClient;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public ChartPresenter() {
		view.addEventHandler(BarRequestEvent.TYPE, event -> {
			loadBars(event.getInstrumentId(), event.getBarSpan(), event.getFrom(), event.getTo(), event.getCallback());
		});
	}

	@Async
	@Override
	public void setLinkState(State state) {
		if (null != state && StringUtil.hasText(state.getInstrumentId())) {
			view.showSpinner(true);
			final Date to = new Date(0 >= state.getDate() ? System.currentTimeMillis() : state.getDate());
			final Date from = ChartUtil.getFrom(to, state.getBarSpan());
			view.setBarSpan(null == state.getBarSpan() ? view.getBarSpan() : state.getBarSpan());
			view.setFrom(from);
			view.setTo(to);
			final PrimaryPricePlot primaryPricePlot = view.getPrimaryChart().getPrimaryPricePlot();
			dataClient.getInstrument(state.getInstrumentId(), new ResponseCallback<Instrument>() {
				@Override
				public void onSuccess(Response<Instrument> response) {
					primaryPricePlot.setInstrument(response.getPayload());
					primaryPricePlot.load();
				}

				@Override
				public void onFailure(Response<Instrument> response, Throwable throwable) {
					view.setMessage(new Message(throwable.getMessage(), MessageType.ERROR));
				}

				@Override
				public void onDone() {
					view.showSpinner(false);
				}
			});
		}
	}

	private void loadBars(final String instrumentId, final BarSpan barSpan, final Date from, final Date to, final ResponseCallback<List<Bar>> callback) {
		view.showSpinner(true);
		dataClient.loadBars(instrumentId, barSpan, from, to, new ResponseCallback<List<Bar>>() {
			@Override
			public void onSuccess(Response<List<Bar>> response) {
				callback.onSuccess(response);
			}

			@Override
			public void onFailure(Response<List<Bar>> response, Throwable throwable) {
				if (throwable instanceof FileNotFoundException) {
					view.setMessage(new Message("No data available for \"" + instrumentId + "\"", MessageType.ERROR));
				} else {
					view.setMessage(new Message(throwable.getMessage(), MessageType.ERROR));
				}
				callback.onFailure(response, throwable);
			}

			@Override
			public void onDone() {
				view.showSpinner(false);
				callback.onDone();
			}
		});
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

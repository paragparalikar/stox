package com.stox.chart.view;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.chart.drawing.Drawing;
import com.stox.chart.drawing.DrawingFactory;
import com.stox.chart.drawing.DrawingStateClient;
import com.stox.chart.event.BarRequestEvent;
import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.util.ChartUtil;
import com.stox.core.intf.DelayedResponseCallback;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.model.Response;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.ui.TargetAwareMenuItemProvider;
import com.stox.core.util.StringUtil;
import com.stox.data.DataClient;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.SubscriberPresenter;

import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class ChartPresenter extends SubscriberPresenter<ChartView, ChartViewState> {

	private final ChartView view = new ChartView();

	@Autowired
	private DataClient dataClient;

	@Autowired
	private DrawingStateClient drawingStateClient;

	private final DrawingFactory drawingFactory = new DrawingFactory();
	
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private InstrumentRepository instrumentRepository;

	public ChartPresenter() {
		view.addEventHandler(BarRequestEvent.TYPE, event -> {
			final Instrument instrument = instrumentRepository.getInstrument(event.getInstrumentId());
			loadBars(instrument, event.getBarSpan(), event.getFrom(), event.getTo(), event.getCallback());
		});
		view.getSplitPane().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			if(MouseButton.SECONDARY.equals(event.getButton())) {
				final ContextMenu contextMenu = view.getContextMenu();
				if(contextMenu.getItems().isEmpty()) {
					final Collection<TargetAwareMenuItemProvider> providers = applicationContext.getBeansOfType(TargetAwareMenuItemProvider.class).values();
					providers.forEach(provider -> {
						if(provider.supports(view)) {
							contextMenu.getItems().add(provider.build(view));
						}
					});
				}
				contextMenu.show(view.getScene().getWindow());
			}
		});
	}

	@Async
	@Override
	public void setLinkState(State state) {
		saveDrawings();
		if (null != state && StringUtil.hasText(state.getInstrumentId())) {
			view.showSpinner(true);

			final PrimaryPricePlot primaryPricePlot = view.getPrimaryChart().getPrimaryPricePlot();
			final Instrument instrument = instrumentRepository.getInstrument(state.getInstrumentId());
			primaryPricePlot.setInstrument(instrument);

			view.setBarSpan(null == state.getBarSpan() ? view.getBarSpan() : state.getBarSpan());
			final Date to = new Date(0 >= state.getDate() ? System.currentTimeMillis() : state.getDate());
			final Date from = ChartUtil.getFrom(to, view.getBarSpan());
			view.setFrom(from);
			view.setTo(to);

			primaryPricePlot.load();
			loadDrawings();
		}
	}

	private void loadDrawings() {
		view.getPrimaryChart().getDrawings().clear();
		drawingStateClient.load(view.getPrimaryChart().getPrimaryPricePlot().getInstrument(),
				new ResponseCallback<List<Drawing.State<?>>>() {
					@Override
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void onSuccess(final Response<List<Drawing.State<?>>> response) {
						if (null != response.getPayload()) {
							final List<Drawing<?>> drawings = response.getPayload().stream().map(state -> {
								final Drawing drawing = drawingFactory.create(state.getCode(), view.getPrimaryChart());
								drawing.getState().copy(state);
								return drawing;
							}).collect(Collectors.toList());
							view.getPrimaryChart().getDrawings().addAll(drawings);
							view.getPrimaryChart().setDirty();
						}
					}
				});
	}
	
	private void saveDrawings() {
		final Instrument instrument = view.getPrimaryChart().getPrimaryPricePlot().getInstrument();
		if(null != instrument) {
			final List<Drawing.State<?>> states = view.getPrimaryChart().getDrawings().stream().map(Drawing::getState).collect(Collectors.toList());
			drawingStateClient.save(instrument, states, new ResponseCallback<Void>() {
				@Override
				public void onSuccess(Response<Void> response) {
					
				}
			});
		}
	}

	private void loadBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to,
			final ResponseCallback<List<Bar>> callback) {
		view.showSpinner(true);
		dataClient.loadBars(instrument, barSpan, from, to, new DelayedResponseCallback<List<Bar>>() {
			@Override
			public void onSuccess(Response<List<Bar>> response) {
				callback.onSuccess(response);
			}

			@Override
			public void onDelayedSuccess(Response<List<Bar>> response) {
				if (callback instanceof DelayedResponseCallback) {
					final DelayedResponseCallback<List<Bar>> delayedCallback = (DelayedResponseCallback<List<Bar>>) callback;
					delayedCallback.onDelayedSuccess(response);
				}
			}

			@Override
			public void onFailure(Response<List<Bar>> response, Throwable throwable) {
				if (throwable instanceof FileNotFoundException) {
					view.setMessage(
							new Message("No data available for \"" + instrument.getName() + "\"", MessageType.ERROR));
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

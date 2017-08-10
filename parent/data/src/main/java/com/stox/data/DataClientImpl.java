package com.stox.data;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.DelayedResponseCallback;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.core.repository.BarRepository;

@Async
@Component
public class DataClientImpl implements DataClient {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private DataProviderManager dataProviderManager;

	@Autowired
	private BarRepository barRepository;

	@Autowired
	private TaskExecutor taskExecutor;

	public void loadBars1(Instrument instrument, BarSpan barSpan, Date from, Date to, ResponseCallback<List<Bar>> callback) {
		dataProviderManager.execute(dataProvider -> {
			try {
				final List<Bar> bars = dataProvider.getBars(instrument, barSpan, from, to);
				callback.onSuccess(new Response<>(bars));
			} catch (Exception e) {
				callback.onFailure(null, e);
			} finally {
				callback.onDone();
			}
			return null;
		});
	}

	@Override
	public void loadBars(Instrument instrument, BarSpan barSpan, Date from, Date to, final ResponseCallback<List<Bar>> callback) {
		dataProviderManager.execute(dataProvider -> {
			try {
				synchronized (callback) {
					if (callback instanceof DelayedResponseCallback) {
						taskExecutor.execute(() -> {
							try {
								final Date date = barRepository.getLastTradingDate(instrument.getId(), barSpan);
								final List<Bar> bars = dataProvider.getBars(instrument, barSpan, date, to);
								final DelayedResponseCallback<List<Bar>> delayedCallback = (DelayedResponseCallback<List<Bar>>) callback;
								synchronized (callback) {
									delayedCallback.onDelayedSuccess(new Response<>(bars));
								}
							} catch (Exception e) {
							}
						});
					}
					final List<Bar> bars = barRepository.find(instrument.getId(), barSpan, from, to);
					callback.onSuccess(new Response<>(bars));
				}
			} catch (Exception e) {
				callback.onFailure(null, e);
			} finally {
				callback.onDone();
			}
			return null;
		});
	}

}

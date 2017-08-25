package com.stox.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
import com.stox.core.util.Constant;
import com.stox.data.event.DataProviderChangedEvent;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Async
@Component
public class DataClientImpl implements DataClient{

	@Autowired
	private DataProviderManager dataProviderManager;

	@Autowired
	private BarRepository barRepository;

	@Autowired
	private TaskExecutor taskExecutor;
	
	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();
	
	@Override
	public void register(TickConsumer consumer) {
		tickConsumerRegistry.register(consumer);
		dataProviderManager.execute(dataProvider -> {
			dataProvider.register(consumer);
			return null;
		});
	}
	
	@Override
	public void unregister(TickConsumer consumer) {
		tickConsumerRegistry.unregister(consumer);
		dataProviderManager.execute(dataProvider -> {
			dataProvider.unregister(consumer);
			return null;
		});
	}
	
	@EventListener
	public void onDataProviderChanged(final DataProviderChangedEvent event) {
		final DataProvider oldDataProvider = event.getOldDataProvider();
		if(null != oldDataProvider) {
			tickConsumerRegistry.getTickConsumers().forEach(consumer -> oldDataProvider.unregister(consumer));
		}
		final DataProvider dataProvider = event.getDataProvider();
		if(null != dataProvider) {
			tickConsumerRegistry.getTickConsumers().forEach(consumer -> dataProvider.register(consumer));
		}
	}
	
	@PreDestroy
	public void preDestroy() {
		final DataProvider dataProvider = dataProviderManager.getSelectedDataProvider();
		if(null != dataProvider) {
			tickConsumerRegistry.getTickConsumers().forEach(consumer -> dataProvider.unregister(consumer));
		}
	}

	@Override
	public void loadBars(Instrument instrument, BarSpan barSpan, Date from, Date to, ResponseCallback<List<Bar>> callback) {
		dataProviderManager.execute(dataProvider -> {
			try {
				final List<Bar> bars = new ArrayList<>();
				final Date date = barRepository.getLastTradingDate(instrument.getId(), barSpan);
				if (date.before(to)) {
					synchronized (bars) {
						taskExecutor.execute(() -> {
							final List<Bar> downloadedBars = new ArrayList<Bar>();
							try {

								downloadedBars.addAll(dataProvider.getBars(instrument, barSpan, date, to));
								if (!downloadedBars.isEmpty()) {
									taskExecutor.execute(() -> {
										barRepository.save(downloadedBars, instrument.getId(), barSpan);
									});
								}
								if (log.isDebugEnabled()) {
									final String fromText = Constant.dateFormatFull.format(from);
									final String toText = Constant.dateFormatFull.format(to);
									log.debug("Downloaded " + downloadedBars.size() + " bars for " + instrument.getName() + " From : " + fromText + " To : " + toText);
								}
							} catch (Exception exception) {
								exception.printStackTrace();
							} finally {
								synchronized (bars) {
									try {
										bars.addAll(0, downloadedBars);
									} finally {
										bars.notifyAll();
									}
								}
							}
						});
						bars.addAll(barRepository.find(instrument.getId(), barSpan, from, to));
						if (log.isDebugEnabled()) {
							final String fromText = Constant.dateFormatFull.format(from);
							final String toText = Constant.dateFormatFull.format(to);
							log.debug("Loaded " + bars.size() + " bars from repository for " + instrument.getName() + " From : " + fromText + " To : " + toText);
						}
						bars.wait();
					}
				} else {
					bars.addAll(barRepository.find(instrument.getId(), barSpan, from, to));
				}
				Collections.sort(bars);
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
	public void loadBars(Instrument instrument, BarSpan barSpan, Date from, Date to, final DelayedResponseCallback<List<Bar>> callback) {
		dataProviderManager.execute(dataProvider -> {
			try {
				synchronized (callback) {
					final Date date = barRepository.getLastTradingDate(instrument.getId(), barSpan);
					if(null != date) {
						if (date.before(to)) {
							taskExecutor.execute(() -> {
								try {
									final List<Bar> bars = dataProvider.getBars(instrument, barSpan, date, to);
									synchronized (callback) {
										if (log.isDebugEnabled()) {
											final String fromText = Constant.dateFormatFull.format(from);
											final String toText = Constant.dateFormatFull.format(to);
											log.debug("Downloaded " + bars.size() + " bars for " + instrument.getName() + " From : " + fromText + " To : " + toText);
										}
										callback.onDelayedSuccess(new Response<>(bars));
										if (null != bars && !bars.isEmpty()) {
											taskExecutor.execute(() -> {
												barRepository.save(bars, instrument.getId(), barSpan);
											});
										}
									}
								} catch (Exception e) {
								}
							});
						}
						final List<Bar> bars = barRepository.find(instrument.getId(), barSpan, from, to);
						if (log.isDebugEnabled()) {
							final String fromText = Constant.dateFormatFull.format(from);
							final String toText = Constant.dateFormatFull.format(to);
							log.debug("Loaded " + bars.size() + " bars from repository for " + instrument.getName() + " From : " + fromText + " To : " + toText);
						}
						callback.onSuccess(new Response<>(bars));
					}else {
						log.debug("No data available for "+instrument.getName()+" for barSpan "+barSpan.getName());
						callback.onSuccess(new Response<>(Collections.emptyList()));
					}
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

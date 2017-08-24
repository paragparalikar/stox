package com.stox.google.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.DataProvider;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;
import com.stox.google.data.bar.GoogleBarDownloader;

@Component
public class GoogleDataProvider implements DataProvider {

	@Autowired
	private Environment environment;
	
	private boolean cancelled;
	
	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();
	
	private final Map<TickConsumer, GoogleTickWrapper> tickCache = new WeakHashMap<>();
	
	@PreDestroy
	public void preDestroy() {
		cancelled = true;
	}

	@Override
	public void register(TickConsumer consumer) {
		synchronized (tickConsumerRegistry) {
			tickConsumerRegistry.register(consumer);
		}
	}

	@Override
	public void unregister(TickConsumer consumer) {
		synchronized (tickConsumerRegistry) {
			tickConsumerRegistry.unregister(consumer);
		}
	}

	@Scheduled(fixedDelay = 60000)
	public void poll() {
		if(!cancelled) {
			tickConsumerRegistry.getTickConsumers().parallelStream().forEach(consumer -> {
				try {
					if(!cancelled && null != consumer) {
						final BarSpan barSpan = consumer.getBarSpan();
						final Instrument instrument = consumer.getInstrument();
						if(null != barSpan && null != instrument) {
							final Date to = new Date();
							final Date from = new Date(barSpan.previous(to.getTime()));
							final List<Bar> bars = getBars(instrument, barSpan, from, to);
							if(null != bars && !bars.isEmpty()) {
								final GoogleTickWrapper previousTick = tickCache.get(consumer);
								final GoogleTickWrapper tick = new GoogleTickWrapper(bars.get(0), barSpan, instrument);
								if(null == previousTick || !previousTick.equals(tick)) {
									tickCache.put(consumer, tick);
									consumer.accept(tick);
								}
							}
						}
					}
				}catch(final Exception exception) {
					exception.printStackTrace();
				}
			});
		}
	}

	@Override
	public void login(Callback<Void, Void> callback) throws Throwable {
		callback.call(null);
	}

	@Override
	public boolean isLoggedIn() {
		return true;
	}

	@Override
	public String getName() {
		return "Google Finance";
	}

	@Override
	public String getCode() {
		return "google";
	}

	@Override
	public List<Bar> getBars(Instrument instrument, BarSpan barSpan, Date from, Date to) throws Exception {
		final GoogleBarDownloader downloader = new GoogleBarDownloader(
				environment.getProperty("com.stox.google.data.download.url.bar"), from, barSpan, instrument);
		final List<Bar> bars = downloader.download();
		Collections.sort(bars);
		return bars;
	}

}

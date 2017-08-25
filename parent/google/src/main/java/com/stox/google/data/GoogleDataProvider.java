package com.stox.google.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

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
import com.stox.google.data.tick.GoogleTick;
import com.stox.google.data.tick.GoogleTickDownloader;

@Component
public class GoogleDataProvider implements DataProvider {

	@Autowired
	private Environment environment;

	private boolean cancelled;

	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();

	private final Map<TickConsumer, GoogleTick> tickCache = new WeakHashMap<>();

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

	@Scheduled(fixedDelay = 1000)
	public void poll() {
		if (!cancelled) {
			try {
				final GoogleTickDownloader tickDownloader = new GoogleTickDownloader(
						environment.getProperty("com.stox.google.data.download.url.tick"),
						tickConsumerRegistry.getTickConsumers().stream().map(TickConsumer::getInstrument).collect(Collectors.toList()));
				final Map<String, GoogleTick> result = tickDownloader.download();
				tickConsumerRegistry.getTickConsumers().parallelStream().forEach(tickConsumer -> {
					if(null != tickConsumer) {
						final Instrument instrument = tickConsumer.getInstrument();
						if(null != instrument) {
							final GoogleTick googleTick = result.get(instrument.getExchangeCode());
							if (null != googleTick && !googleTick.equals(tickCache.get(tickConsumer))) {
								tickCache.put(tickConsumer, googleTick);
								tickConsumer.accept(googleTick);
							}
						}
					}
				});
			} catch (final Exception exception) {
				exception.printStackTrace();
			}
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

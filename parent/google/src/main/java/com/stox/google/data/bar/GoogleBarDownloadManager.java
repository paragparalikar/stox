package com.stox.google.data.bar;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.data.ui.BarLengthDownloadNotification;

@Component
@PropertySource("classpath:google.properties")
public class GoogleBarDownloadManager {

	private static final BarSpan[] BARSPANS = { BarSpan.H, BarSpan.M30, BarSpan.M15, BarSpan.M10, BarSpan.M5, BarSpan.M1 };

	@Autowired
	private ScheduledExecutorService taskExecutor;

	@Autowired
	private Environment environment;

	@Autowired
	private BarRepository barRepository;

	@Autowired
	private InstrumentRepository instrumentRepository;

	private volatile boolean busy;

	private volatile boolean cancelled;

	private volatile int total = 0;

	private BarLengthDownloadNotification notification;

	private final AtomicInteger counter = new AtomicInteger(0);

	@PreDestroy
	public void preDestroy() {
		this.cancelled = true;
	}

	@EventListener({ ContextRefreshedEvent.class, InstrumentsChangedEvent.class })
	public void downloadIfIdle() {
		if (!busy) {
			busy = true;
			taskExecutor.schedule(() -> {
				try {
					download();
				} catch (final Exception e) {
					e.printStackTrace();
				} finally {
					busy = false;
				}
			}, 3, TimeUnit.SECONDS);
		}
	}

	private void download() throws Exception {
		final String indexId = environment.getProperty("com.stox.google.data.download.bar.index");
		final Instrument indexInstrument = instrumentRepository.getInstrument(indexId);
		final List<Instrument> components = instrumentRepository.getComponentInstruments(indexInstrument);

		notification = new BarLengthDownloadNotification();
		notification.show();
		total = (1 + components.size()) * BARSPANS.length;
		counter.set(total);

		download(indexInstrument.withSymbol(environment.getProperty("com.stox.google.mapping." + indexId, indexId)));
		components.forEach(instrument -> {
			try {
				download(instrument);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void download(final Instrument instrument) throws Exception {
		for (final BarSpan barSpan : BARSPANS) {
			taskExecutor.execute(() -> {
				try {
					if (cancelled) {
						return;
					}
					if (null != notification) {
						notification.setBarSpan(barSpan);
						notification.setInstrument(instrument);
						notification.setExchange(instrument.getExchange());
					}
					download(instrument, barSpan);
				} catch (Exception e) {
				} finally {
					if (null != notification) {
						final int remaining = counter.decrementAndGet();
						if (0 == remaining) {
							notification.hide();
							notification = null;
							total = 0;
							counter.set(0);
						} else {
							notification.setProgress(((((double) total) - ((double) remaining)) / (total)));
						}
					}
				}
			});
		}
	}

	private void download(final Instrument instrument, final BarSpan barSpan) throws Exception {
		final Date start = getLastDownloadDate(instrument, barSpan);
		final GoogleBarDownloader downloader = new GoogleBarDownloader(environment.getProperty("com.stox.google.data.download.url.bar"), start, barSpan, instrument);
		final List<Bar> bars = downloader.download();
		barRepository.save(bars, instrument.getId(), barSpan);
	}

	private Date getLastDownloadDate(final Instrument instrument, final BarSpan barSpan) {
		final Date date = barRepository.getLastTradingDate(instrument.getId(), barSpan);
		if (null == date) {
			return new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(15));
		}
		return date;
	}
}

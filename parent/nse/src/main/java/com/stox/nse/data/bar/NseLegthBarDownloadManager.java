package com.stox.nse.data.bar;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.repository.LineRepository;
import com.stox.core.util.StringUtil;
import com.stox.data.event.BarLengthDownloadCompletedEvent;
import com.stox.data.ui.BarLengthDownloadNotification;
import com.stox.nse.data.NseDataState;
import com.stox.nse.data.NseDataStateRepository;

@Component
public class NseLegthBarDownloadManager {

	@Autowired
	private Environment environment;

	@Autowired
	private InstrumentRepository instrumentRepository;

	@Autowired
	private NseDataStateRepository dataStateRepository;

	@Autowired
	private BarRepository barRepository;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private final LineRepository repository = new LineRepository(
			"nse" + File.separator + "com.stox.data.download.bar.legth.nse.csv");

	private boolean cancel;

	@PostConstruct
	public void postConstruct() {
		if (shouldDownload()) {
			taskExecutor.execute(() -> download());
		}
	}

	@EventListener
	public void onInstrumentsChanged(final InstrumentsChangedEvent event) {
		if (shouldDownload()) {
			taskExecutor.execute(() -> download());
		}
	}

	@PreDestroy
	public void preDestroy() {
		cancel = true;
	}

	private boolean shouldDownload() {
		final NseDataState state = dataStateRepository.getDataState();
		final Date lastInstrumentDownloadDate = state.getLastInstrumentDownloadDate();
		final Boolean barLengthDownloadCompleted = state.getBarLengthDownloadCompleted();
		return null != lastInstrumentDownloadDate
				&& (null == barLengthDownloadCompleted || !barLengthDownloadCompleted);
	}

	public void download() {
		final String url = environment.getProperty("com.stox.nse.url.bar.length");
		final List<String> downloadedInstrumentIds = repository.findAll();
		final List<Instrument> allInstruments = instrumentRepository.getInstruments(Exchange.NSE);
		allInstruments.removeIf(instrument -> {
			return downloadedInstrumentIds.contains(instrument.getId())
					|| null != barRepository.getLastTradingDate(instrument.getId(), BarSpan.D);
		});
		final AtomicInteger count = new AtomicInteger(downloadedInstrumentIds.size());
		final BarLengthDownloadNotification notification = new BarLengthDownloadNotification();
		notification.setExchange(Exchange.NSE);
		notification.setBarSpan(BarSpan.D);
		notification.show();

		final NseDataState state = dataStateRepository.getDataState();
		if (null == state.getBarLengthDownloadStartDate()) {
			state.setBarLengthDownloadStartDate(new Date());
			dataStateRepository.persistDataState();
		}

		allInstruments.stream().parallel().forEach(instrument -> {
			if (!cancel && StringUtil.hasText(instrument.getSymbol())) {
				try {
					final NseBarLengthDownloader downloader = new NseBarLengthDownloader(url, instrument);
					final List<Bar> bars = downloader.download();
					barRepository.save(bars, instrument.getId(), BarSpan.D);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					repository.append(instrument.getId());
					notification.setInstrument(instrument);
					notification.setProgress(((double) count.incrementAndGet()) / ((double) allInstruments.size()));
				}
			}
		});

		if (!cancel) {
			state.setBarLengthDownloadCompleted(Boolean.TRUE);
			dataStateRepository.persistDataState();
			repository.drop();
			notification.hide();
			eventPublisher.publishEvent(new BarLengthDownloadCompletedEvent(Exchange.NSE));
		}
	}
}

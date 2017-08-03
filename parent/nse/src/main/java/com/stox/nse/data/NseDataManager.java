package com.stox.nse.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.downloader.Downloader;
import com.stox.core.event.MessageEvent;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.StringUtil;
import com.stox.nse.data.instrument.IndexComponentDownloader;
import com.stox.nse.data.instrument.InstrumentDownloaderFactory;

@Component
@PropertySource("classpath:nse.properties")
public class NseDataManager {

	@Autowired
	private InstrumentRepository instrumentRepository;

	@Autowired
	private BarRepository barRepository;

	@Autowired
	private NseDataStateRepository dataStateRepository;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		if (shouldDownloadInstruments()) {
			downloadInstruments();
		}

		downloadBars();
	}

	private void downloadBars() {
		eventPublisher.publishEvent(new MessageEvent(this, "some message"));
	}

	private boolean shouldDownloadInstruments() {
		return null == dataStateRepository.getDataState().getLastInstrumentDownloadDate();
	}

	private void downloadInstruments() {
		try {
			final InstrumentDownloaderFactory downloaderFactory = new InstrumentDownloaderFactory(environment);
			final List<Downloader<Instrument, ?>> downlaoders = Arrays.stream(InstrumentType.values()).map(type -> downloaderFactory.getDownloader(type))
					.filter(downloader -> null != downloader).collect(Collectors.toList());
			taskExecutor.submit(() -> {
				final List<Instrument> allInstruments = downlaoders.stream().parallel().map(downloader -> {
					try {
						return downloader.download();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}).flatMap(x -> x.stream()).collect(Collectors.toList());
				instrumentRepository.save(Exchange.NSE, allInstruments);
				downloadParentComponentMapping();
				dataStateRepository.getDataState().setLastInstrumentDownloadDate(new Date());
				dataStateRepository.persistDataState();
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadParentComponentMapping() {
		final List<Instrument> instruments = instrumentRepository.getInstruments(Exchange.NSE, InstrumentType.INDEX);
		final Map<String, List<String>> map = instruments.stream().parallel().collect(Collectors.<Instrument, String, List<String>> toMap(Instrument::getId, instrument -> {
			final String url = environment.getProperty("com.stox.nse.url.index.component." + instrument.getId());
			if (StringUtil.hasText(url)) {
				final IndexComponentDownloader downloader = new IndexComponentDownloader(url);
				try {
					return downloader.download();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return Collections.emptyList();
		}));
		instrumentRepository.save(Exchange.NSE, map);
	}
}

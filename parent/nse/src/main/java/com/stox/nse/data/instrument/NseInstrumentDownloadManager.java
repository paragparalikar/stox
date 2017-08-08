package com.stox.nse.data.instrument;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.downloader.Downloader;
import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.StringUtil;
import com.stox.nse.data.NseDataStateRepository;

@Component
public class NseInstrumentDownloadManager {

	@Autowired
	private Environment environment;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private InstrumentRepository instrumentRepository;

	@Autowired
	private NseDataStateRepository dataStateRepository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@PostConstruct
	public void postConstruct() {
		if (shouldDownload()) {
			taskExecutor.execute(() -> download());
		}
	}

	private boolean shouldDownload() {
		// TODO add monthly logic
		return null == dataStateRepository.getDataState().getLastInstrumentDownloadDate();
	}

	public void download() {
		if (shouldDownload()) {
			try {
				final InstrumentDownloaderFactory downloaderFactory = new InstrumentDownloaderFactory(environment);
				final List<Downloader<Instrument, ?>> downlaoders = Arrays.stream(InstrumentType.values()).map(type -> downloaderFactory.getDownloader(type))
						.filter(downloader -> null != downloader).collect(Collectors.toList());
				final List<Instrument> allInstruments = downlaoders.stream().parallel().map(downloader -> {
					try {
						return downloader.download();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}).flatMap(x -> x.stream()).collect(Collectors.toList());
				if (!allInstruments.isEmpty()) {
					instrumentRepository.save(Exchange.NSE, allInstruments);
					downloadMappings();
					dataStateRepository.getDataState().setLastInstrumentDownloadDate(new Date());
					dataStateRepository.persistDataState();
					eventPublisher.publishEvent(new InstrumentsChangedEvent(Exchange.NSE, allInstruments));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void downloadMappings() {
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

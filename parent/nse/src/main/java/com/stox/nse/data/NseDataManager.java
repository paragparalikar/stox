package com.stox.nse.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.downloader.Downloader;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.DateUtil;
import com.stox.core.util.StringUtil;
import com.stox.data.ui.BarDownloadNotification;
import com.stox.nse.data.bar.NseBarDownloader;
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

	private boolean cancel;

	private final DateFormat bhavcopyDateFormat;

	public NseDataManager() {
		final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		dateFormatSymbols.setShortMonths(new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" });
		bhavcopyDateFormat = new SimpleDateFormat("yyyy/MMM/'cm'ddMMMyyyy", dateFormatSymbols);
	}

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		if (shouldDownloadInstruments()) {
			downloadInstruments();
		}
		downloadBars();
	}

	@PreDestroy
	public void preDestroy() {
		cancel = true;
	}

	private void downloadBars() {
		taskExecutor.submit(() -> {
			final String url = environment.getProperty("com.stox.nse.url.bar");
			final Date start = DateUtil.trim(getLastBarDownloadDate());
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			final Calendar today = Calendar.getInstance();
			today.setTime(DateUtil.trim(today.getTime()));
			if (!cancel && calendar.before(today)) {
				final BarDownloadNotification notification = new BarDownloadNotification(start, today.getTime());
				notification.show();
				for (; !cancel && calendar.before(today); calendar.add(Calendar.DATE, 1)) {
					try {
						if (DateUtil.isWeekend(calendar)) {
							continue;
						}
						final Date date = calendar.getTime();
						final String effectiveUrl = url.replace("{date}", bhavcopyDateFormat.format(date));
						final NseBarDownloader downloader = new NseBarDownloader(effectiveUrl);
						final List<Bar> bars = downloader.download();
						if (null != bars && !bars.isEmpty()) {
							bars.forEach(bar -> barRepository.save(bar, bar.getInstrumentId(), BarSpan.D));
							dataStateRepository.getDataState().setLastBarDownloadDate(date);
							dataStateRepository.persistDataState();
							notification.setDate(date);
						}
					} catch (IOException e) {

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		// Notifications.create().darkStyle().position(Pos.TOP_RIGHT).title("the title text").graphic(UiUtil.classes(new Label("Graphic label"), "primary")).show();
	}

	private Date getLastBarDownloadDate() {
		final Date date = dataStateRepository.getDataState().getLastBarDownloadDate();
		if (null == date) {
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -1);
			return calendar.getTime();
		}
		return date;
	}

	private boolean shouldDownloadInstruments() {
		// TODO add monthly logic
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

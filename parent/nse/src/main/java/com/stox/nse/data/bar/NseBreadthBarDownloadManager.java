package com.stox.nse.data.bar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.DateUtil;
import com.stox.data.ui.BarDownloadNotification;
import com.stox.nse.data.NseDataState;
import com.stox.nse.data.NseDataStateRepository;

@Component
@PropertySource("classpath:nse.properties")
public class NseBreadthBarDownloadManager {

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

	private boolean cancel;

	private final DateFormat bhavcopyDateFormat;

	public NseBreadthBarDownloadManager() {
		final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		dateFormatSymbols.setShortMonths(new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" });
		bhavcopyDateFormat = new SimpleDateFormat("yyyy/MMM/'cm'ddMMMyyyy", dateFormatSymbols);
	}

	@PreDestroy
	public void preDestroy() {
		cancel = true;
	}

	@PostConstruct
	public void postConstruct() {
		if (shouldDownload()) {
			taskExecutor.execute(() -> download());
		}
	}

	public void download() {
		final DateFormat indexUrlDateFormat = new SimpleDateFormat("ddMMyyyy");
		final String url = environment.getProperty("com.stox.nse.url.bar");
		final String url_index = environment.getProperty("com.stox.nse.url.bar.index");
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
					final NseBarIndexDownloader indexDownloader = new NseBarIndexDownloader(url_index.replace("", indexUrlDateFormat.format(date)), instrumentRepository);
					bars.addAll(indexDownloader.download());
					if (!bars.isEmpty()) {
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
	}

	private boolean shouldDownload() {
		final NseDataState state = dataStateRepository.getDataState();
		final Date lastInstrumentDownloadDate = state.getLastInstrumentDownloadDate();
		final Boolean barLengthDownloadCompleted = state.getBarLengthDownloadCompleted();
		final Date start = DateUtil.trim(getLastBarDownloadDate());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		final Calendar today = Calendar.getInstance();
		return null != lastInstrumentDownloadDate && null != barLengthDownloadCompleted && barLengthDownloadCompleted && calendar.before(today);
	}

	private Date getLastBarDownloadDate() {
		final Date date = dataStateRepository.getDataState().getLastBarDownloadDate();
		if (null == date) {
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -8);
			return calendar.getTime();
		}
		return date;
	}

}

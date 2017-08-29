package com.stox.data;

import java.util.Date;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.repository.BarRepository;
import com.stox.core.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class DataProviderAdvice {

	@Autowired
	private BarRepository barRepository;

	@Autowired
	private TaskExecutor taskExecutor;

	@SuppressWarnings("unchecked")
	@Around("target(com.stox.data.DataProvider) && execution(* getBars(..))")
	public Object getBars(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		final Object[] args = proceedingJoinPoint.getArgs();
		final Instrument instrument = (Instrument) args[0];
		final BarSpan barSpan = (BarSpan) args[1];
		final Date from = (Date) args[2];
		final Date to = (Date) args[3];
		final List<Bar> bars = barRepository.find(instrument.getId(), barSpan, from, to);

		if (bars.isEmpty()) {
			log.debug("No data available locally for " + instrument.getId() + ", fetching all from data provider");
			bars.addAll((List<Bar>) proceedingJoinPoint.proceed());
			taskExecutor.execute(() -> {
				barRepository.save(bars, instrument.getId(), barSpan);
			});
			return DateUtil.trim(bars, from, to);
		} else {
			final Date start = bars.get(bars.size() - 1).getDate();
			final Date end = bars.get(0).getDate();
			final long next = barSpan.next(end.getTime());
			if (start.getTime() <= from.getTime() &&  next > to.getTime()) {
				log.debug("All data available locally for " + instrument.getId());
				return bars;
			} else if (start.getTime() > from.getTime() && next <= to.getTime()) {
				log.debug("Only inclusive subset of data available locally, start : " + start + ", end : " + end);
				bars.clear();
				bars.addAll((List<Bar>) proceedingJoinPoint.proceed());
				taskExecutor.execute(() -> {
					barRepository.save(bars, instrument.getId(), barSpan);
				});
				return DateUtil.trim(bars, from, to);
			} else if (start.getTime() <= from.getTime() && next <= to.getTime()) {
				log.debug(
						"Fetch latest data, start : " + start + ", from : " + from + ", end : " + end + ", to : " + to);
				final List<Bar> downloadedBars = (List<Bar>) proceedingJoinPoint
						.proceed(new Object[] { instrument, barSpan, end, to });
				taskExecutor.execute(() -> {
					barRepository.save(downloadedBars, instrument.getId(), barSpan);
				});
				bars.addAll(0, DateUtil.trim(downloadedBars, end, to)); // There might one bar in bars with
																		// date = end and one in downloaded
																		// bars with date = end; this will
																		// cause duplicate bars
				return bars;
			} else if (start.getTime() > from.getTime() && next >= to.getTime()) {
				log.debug("Fetch historic data, from : "+from+", start : "+start+", to : "+to+", end : "+end);
				final List<Bar> downloadedBars = (List<Bar>) proceedingJoinPoint
						.proceed(new Object[] { instrument, barSpan, from, start });
				taskExecutor.execute(() -> {
					barRepository.save(downloadedBars, instrument.getId(), barSpan);
				});
				bars.addAll(DateUtil.trim(downloadedBars, from, start));
				return bars;
			}
		}
		throw new IllegalStateException("We should not reach this point");
	}

}

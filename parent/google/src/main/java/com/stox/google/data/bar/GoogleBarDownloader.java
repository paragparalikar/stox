package com.stox.google.data.bar;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;

public class GoogleBarDownloader extends CsvDownloader<Bar> {

	private long offset = 0;
	private double previousClose = 0;

	private final Date start;
	private final BarSpan barSpan;
	private final Instrument instrument;

	public GoogleBarDownloader(String url, final Date start, final BarSpan barSpan, final Instrument instrument) {
		super(url, 7);
		this.start = start;
		this.barSpan = barSpan;
		this.instrument = instrument;
	}

	private int getSeconds(final BarSpan barSpan) {
		return (int) (barSpan.getMillis() / 1000);
	}

	private String getPeriod(final Date start) {
		final long delta = System.currentTimeMillis() - start.getTime();
		if (delta >= TimeUnit.DAYS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toDays(delta) + "d";
		}
		if (delta >= TimeUnit.HOURS.toMillis(1)) {
			return TimeUnit.MILLISECONDS.toHours(delta) + "h";
		}
		return TimeUnit.MILLISECONDS.toMinutes(delta) + "m";
	}

	@Override
	public String getUrl() {
		String url = super.getUrl();
		url = url.replace("{symbol}", instrument.getSymbol());
		url = url.replace("{exchange}", getGoogleExchangeCode(instrument.getExchange()));
		url = url.replace("{intervalInSeconds}", String.valueOf(getSeconds(barSpan)));
		url = url.replace("{period}", getPeriod(start));
		return url;
	}

	@Override
	public Bar parse(String[] values) throws Exception {
		final Bar bar = new Bar();
		if (values[0].startsWith("a")) {
			offset = 1000l * Long.parseLong(values[0].substring(1));
			bar.setDate(new Date(offset));
		} else {
			bar.setDate(new Date(offset + (Long.parseLong(values[0]) * getSeconds(barSpan) * 1000l)));
		}
		bar.setClose(Double.parseDouble(values[1]));
		bar.setPreviousClose(previousClose);
		previousClose = bar.getClose();
		bar.setHigh(Double.parseDouble(values[2]));
		bar.setLow(Double.parseDouble(values[3]));
		bar.setOpen(Double.parseDouble(values[4]));
		bar.setVolume(Double.parseDouble(values[5]));
		bar.setInstrumentId(instrument.getId());
		return bar;
	}

	private String getGoogleExchangeCode(final Exchange exchange) {
		switch (exchange) {
		case BSE:
			return "BOM";
		case NSE:
			return "NSE";
		default:
			return null;
		}
	}

}

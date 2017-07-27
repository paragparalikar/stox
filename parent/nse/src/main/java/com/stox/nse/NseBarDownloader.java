package com.stox.nse;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.util.HttpUtil;
import com.stox.core.util.StringUtil;

@Component
public class NseBarDownloader {

	private final DateFormat historicalDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private final DateFormat intradayDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	private Bar parse(final String text, final DateFormat dateFormat) throws ParseException {
		final Bar bar = new Bar();
		final String[] tokens = text.split("\\|");
		bar.setDate(dateFormat.parse(tokens[0]));
		bar.setOpen(Double.parseDouble(tokens[1]));
		bar.setHigh(Double.parseDouble(tokens[2]));
		bar.setLow(Double.parseDouble(tokens[3]));
		bar.setClose(Double.parseDouble(tokens[4]));
		bar.setVolume(Double.parseDouble(tokens[5]));
		return bar;
	}

	/*
	 * PeriodType: 1-Historical ie daily, 2-Intraday :::: Periodicity 1 1Min 2 5Min 3 15Min 4 30Min 5 1HR
	 */
	@Async
	public void download(final Instrument instrument, final BarSpan barSpan, final Callback<List<Bar>, Void> callback) throws Exception {
		final HttpURLConnection connection = HttpUtil.getConnection("https://www.nseindia.com/ChartApp/install/charts/data/GetHistoricalNew.jsp", "POST",
				"application/x-www-form-urlencoded", "www.nseindia.com", "*/*", "https://www.nseindia.com/ChartApp/install/charts/mainpage.jsp", "gzip, deflate, br",
				"en-US,en;q=0.8", "https://www.nseindia.com");
		connection.setConnectTimeout(30000);
		final String periodicity = getPeriodicity(barSpan);
		final String periodType = getPeriodType(barSpan);
		final DateFormat dateFormat = getDateFormat(periodType);
		connection.getOutputStream().write(
				HttpUtil.body("Instrument", "FUTSTK", "CDSymbol", instrument.getExchangeCode(), "Segment", "CM", "Series", "EQ", "PeriodType", periodType, "Periodicity",
						periodicity, "ct0", "g1|1|1", "ct1", "g2|2|1", "ctcount", "2"));
		final String[] lines = StringUtil.toString(new GZIPInputStream(connection.getInputStream())).split("~");
		final List<Bar> bars = new LinkedList<Bar>();
		for (int index = 1; index < lines.length; index++) {
			bars.add(parse(lines[index], dateFormat));
		}
		callback.call(bars);
	}

	private DateFormat getDateFormat(final String periodType) {
		switch (periodType) {
		case "1":
			return historicalDateFormat;
		case "2":
			return intradayDateFormat;
		}
		return null;
	}

	private String getPeriodType(final BarSpan barSpan) {
		switch (barSpan) {
		case M:
		case W:
		case D:
			return "1";
		default:
			return "2";
		}
	}

	private String getPeriodicity(final BarSpan barSpan) {
		switch (barSpan) {
		case D:
			return "1";
		case W:
			return "2";
		case M:
			return "3";
		case M1:
			return "1";
		case M10:
		case M5:
			return "2";
		case M15:
			return "3";
		case M30:
			return "4";
		case H:
			return "5";
		}
		return null;
	}
}

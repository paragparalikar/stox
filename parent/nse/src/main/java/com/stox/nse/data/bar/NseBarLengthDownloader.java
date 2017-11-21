package com.stox.nse.data.bar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPInputStream;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Bar;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.HttpUtil;

/**
 * PeriodType: Historical - 1 Intraday - 2
 * 
 * Periodicity: 1 - 1 min 2 - 5 min 3 - 15 min 4 - 30 min 5 - 1 hr
 *
 */
public class NseBarLengthDownloader extends CsvDownloader<Bar> {
	public static final String PERIODICITY_MIN1 = "1";
	public static final String PERIODICITY_DAY = "1";
	public static final String PERIOD_TYPE_HISTORICAL = "1";
	public static final String PERIOD_TYPE_INTRADAY = "2";
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static final DateFormat DATETIMEFORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm");

	private final Instrument instrument;
	private double previousClose = 0;
	private final String periodicity;
	private final String periodType;

	public NseBarLengthDownloader(final String url, final Instrument instrument, final String periodicity,
			final String periodType) {
		super(url, 1);
		this.periodicity = periodicity;
		this.periodType = periodType;
		this.instrument = instrument;
		setLineDelimiter("~");
		setTokenDelimiter("\\|");
	}

	@Override
	public Bar parse(String[] tokens) throws Exception {
		final Bar bar = new Bar();
		bar.setDate(PERIOD_TYPE_HISTORICAL.equals(periodType) ? DATEFORMAT.parse(tokens[0]) : DATETIMEFORMAT.parse(tokens[0]));
		bar.setOpen(Double.parseDouble(tokens[1]));
		bar.setHigh(Double.parseDouble(tokens[2]));
		bar.setLow(Double.parseDouble(tokens[3]));
		bar.setClose(Double.parseDouble(tokens[4]));
		bar.setVolume(Double.parseDouble(tokens[5]));
		bar.setPreviousClose(previousClose);
		previousClose = bar.getClose();
		bar.setInstrumentId(instrument.getId());
		return bar;
	}

	@Override
	protected InputStream inputStream() throws Exception {
		final HttpURLConnection connection = HttpUtil.getConnection(getUrl(), "POST",
				"application/x-www-form-urlencoded", "www.nseindia.com", "*/*",
				"https://www.nseindia.com/ChartApp/install/charts/mainpageall1.jsp?Segment=CD", "gzip, deflate, br",
				"en-US,en;q=0.8", null);
		connection.setConnectTimeout(30000);
		connection.getOutputStream()
				.write(HttpUtil.body("Instrument", "FUTSTK", "CDSymbol", instrument.getSymbol().toUpperCase(),
						"Segment", InstrumentType.INDEX.equals(instrument.getType()) ? "OI" : "CM", "Series", "EQ",
						"PeriodType", periodType, "Periodicity", periodicity, "ct0", "g1|1|1", "ct1", "g2|2|1",
						"ctcount", "2"));
		final GZIPInputStream inStream = new GZIPInputStream(connection.getInputStream());
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
		final byte[] buffer = new byte[100 * 1024];
		int nrBytesRead = 0;
		while ((nrBytesRead = inStream.read(buffer)) > 0) {
			baos.write(buffer, 0, nrBytesRead);
		}
		inStream.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}

}

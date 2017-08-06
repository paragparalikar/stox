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

public class NseBarLengthDownloader extends CsvDownloader<Bar> {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("dd-MM-yyyy");

	private final Instrument instrument;
	private double previousClose = 0;

	public NseBarLengthDownloader(final String url, final Instrument instrument) {
		super(url, 1);
		this.instrument = instrument;
		setLineDelimiter("~");
		setTokenDelimiter("\\|");
	}

	@Override
	public Bar parse(String[] tokens) throws Exception {
		final Bar bar = new Bar();
		bar.setDate(DATEFORMAT.parse(tokens[0]));
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
		final HttpURLConnection connection = HttpUtil.getConnection(getUrl(), "POST", "application/x-www-form-urlencoded", "www.nseindia.com", "*/*",
				"https://www.nseindia.com/ChartApp/install/charts/mainpageall1.jsp?Segment=CD", "gzip, deflate, br", "en-US,en;q=0.8", null);
		connection.setConnectTimeout(30000);
		connection.getOutputStream().write(
				HttpUtil.body("Instrument", "FUTSTK", "CDSymbol", instrument.getExchangeCode().toUpperCase(), "Segment", InstrumentType.INDEX.equals(instrument.getType()) ? "OI"
						: "CM", "Series", "EQ", "PeriodType", "1", "Periodicity", "1", "ct0", "g1|1|1", "ct1", "g2|2|1", "ctcount", "2"));
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

package com.stox.nse.batch.instrument;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.stox.core.batch.CsvItemReader;
import com.stox.core.batch.CsvRowMapper;
import com.stox.core.model.Bar;
import com.stox.core.model.Instrument;
import com.stox.core.util.HttpUtil;
import com.stox.core.util.StringUtil;

public class BarLengthDownloadItemReader extends CsvItemReader<Bar> {

	private final Instrument instrument;

	public BarLengthDownloadItemReader(final String url, final Instrument instrument, final CsvRowMapper<Bar> rowMapper) {
		super(url, rowMapper);
		this.instrument = instrument;
	}

	@Override
	protected List<String> createRows(InputStream inputStream) throws Exception {
		super.setDelimiter("\\|");
		return Arrays.asList(StringUtil.toString(inputStream).split("~"));
	}

	@Override
	public void setDelimiter(String delimiter) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected InputStream getInputStream() throws Exception {
		final HttpURLConnection connection = HttpUtil.getConnection(getUrl(), "POST", "application/x-www-form-urlencoded", "www.nseindia.com", "*/*",
				"https://www.nseindia.com/ChartApp/install/charts/mainpage.jsp", "gzip, deflate, br", "en-US,en;q=0.8", "https://www.nseindia.com");
		connection.setConnectTimeout(30000);
		connection.getOutputStream().write(
				HttpUtil.body("Instrument", "FUTSTK", "CDSymbol", instrument.getSymbol(), "Segment", "CM", "Series", "EQ", "PeriodType", "1", "Periodicity", "1", "ct0", "g1|1|1",
						"ct1", "g2|2|1", "ctcount", "2"));
		return new GZIPInputStream(connection.getInputStream());
	}

}

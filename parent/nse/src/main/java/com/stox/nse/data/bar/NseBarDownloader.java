package com.stox.nse.data.bar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.ZipInputStream;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Bar;
import com.stox.core.util.HttpUtil;

public class NseBarDownloader extends CsvDownloader<Bar> {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("dd-MMM-yy");

	public NseBarDownloader(String url) {
		super(url, 1);
	}

	@Override
	public Bar parse(String[] tokens) throws Exception {
		final Bar bar = new Bar();
		bar.setOpen(Double.parseDouble(tokens[2]));
		bar.setHigh(Double.parseDouble(tokens[3]));
		bar.setLow(Double.parseDouble(tokens[4]));
		bar.setClose(Double.parseDouble(tokens[5]));
		bar.setPreviousClose(Double.parseDouble(tokens[7]));
		bar.setVolume(Double.parseDouble(tokens[8]));
		bar.setDate(DATEFORMAT.parse(tokens[10]));
		bar.setInstrumentId(tokens[12]);
		return bar;
	}

	@Override
	protected InputStream createInputStream() throws Exception {
		final HttpURLConnection connection = HttpUtil.getConnection(getUrl(), "GET", null, "www.nseindia.com",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8", "https://www.nseindia.com/products/content/equities/equities/archieve_eq.htm",
				"gzip, deflate, sdch, br", "en-US,en;q=0.8", null);
		final ZipInputStream inStream = new ZipInputStream(connection.getInputStream());
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
		final byte[] buffer = new byte[100 * 1024];
		if (inStream.getNextEntry() != null) {
			int nrBytesRead = 0;
			while ((nrBytesRead = inStream.read(buffer)) > 0) {
				baos.write(buffer, 0, nrBytesRead);
			}
		}
		inStream.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}

}

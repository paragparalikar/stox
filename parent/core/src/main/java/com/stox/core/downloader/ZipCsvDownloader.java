package com.stox.core.downloader;

import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

public abstract class ZipCsvDownloader<T> extends CsvDownloader<T> {

	public ZipCsvDownloader(String url) {
		super(url);
	}

	public ZipCsvDownloader(String url, int linesToSkip, String lineDelimiter, String tokenDelimiter) {
		super(url, linesToSkip, lineDelimiter, tokenDelimiter);
	}

	public ZipCsvDownloader(String url, int linesToSkip) {
		super(url, linesToSkip);
	}

	@Override
	protected InputStream createInputStream() throws Exception {
		return new ZipInputStream(new URL(getUrl()).openStream());
	}

}

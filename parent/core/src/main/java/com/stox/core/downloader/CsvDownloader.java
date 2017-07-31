package com.stox.core.downloader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.stox.core.util.Constant;

@Data
@AllArgsConstructor
public abstract class CsvDownloader<T> implements Downloader<T, String[]> {

	private String url;
	private int linesToSkip;
	private String lineDelimiter;
	private String tokenDelimiter;

	public CsvDownloader(final String url) {
		this(url, 0);
	}

	public CsvDownloader(final String url, final int linesToSkip) {
		this(url, linesToSkip, Constant.LINEFEED, ",");
	}

	@Override
	public List<T> download() throws Exception {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(createInputStream()));
		return bufferedReader.lines().skip(linesToSkip).map(line -> {
			try {
				return parse(line.split(tokenDelimiter));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}

	protected InputStream createInputStream() throws Exception {
		return new URL(url).openStream();
	}

}

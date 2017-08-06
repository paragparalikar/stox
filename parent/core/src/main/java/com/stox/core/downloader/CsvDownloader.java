package com.stox.core.downloader;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stox.core.util.StringUtil;

@Data
@AllArgsConstructor
public abstract class CsvDownloader<T> implements Downloader<T, String[]> {
	private static final Logger log = LoggerFactory.getLogger("com.stox.core.downloader.CsvDownloader");

	private String url;
	private int linesToSkip;
	private String lineDelimiter;
	private String tokenDelimiter;

	public CsvDownloader(final String url) {
		this(url, 0);
	}

	public CsvDownloader(final String url, final int linesToSkip) {
		this(url, linesToSkip, "\n", ",");
	}

	@Override
	public List<T> download() throws Exception {
		final InputStream inputStream = inputStream();
		return lines(inputStream).skip(linesToSkip).map(line -> {
			try {
				final String[] tokens = tokens(line);
				return parse(tokens);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}

	protected String[] tokens(final String row) {
		return row.split(tokenDelimiter);
	}

	protected Stream<String> lines(final InputStream inputStream) throws Exception {
		log.info("Downloading from " + url + "(linesToSkip=" + linesToSkip + ")");
		final String content = StringUtil.toString(inputStream);
		return Arrays.stream(content.split(lineDelimiter));
	}

	protected InputStream inputStream() throws Exception {
		return new URL(url).openStream();
	}

}

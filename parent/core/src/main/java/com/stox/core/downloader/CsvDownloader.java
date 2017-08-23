package com.stox.core.downloader;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.stox.core.util.StringUtil;

@Data
@Slf4j
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
		this(url, linesToSkip, "\n", ",");
	}

	@Override
	public List<T> download() throws Exception {
		final InputStream inputStream = inputStream();
		return lines(inputStream).skip(linesToSkip).map(line -> {
			try {
				try {
					final String[] tokens = tokens(line);
					return parse(tokens);
				} catch (Exception exception) {
					return null;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).filter(t -> null != t).collect(Collectors.toList());
	}

	protected String[] tokens(final String row) {
		return row.split(tokenDelimiter);
	}

	protected Stream<String> lines(final InputStream inputStream) throws Exception {
		final String content = StringUtil.toString(inputStream);
		return Arrays.stream(content.split(lineDelimiter));
	}

	protected InputStream inputStream() throws Exception {
		final String url = getUrl();
		log.debug("Downloading from " + url + "(linesToSkip=" + linesToSkip + ")");
		return new URL(getUrl()).openStream();
	}

}

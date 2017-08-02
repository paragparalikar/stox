package com.stox.nse.data.instrument;

import com.stox.core.downloader.CsvDownloader;

public class IndexComponentDownloader extends CsvDownloader<String> {

	public IndexComponentDownloader(String url) {
		super(url, 1);
	}

	@Override
	public String parse(String[] tokens) throws Exception {
		return tokens[4];
	}

}

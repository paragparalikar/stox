package com.stox.core.batch;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class ArchivedCsvItemReader<T> extends CsvItemReader<T> {

	public ArchivedCsvItemReader(String url, CsvRowMapper<T> rowMapper) {
		super(url, rowMapper);
	}

	@Override
	protected InputStream getInputStream() throws Exception {
		final ZipInputStream zis = new ZipInputStream(super.getInputStream());
		return zis;
	}
}

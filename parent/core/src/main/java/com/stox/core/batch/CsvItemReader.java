package com.stox.core.batch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;

@Data
@EqualsAndHashCode(callSuper = true)
public class CsvItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ItemReader<T> {

	private final Log logger = LogFactory.getLog(getClass());

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private int index;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private final List<String> rows = new ArrayList<>();

	private String delimiter;
	private Resource resource;
	private int linesToSkip = 0;
	private CsvRowCallback skippedRowsCallback;

	private final String url;
	private final CsvRowMapper<T> rowMapper;

	public CsvItemReader(final String url, final CsvRowMapper<T> rowMapper) {
		this.url = url;
		this.rowMapper = rowMapper;
	}

	@Override
	protected T doRead() throws Exception {
		final String row = rows.get(index++);
		return rowMapper.map(null != row ? row.split(delimiter) : new String[] {});
	}

	@Override
	protected void doOpen() throws Exception {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
		String line = null;
		while (null != (line = bufferedReader.readLine())) {
			rows.add(line);
		}
		if (null != skippedRowsCallback) {
			for (; index < Math.min(linesToSkip, rows.size()); index++) {
				final String row = rows.get(index);
				skippedRowsCallback.row(null != row ? row.split(delimiter) : new String[] {});
			}
		}
		index = linesToSkip;
	}

	protected InputStream getInputStream() throws Exception {
		return new URL(url).openStream();
	}

	@Override
	protected void doClose() throws Exception {

	}

}

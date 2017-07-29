package com.stox.nse.batch.instrument.mapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.stox.core.batch.CsvRowMapper;
import com.stox.core.model.Bar;

public class LengthBarDownloadCsvRowMapper implements CsvRowMapper<Bar> {

	private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	@Override
	public Bar map(String[] tokens) {
		try {
			final Bar bar = new Bar();
			bar.setDate(dateFormat.parse(tokens[0]));
			bar.setOpen(Double.parseDouble(tokens[1]));
			bar.setHigh(Double.parseDouble(tokens[2]));
			bar.setLow(Double.parseDouble(tokens[3]));
			bar.setClose(Double.parseDouble(tokens[4]));
			bar.setVolume(Double.parseDouble(tokens[5]));
			return bar;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}

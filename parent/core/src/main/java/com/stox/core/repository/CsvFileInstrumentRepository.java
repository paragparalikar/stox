package com.stox.core.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;

@Component
public class CsvFileInstrumentRepository implements InstrumentRepository {

	private final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();

	public String getPath(final String dataProviderCode) {
		return Constant.PATH + "com.stox.instruments." + dataProviderCode + ".csv";
	}

	@Override
	public Date getLastUpdatedDate(final String dataProviderCode) {
		try {
			final Path path = Paths.get(getPath(dataProviderCode));
			if (Files.exists(path)) {
				return new Date(Files.getLastModifiedTime(path).toMillis());
			}
		} catch (IOException e) {
		}
		return new Date(0);
	}

	@Override
	public List<Instrument> getAllInstruments(final String dataProviderCode) {
		try {
			return Constant.csvMapper.reader(schema).readValues(new File(getPath(dataProviderCode))).readAll().stream().map(o -> (Instrument) o).collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(final String dataProviderCode, List<Instrument> instruments) {
		try {
			Constant.csvMapper.writer(schema).writeValues(new File(getPath(dataProviderCode))).writeAll(instruments).flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

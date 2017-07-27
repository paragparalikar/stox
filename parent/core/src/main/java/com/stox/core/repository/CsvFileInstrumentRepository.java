package com.stox.core.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;

@Component
public class CsvFileInstrumentRepository implements InstrumentRepository {

	private final Map<String, Instrument> cache = new HashMap<>();
	private final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();

	public String getPath() {
		return Constant.PATH + "com.stox.instruments.csv";
	}

	@Override
	public Date getLastUpdatedDate() {
		try {
			final Path path = Paths.get(getPath());
			if (Files.exists(path)) {
				return new Date(Files.getLastModifiedTime(path).toMillis());
			}
		} catch (IOException e) {
		}
		return new Date(0);
	}

	private void loadInstruments() {
		try {
			if (cache.isEmpty()) {
				final List<Instrument> instruments = Constant.csvMapper.reader(schema).forType(Instrument.class).readValues(new File(getPath())).readAll().stream()
						.map(o -> (Instrument) o).collect(Collectors.toList());
				instruments.forEach(instrument -> cache.put(instrument.getId(), instrument));
			}
		} catch (FileNotFoundException fnfe) {
			// Do nothing
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Instrument> getAllInstruments() {
		loadInstruments();
		return cache.values().stream().sorted(new HasNameComaparator<>()).collect(Collectors.toList());
	}

	@Override
	public void save(final List<Instrument> instruments) {
		try {
			Constant.csvMapper.writer(schema).writeValues(new File(getPath())).writeAll(instruments).flush();
			instruments.forEach(instrument -> cache.put(instrument.getId(), instrument));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Instrument getInstrument(String id) {
		loadInstruments();
		return cache.get(id);
	}

}

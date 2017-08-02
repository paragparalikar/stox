package com.stox.core.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.Constant;
import com.stox.core.util.FileUtil;

@Component
public class CsvFileInstrumentRepository implements InstrumentRepository {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private final Map<String, Instrument> cache = new HashMap<>();
	private final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();

	private String getPath(final Exchange exchange) {
		return Constant.PATH + "com.stox.instruments." + exchange.getId().toLowerCase() + ".csv";
	}

	private String getParentComponentMappingPath(final Exchange exchange) {
		return Constant.PATH + "com.stox.instruments." + exchange.getId().toLowerCase() + ".parent-component-mapping.json";
	}

	private void loadInstruments() {
		try {
			if (cache.isEmpty()) {
				for (final Exchange exchange : Exchange.values()) {
					final File file = new File(getPath(exchange));
					final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Instrument.class);
					final List<Instrument> instruments = reader.<Instrument> readValues(file).readAll();
					instruments.forEach(instrument -> cache.put(instrument.getId(), instrument));
				}
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
	public List<Instrument> getInstruments(Exchange exchange) {
		loadInstruments();
		return cache.values().stream().filter(instrument -> exchange.equals(instrument.getExchange())).sorted(new HasNameComaparator<>()).collect(Collectors.toList());
	}

	@Override
	public List<Instrument> getInstruments(Exchange exchange, InstrumentType type) {
		loadInstruments();
		return cache.values().stream().filter(instrument -> exchange.equals(instrument.getExchange()) && type.equals(instrument.getType())).sorted(new HasNameComaparator<>())
				.collect(Collectors.toList());
	}

	@Override
	public void save(Exchange exchange, Map<String, List<String>> parentComponentMapping) {
		try {
			final String path = getParentComponentMappingPath(exchange);
			Constant.objectMapper.writeValue(FileUtil.getFile(path), parentComponentMapping);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Instrument> getComponentInstruments(Instrument instrument) {
		try {
			final String path = getParentComponentMappingPath(instrument.getExchange());
			final Map<String, List<String>> map = Constant.objectMapper.readerFor(Map.class).readValue(FileUtil.getFile(path));
			final List<String> componentIds = map.get(instrument.getId());
			if (null != componentIds && !componentIds.isEmpty()) {
				return componentIds.stream().map(id -> getInstrument(id)).collect(Collectors.toList());
			}
			return Collections.emptyList();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(final Exchange exchange, final List<Instrument> instruments) {
		try {
			Constant.csvMapper.writer(schema).writeValues(new File(getPath(exchange))).writeAll(instruments).flush();
			instruments.forEach(instrument -> cache.put(instrument.getId(), instrument));
			final List<Instrument> allInstruments = cache.values().stream().sorted(new HasNameComaparator<>()).collect(Collectors.toList());
			eventPublisher.publishEvent(new InstrumentsChangedEvent(this, allInstruments));
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

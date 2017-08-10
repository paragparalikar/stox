package com.stox.core.repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
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
	private CacheManager cacheManager;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private TaskExecutor taskExecutor;

	private final Map<String, Instrument> cache = new HashMap<>();
	private final Map<String, String> exchangeCodeToIdCache = new HashMap<>();
	private final Map<String, List<Instrument>> componentCache = new HashMap<>();
	private final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();

	private String getPath(final Exchange exchange) {
		return Constant.PATH + "com.stox.instruments." + exchange.getId().toLowerCase() + ".csv";
	}

	private String getParentComponentMappingPath(final Exchange exchange) {
		return Constant.PATH + "com.stox.instruments.mapping." + exchange.getId().toLowerCase() + ".json";
	}

	@PostConstruct
	public synchronized void postConstruct() {
		taskExecutor.execute(() -> loadInstruments());
	}

	private synchronized void loadInstruments() {
		if (cache.isEmpty()) {
			for (final Exchange exchange : Exchange.values()) {
				try {
					final File file = new File(getPath(exchange));
					final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Instrument.class);
					final List<Instrument> instruments = reader.<Instrument> readValues(file).readAll();
					instruments.forEach(instrument -> {
						cache.put(instrument.getId(), instrument);
						exchangeCodeToIdCache.put(instrument.getExchangeCode(), instrument.getId());
					});
				} catch (Exception e) {
				}

				try {
					final Map<String, List<String>> map = Constant.objectMapper.readValue(FileUtil.getFile(getParentComponentMappingPath(exchange)),
							new TypeReference<HashMap<String, List<String>>>() {
							});
					for (final String indexId : map.keySet()) {
						componentCache.put(indexId, map.getOrDefault(indexId, Collections.emptyList()).stream().map(id -> cache.get(id)).collect(Collectors.toList()));
					}
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public synchronized String getIdByExchangeCode(String exchangeCode) {
		return exchangeCodeToIdCache.get(exchangeCode);
	}

	@Override
	public synchronized List<Instrument> getAllInstruments() {
		loadInstruments();
		return cache.values().stream().sorted(new HasNameComaparator<>()).collect(Collectors.toList());
	}

	@Override
	public synchronized List<Instrument> getInstruments(Exchange exchange) {
		loadInstruments();
		return cache.values().stream().filter(instrument -> exchange.equals(instrument.getExchange())).sorted(new HasNameComaparator<>()).collect(Collectors.toList());
	}

	@Override
	public synchronized List<Instrument> getInstruments(Exchange exchange, InstrumentType type) {
		loadInstruments();
		return cache.values().stream().filter(instrument -> exchange.equals(instrument.getExchange()) && type.equals(instrument.getType())).sorted(new HasNameComaparator<>())
				.collect(Collectors.toList());
	}

	@Override
	public synchronized void save(Exchange exchange, Map<String, List<String>> parentComponentMapping) {
		try {
			final String path = getParentComponentMappingPath(exchange);
			Constant.objectMapper.writeValue(FileUtil.getFile(path), parentComponentMapping);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized List<Instrument> getComponentInstruments(Instrument instrument) {
		loadInstruments();
		final List<Instrument> instruments = componentCache.get(instrument.getId());
		return null == instruments ? Collections.emptyList() : instruments;
	}

	@Override
	public synchronized void save(final Exchange exchange, final List<Instrument> instruments) {
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
	public synchronized Instrument getInstrument(String id) {
		loadInstruments();
		return cache.get(id);
	}

}

package com.stox.core.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.Constant;
import com.stox.core.util.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@SuppressWarnings("unchecked")
public class CsvFileInstrumentRepository implements InstrumentRepository {
	private static final String ALL = "all";
	private static final String CACHE = "instruments";

	private CacheManager cacheManager;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private TaskExecutor taskExecutor;

	private volatile Cache cache;

	private final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();

	private String getPath(final Exchange exchange) {
		return Constant.PATH + "instrument" + File.separator + "com.stox.instruments." + exchange.getId().toLowerCase()
				+ ".csv";
	}

	private String getParentComponentMappingPath(final Exchange exchange) {
		return Constant.PATH + "instrument" + File.separator + "com.stox.instruments.mapping."
				+ exchange.getId().toLowerCase() + ".json";
	}

	@Autowired
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@PostConstruct
	public void postConstruct() {
		taskExecutor.execute(() -> populateCache());
	}

	private synchronized void populateCache() {
		if (null == cache) {
			cache = cacheManager.getCache(CACHE);
			cache.put(ALL, new ArrayList<Instrument>(100000));
			for (final Exchange exchange : Exchange.values()) {
				try {
					final File file = new File(getPath(exchange));
					if(file.exists()) {
						final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Instrument.class);
						final List<Instrument> instruments = reader.<Instrument>readValues(file).readAll();
						updateExchangeCache(exchange, instruments);
					}
				} catch (Exception e) {
					log.error("Could not load instruments for "+exchange.getName(), e);
				}
				try {
					final File mappingFile = new File(getParentComponentMappingPath(exchange));
					if(mappingFile.exists()) {
						final Map<String, List<String>> parentComponentMapping = Constant.objectMapper.readValue(
								mappingFile,
								new TypeReference<HashMap<String, List<String>>>() {
								});
						updateParentComponentMapping(parentComponentMapping);
					}
				} catch (Exception e) {
					log.error("Could not load parent-component mapping for "+exchange.getName(), e);
				}
			}
		}
	}

	@Override
	@Cacheable(CACHE)
	public Instrument getInstrument(String id) {
		populateCache();
		final ValueWrapper wrapper = cache.get(id);
		return null == wrapper ? null : (Instrument) wrapper.get();
	}

	@Override
	@Cacheable(CACHE)
	public Instrument findBySymbol(String symbol) {
		populateCache();
		final ValueWrapper wrapper = cache.get(symbol);
		return null == wrapper ? null : (Instrument) wrapper.get();
	}

	@Override
	@Cacheable(value = CACHE, key = "'" + ALL + "'")
	public List<Instrument> getAllInstruments() {
		populateCache();
		final ValueWrapper wrapper = cache.get(ALL);
		return null == wrapper ? null : (List<Instrument>) wrapper.get();
	}

	@Override
	@Cacheable(CACHE)
	public List<Instrument> getInstruments(Exchange exchange) {
		populateCache();
		final ValueWrapper wrapper = cache.get(exchange);
		return null == wrapper ? null : (List<Instrument>) wrapper.get();
	}

	@Override
	@Cacheable(value = CACHE, key = "#a0.toString().concat(#a1.toString())")
	public List<Instrument> getInstruments(Exchange exchange, InstrumentType type) {
		populateCache();
		final ValueWrapper wrapper = cache.get(exchange.toString() + type.toString());
		return null == wrapper ? null : (List<Instrument>) wrapper.get();
	}

	@Override
	public Map<String, List<String>> saveParentComponentMapping(Exchange exchange,
			Map<String, List<String>> parentComponentMapping) {
		try {
			final String path = getParentComponentMappingPath(exchange);
			Constant.objectMapper.writeValue(FileUtil.getFile(path), parentComponentMapping);
			updateParentComponentMapping(parentComponentMapping);
			return parentComponentMapping;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void updateParentComponentMapping(final Map<String, List<String>> parentComponentMapping) {
		parentComponentMapping.keySet().forEach(key -> {
			final Instrument parent = getInstrument(key);
			if(null != parent) {
				Optional.ofNullable(parentComponentMapping.get(key)).ifPresent(ids -> {
					cache.put(parent, ids.stream().map(id -> getInstrument(id)).collect(Collectors.toList()));
				});
			}
		});
	}

	private void updateExchangeCache(final Exchange exchange, final List<Instrument> instruments) {
		final List<Instrument> allInstruments = getAllInstruments();
		allInstruments.addAll(instruments);
		cache.put(exchange, instruments);
		instruments.forEach(instrument -> {
			if(null != instrument.getId() && null != instrument.getSymbol()) {
				cache.put(instrument.getId(), instrument);
				cache.put(instrument.getSymbol(), instrument);
			}else {
				log.debug("Invalid instrument : "+instrument);
			}
		});
		instruments.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(Instrument::getType, Collectors.toList()))
				.forEach((t, i) -> {
					cache.put(exchange.toString() + t.toString(), i);
				});
	}

	@Override
	@Cacheable(value = CACHE)
	public List<Instrument> getComponentInstruments(Instrument instrument) {
		populateCache();
		final ValueWrapper wrapper = cache.get(instrument);
		return null == wrapper ? null : (List<Instrument>) wrapper.get();
	}

	@Override
	public List<Instrument> save(final Exchange exchange, final List<Instrument> instruments) {
		try {
			Constant.csvMapper.writer(schema).writeValues(FileUtil.getFile(getPath(exchange))).writeAll(instruments).flush();
			updateExchangeCache(exchange, instruments);
			eventPublisher.publishEvent(new InstrumentsChangedEvent(this, getAllInstruments()));
			return instruments;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}

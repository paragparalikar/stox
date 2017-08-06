package com.stox.core.repository;

import java.util.List;
import java.util.Map;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public interface InstrumentRepository {

	List<Instrument> getAllInstruments();

	List<Instrument> getInstruments(final Exchange exchange);

	List<Instrument> getInstruments(final Exchange exchange, InstrumentType type);

	Instrument getInstrument(final String id);

	String getIdByExchangeCode(final String exchangeCode);

	void save(final Exchange exchange, final List<Instrument> instruments);

	void save(final Exchange exchange, final Map<String, List<String>> parentComponentMapping);

	List<Instrument> getComponentInstruments(final Instrument instrument);

}

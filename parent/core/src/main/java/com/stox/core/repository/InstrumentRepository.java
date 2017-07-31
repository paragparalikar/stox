package com.stox.core.repository;

import java.util.List;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;

public interface InstrumentRepository {

	List<Instrument> getAllInstruments();

	Instrument getInstrument(final String id);

	void save(final Exchange exchange, final List<Instrument> instruments);

}

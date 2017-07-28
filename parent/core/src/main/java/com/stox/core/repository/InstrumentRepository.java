package com.stox.core.repository;

import java.util.Date;
import java.util.List;

import com.stox.core.model.Instrument;

public interface InstrumentRepository {

	Date getLastUpdatedDate();

	List<Instrument> getAllInstruments();

	Instrument getInstrument(final String id);

	void save(final List<Instrument> instruments);

	void flush();

}

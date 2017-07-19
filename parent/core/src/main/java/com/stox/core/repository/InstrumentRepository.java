package com.stox.core.repository;

import java.util.Date;
import java.util.List;

import com.stox.core.model.Instrument;

public interface InstrumentRepository {

	Date getLastUpdatedDate(final String dataProviderCode);

	List<Instrument> getAllInstruments(final String dataProviderCode);

	void save(final String dataProviderCode, final List<Instrument> instruments);

}

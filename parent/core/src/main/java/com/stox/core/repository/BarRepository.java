package com.stox.core.repository;

import java.util.Date;
import java.util.List;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;

public interface BarRepository {

	List<Bar> find(final String instrumentId, final BarSpan barSpan, final int count);
	
	List<Bar> find(final String instrumentId, final BarSpan barSpan, final Date from, final Date to);

	void save(final List<Bar> bars, final String instrumentId, final BarSpan barSpan);

	void save(final Bar bar, final String instrumentId, final BarSpan barSpan);

	Date getLastTradingDate(final String instrumentId, final BarSpan barSpan);
	
	Date getFirstTradingDate(final String instrumentId, final BarSpan barSpan);

	public void drop(final Instrument instrument, final BarSpan barSpan) throws Exception;

	void truncate(Instrument instrument, BarSpan barSpan, int barCount) throws Exception;

}

package com.stox.core.repository;

import java.util.Date;
import java.util.List;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;

public interface BarRepository {

	List<Bar> find(final String exchangeCode, final BarSpan barSpan, final Date from, final Date to);

	void save(final List<Bar> bars, final String exchangeCode, final BarSpan barSpan);

	void save(final Bar bar, final String exchangeCode, final BarSpan barSpan);

	Date getLastTradingDate(final String exchangeCode, final BarSpan barSpan);

}

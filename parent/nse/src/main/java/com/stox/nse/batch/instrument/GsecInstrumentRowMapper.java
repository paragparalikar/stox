package com.stox.nse.batch.instrument;

import java.util.Date;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class GsecInstrumentRowMapper implements RowMapper<Instrument> {

	@Override
	public Instrument mapRow(RowSet rowSet) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.GOVERNMENT_SECURITY);
		instrument.setIsin(rowSet.getColumnValue(1));
		instrument.setName(rowSet.getColumnValue(2));
		try {
			final Date date = new Date();
			date.setTime(Long.parseLong(rowSet.getColumnValue(3)));
			instrument.setExpiry(date);
		} catch (NumberFormatException e) {
			instrument.setSymbol(rowSet.getColumnValue(3));
		}
		return instrument;
	}

}

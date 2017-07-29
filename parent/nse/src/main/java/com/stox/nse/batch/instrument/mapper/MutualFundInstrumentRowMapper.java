package com.stox.nse.batch.instrument.mapper;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class MutualFundInstrumentRowMapper implements RowMapper<Instrument> {

	@Override
	public Instrument mapRow(RowSet rowSet) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.MUTUAL_FUND);
		instrument.setSymbol(rowSet.getColumnValue(1));
		instrument.setExchangeCode(instrument.getSymbol());
		instrument.setIsin(rowSet.getColumnValue(2));
		instrument.setName(rowSet.getColumnValue(3));
		return instrument;
	}

}

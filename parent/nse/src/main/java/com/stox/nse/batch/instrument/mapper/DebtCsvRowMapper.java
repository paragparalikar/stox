package com.stox.nse.batch.instrument.mapper;

import com.stox.core.batch.CsvRowMapper;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.StringUtil;

public class DebtCsvRowMapper implements CsvRowMapper<Instrument> {

	@Override
	public Instrument map(String[] tokens) {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.DEBT);
		instrument.setSymbol(StringUtil.get(tokens, 0));
		instrument.setName(StringUtil.get(tokens, 1));
		instrument.setLotSize(StringUtil.parseInt(StringUtil.get(tokens, 5)));
		instrument.setIsin(StringUtil.get(tokens, 14));
		return instrument;
	}

}

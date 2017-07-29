package com.stox.nse.batch.instrument;

import java.util.Date;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.Constant;

public class CorporateBondInstrumentRowMapper implements RowMapper<Instrument> {

	public static void main(String[] args) {
		final Date date = new Date();
		date.setTime(Long.parseLong("1661625000000"));
		System.out.println(Constant.dateFormatFull.format(date));
	}

	@Override
	public Instrument mapRow(RowSet rowSet) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.CORPORATE_BOND);
		instrument.setIsin(rowSet.getColumnValue(1));
		instrument.setSymbol(rowSet.getColumnValue(2));
		instrument.setExchangeCode(instrument.getSymbol());
		instrument.setName(rowSet.getColumnValue(3));
		final Date date = new Date();
		date.setTime(Long.parseLong(rowSet.getColumnValue(5)));
		instrument.setExpiry(date);
		return instrument;
	}

}

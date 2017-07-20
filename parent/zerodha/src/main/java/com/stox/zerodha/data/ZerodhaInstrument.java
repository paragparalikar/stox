package com.stox.zerodha.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.Data;

import com.stox.core.model.Instrument;
import com.stox.core.util.StringUtil;

@Data
public class ZerodhaInstrument {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String instrument_token;

	private String exchange_token;

	private String tradingsymbol;

	private String name;

	private String last_price;

	private String expiry;

	private String strike;

	private String tick_size;

	private String lot_size;

	private String instrument_type;

	private String segment;

	private String exchange;

	public Instrument toInstrument() {
		try {
			final Instrument instrument = new Instrument();
			instrument.setSymbol(tradingsymbol);
			instrument.setExchangeCode(exchange_token);
			instrument.setDataProviderCode(instrument_token);
			instrument.setExchange(exchange);
			instrument.setType(instrument_type);
			instrument.setName(StringUtil.hasText(name) ? name : tradingsymbol);
			if (StringUtil.hasText(expiry)) {
				instrument.setExpiry(dateFormat.parse(expiry));
			}
			if (StringUtil.hasText(lot_size)) {
				instrument.setLotSize(Integer.parseInt(lot_size));
			}
			if (StringUtil.hasText(tick_size)) {
				instrument.setTickSize(Double.parseDouble(tick_size));
			}
			return instrument;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}

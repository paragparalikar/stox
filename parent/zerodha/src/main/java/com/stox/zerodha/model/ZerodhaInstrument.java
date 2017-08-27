package com.stox.zerodha.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.stox.core.model.Instrument;
import com.stox.core.util.StringUtil;
import com.stox.zerodha.util.ZerodhaUtil;

import lombok.Data;

@Data
public class ZerodhaInstrument {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String instrumentToken;

	private String exchangeToken;

	private String tradingsymbol;

	private String name;

	private String lastPrice;

	private String expiry;

	private String strike;

	private String tickSize;

	private String lotSize;

	private String instrumentType;

	private String segment;

	private ZerodhaExchange exchange;

	public Instrument toInstrument() {
		try {
			final Instrument instrument = new Instrument();
			instrument.setSymbol(tradingsymbol);
			instrument.setExchangeCode(exchangeToken);
			instrument.setExchange(ZerodhaUtil.toExchange(exchange));
			instrument.setType(ZerodhaUtil.toInstrumentType(instrumentType));
			instrument.setName(StringUtil.hasText(name) ? name : tradingsymbol);
			if (StringUtil.hasText(expiry)) {
				instrument.setExpiry(dateFormat.parse(expiry));
			}
			if (StringUtil.hasText(strike)) {
				instrument.setStrike(Double.parseDouble(strike));
			}
			if (StringUtil.hasText(lotSize)) {
				instrument.setLotSize(Integer.parseInt(lotSize));
			}
			if (StringUtil.hasText(tickSize)) {
				instrument.setTickSize(Double.parseDouble(tickSize));
			}
			return instrument;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}

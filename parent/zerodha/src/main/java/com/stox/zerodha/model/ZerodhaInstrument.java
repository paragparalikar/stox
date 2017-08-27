package com.stox.zerodha.model;

import java.util.Date;

import com.stox.core.model.Instrument;
import com.stox.core.util.StringUtil;

import lombok.Data;

@Data
public class ZerodhaInstrument {

	private String instrumentToken;

	private String exchangeToken;

	private String tradingsymbol;

	private String name;

	private Double lastPrice;

	private Date expiry;

	private Double strike;

	private Double tickSize;

	private Integer lotSize;

	private ZerodhaInstrumentType instrumentType;

	private ZerodhaSegment segment;

	private ZerodhaExchange exchange;

	public Instrument toInstrument() {
		final Instrument instrument = new Instrument();
		instrument.setSymbol(tradingsymbol);
		instrument.setExchange(null == exchange ? null : exchange.getExchange());
		instrument.setType(null == instrumentType ? null : instrumentType.getInstrumentType());
		instrument.setName(StringUtil.hasText(name) ? name : tradingsymbol);
		instrument.setExpiry(expiry);
		instrument.setStrike(strike);
		instrument.setLotSize(lotSize);
		instrument.setTickSize(tickSize);
		return instrument;
	}

}

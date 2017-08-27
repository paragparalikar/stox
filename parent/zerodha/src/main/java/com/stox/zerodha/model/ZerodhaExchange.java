package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.stox.core.model.Exchange;

public enum ZerodhaExchange {

	MCX("MCX", Exchange.MCX), NFO("NFO", Exchange.NSE), NSE("NSE", Exchange.NSE), CDS("CDS", Exchange.CDS), BFO("BFO",
			Exchange.BSE), BSE("BSE", Exchange.BSE);

	@JsonCreator
	public static ZerodhaExchange findByCode(final String code) {
		for (final ZerodhaExchange exchange : values()) {
			if (exchange.getCode().equalsIgnoreCase(code)) {
				return exchange;
			}
		}
		return null;
	}

	private final String code;
	private final Exchange exchange;

	private ZerodhaExchange(final String code, final Exchange exchange) {
		this.code = code;
		this.exchange = exchange;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	public Exchange getExchange() {
		return exchange;
	}

}

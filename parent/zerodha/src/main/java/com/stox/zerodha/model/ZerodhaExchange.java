package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZerodhaExchange {
	
	MCX("MCX"), NFO("NFO"), NSE("NSE"), CDS("CDS"), BFO("BFO"), BSE("BSE");
	
	@JsonCreator
	public static ZerodhaExchange findByCode(final String code) {
		for(final ZerodhaExchange exchange : values()) {
			if(exchange.getCode().equalsIgnoreCase(code)) {
				return exchange;
			}
		}
		return null;
	}

	private final String code;
	
	private ZerodhaExchange(final String code) {
		this.code = code;
	}
	
	@JsonValue
	public String getCode() {
		return code;
	}
	
}

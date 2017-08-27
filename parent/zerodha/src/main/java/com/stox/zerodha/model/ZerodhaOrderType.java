package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZerodhaOrderType {

	MARKET("MARKET"), LIMIT("LIMIT"), SL("SL"), SLM("SL-M");
	
	@JsonCreator
	public static ZerodhaOrderType findByCode(final String code) {
		for(final ZerodhaOrderType orderType : values()) {
			if(orderType.getCode().equalsIgnoreCase(code)) {
				return orderType;
			}
		}
		return null;
	}
	
	private final String code;
	
	private ZerodhaOrderType(final String code) {
		this.code = code;
	}
	
	@JsonValue
	public String getCode() {
		return code;
	}
	
}

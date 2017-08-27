package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZerodhaInstrumentType {

	CE("CE"), EQ("EQ"), FUT("FUT"), PE("PE");
	
	@JsonCreator
	public static ZerodhaInstrumentType findByCode(final String code) {
		for(final ZerodhaInstrumentType zerodhaInstrumentType : values()) {
			if(zerodhaInstrumentType.getCode().equalsIgnoreCase(code)) {
				return zerodhaInstrumentType;
			}
		}
		return null;
	}
	
	private final String code;
	
	private ZerodhaInstrumentType(final String code) {
		this.code = code;
	}
	
	@JsonValue
	public String getCode() {
		return code;
	}
	
}

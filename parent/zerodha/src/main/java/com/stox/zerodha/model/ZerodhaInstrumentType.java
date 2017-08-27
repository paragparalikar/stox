package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.stox.core.model.InstrumentType;

public enum ZerodhaInstrumentType {

	CE("CE", InstrumentType.CALL), EQ("EQ", InstrumentType.EQUITY), FUT("FUT", InstrumentType.FUTURE), PE("PE", InstrumentType.PUT);
	
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
	private final InstrumentType instrumentType;
	
	private ZerodhaInstrumentType(final String code, final InstrumentType instrumentType) {
		this.code = code;
		this.instrumentType = instrumentType;
	}
	
	@JsonValue
	public String getCode() {
		return code;
	}
	
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
	
}

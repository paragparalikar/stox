package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZerodhaSegment {

	BFOFUT("BFO-FUT"), BFOOPT("BFO-OPT"), BSE("BSE"), CDSFUT("CDS-FUT"), CDSOPT("CDS-OPT"), MCX("MCX"), NFOFUT(
			"NFO-FUT"), NFOOPT("NFO-OPT"), NSE("NSE"), NSEIND("NSE-INDICES");

	@JsonCreator
	public static ZerodhaSegment findByCode(final String code) {
		for (final ZerodhaSegment segment : values()) {
			if (segment.getCode().equalsIgnoreCase(code)) {
				return segment;
			}
		}
		return null;
	}

	private final String code;

	private ZerodhaSegment(final String code) {
		this.code = code;
	}

	@JsonValue
	public String getCode() {
		return code;
	}

}

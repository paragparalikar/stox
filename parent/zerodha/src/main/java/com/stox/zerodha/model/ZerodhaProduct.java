package com.stox.zerodha.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ZerodhaProduct {

	CO("CO"), MIS("MIS"), NRML("NRML"), CNC("CNC");
	
	@JsonCreator
	public static ZerodhaProduct findByCode(final String code) {
		for(final ZerodhaProduct product : values()) {
			if(product.getCode().equalsIgnoreCase(code)) {
				return product;
			}
		}
		return null;
	}
	
	private final String code;
	
	private ZerodhaProduct(final String code) {
		this.code = code;
	}
	
	@JsonValue
	public String getCode() {
		return code;
	}
	
}

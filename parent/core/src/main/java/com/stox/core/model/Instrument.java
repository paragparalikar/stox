package com.stox.core.model;

import java.util.Date;

import lombok.Data;

import com.stox.core.intf.Identifiable;
import com.stox.core.intf.Nameable;
import com.stox.core.util.StringUtil;

@Data
public class Instrument implements Identifiable<String>, Nameable {

	private String exchangeCode;

	private String symbol;

	private String isin;

	private Exchange exchange;

	private String name;

	private Date expiry;

	private double strike;

	private int lotSize;

	private double tickSize;

	private InstrumentType type;

	@Override
	public String getId() {
		return isin;
	}

	@Override
	public void setId(String id) {
		this.isin = id;
	}

	@Override
	public String getName() {
		return StringUtil.hasText(name) ? name : symbol;
	}

	@Override
	public String toString() {
		return getName();
	}
}

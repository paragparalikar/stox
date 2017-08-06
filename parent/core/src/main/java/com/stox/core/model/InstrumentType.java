package com.stox.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.stox.core.intf.HasId;
import com.stox.core.intf.HasName;

public enum InstrumentType implements HasName, HasId<String> {

	//@formatter:off
	EQUITY("EQ", "Equities"), 
	CALL("CE", "Call Options"), 
	PUT("PE", "Put Options"), 
	FUTURE("FUT", "Futures"), 
	MUTUAL_FUND("MF", "Mutual Funds"), 
	MUTUAL_FUND_CE("MFCE","Mutual Funds(Close ended)"),
	CORPORATE_BOND("CB", "Corporate Bonds"), 
	GOVERNMENT_SECURITY("GSEC","Government Bonds & Treasury Bills"),
	DEPOSITORY_RECEIPTS("DR","Depository Receipts"),
	PREFERENCE_SHARES("PS","Preference Shares"),
	DEBT("DE","Debt Instruments"),
	WARRANTS("WR","Warrants"),
	ETF("ETF","Exchange Traded Funds"),
	
	INDEX("ID","Indices");
	//@formatter:on

	@JsonCreator
	public static InstrumentType findById(final String id) {
		for (final InstrumentType type : values()) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}

	private final String id;
	private final String name;

	private InstrumentType(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	@JsonValue
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

}

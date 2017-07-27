package com.stox.core.model;

import com.stox.core.intf.HasId;
import com.stox.core.intf.HasName;

public enum InstrumentType implements HasName, HasId<String> {

	EQ("EQ", "Equities"), CALL("CE", "Call Options"), PUT("PE", "Put Options"), FUT("FUT", "Futures"), MF("MF", "Mutual Funds"), CB("CB", "Corporate Bonds"), GB("GB",
			"Government Bonds"), TB("TB", "Treasury Bills");

	private final String id;
	private final String name;

	private InstrumentType(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

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

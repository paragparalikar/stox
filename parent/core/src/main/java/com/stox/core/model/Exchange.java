package com.stox.core.model;

import com.stox.core.intf.HasId;
import com.stox.core.intf.HasName;

public enum Exchange implements HasName, HasId<String> {
	NSE("NSE", "National Stock Exchange"), BSE("BSE", "Bombay Stock Exchange"), CDS("CDS", "Currency Derivatives"), MCX("MCX", "Multi Commodity Exchange");

	private final String id;
	private final String name;

	private Exchange(final String id, final String name) {
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

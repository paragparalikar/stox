package com.stox.core.model;

import com.stox.core.intf.HasId;
import com.stox.core.intf.HasName;

public enum BarValueType implements HasId<String>, HasName {

	OPEN("OPEN", "Open") {
		@Override
		public double getValue(final Bar bar) {
			return bar.getOpen();
		}
	},
	HIGH("HIGH", "High") {
		@Override
		public double getValue(final Bar bar) {
			return bar.getHigh();
		}
	},
	LOW("LOW", "Low") {
		@Override
		public double getValue(final Bar bar) {
			return bar.getLow();
		}
	},
	CLOSE("CLOSE", "Close") {
		@Override
		public double getValue(final Bar bar) {
			return bar.getClose();
		}
	},
	VOLUME("VOLUME", "Volume") {
		@Override
		public double getValue(final Bar bar) {
			return bar.getVolume();
		}
	};

	private final String id, name;

	private BarValueType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public abstract double getValue(final Bar bar);

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

}

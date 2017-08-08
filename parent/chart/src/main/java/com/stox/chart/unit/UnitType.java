package com.stox.chart.unit;

public enum UnitType {

	LINE("line", true, true), BAR("bar", true, true), AREA("area", true, true), HLC("hlc", true, true), OHLC("ohlc", true, true), CANDLE("candle", true, true), HEIKEN_ASHI(
			"heikin-ashi", true, true), RENKO("renko", true, false), KAGI("kagi", true, false), LINE_BREAK("line-break", true, false), PNF("pnf", true, false), EQUI_VOLUME(
			"equi-volume", false, true), CANDLE_VOLUME("candle-volume", false, true);

	private final String key;
	private final boolean uniformWidth;
	private final boolean uniformTime;

	private UnitType(final String key, final boolean uniformWidth, final boolean uniformTime) {
		this.key = key;
		this.uniformWidth = uniformWidth;
		this.uniformTime = uniformTime;
	}

	public String getKey() {
		return key;
	}

	public boolean isUniformWidth() {
		return uniformWidth;
	}

	public boolean isUniformTime() {
		return uniformTime;
	}

}

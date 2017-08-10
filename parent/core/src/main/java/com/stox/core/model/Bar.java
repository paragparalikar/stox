package com.stox.core.model;

import java.util.Date;

import lombok.Data;

import com.stox.core.intf.Range;
import com.stox.core.util.StringUtil;

@Data
public class Bar implements Range, Comparable<Bar> {

	public static final int BYTES = 56;

	private Date date;

	private double open;

	private double high;

	private double low;

	private double close;

	private double previousClose;

	private double volume;

	private String instrumentId;

	public void copy(final Bar bar) {
		date = bar.getDate();
		open = bar.getOpen();
		high = bar.getHigh();
		low = bar.getLow();
		close = bar.getClose();
		previousClose = 0 < bar.getPreviousClose() ? bar.getPreviousClose() : previousClose;
		volume = 0 < bar.getVolume() ? bar.getVolume() : volume;
		instrumentId = StringUtil.hasText(bar.getInstrumentId()) ? bar.getInstrumentId() : instrumentId;
	}

	@Override
	public double getValue() {
		return getClose();
	}

	@Override
	public int compareTo(Bar other) {
		return other.getDate().compareTo(date);
	}

}

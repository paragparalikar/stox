package com.stox.core.model;

import java.util.Date;

import lombok.Data;

import com.stox.core.intf.Range;

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

	@Override
	public double getValue() {
		return getClose();
	}

	@Override
	public int compareTo(Bar other) {
		return other.getDate().compareTo(date);
	}

}

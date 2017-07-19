package com.stox.core.model;

import java.util.Date;

import lombok.Data;

@Data
public class Bar implements Comparable<Bar> {

	private Date date;

	private double open;

	private double high;

	private double low;

	private double close;

	private double previousClose;

	private double volume;

	@Override
	public int compareTo(Bar other) {
		return other.getDate().compareTo(date);
	}

}

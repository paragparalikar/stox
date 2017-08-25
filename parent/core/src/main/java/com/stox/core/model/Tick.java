package com.stox.core.model;

import java.util.Date;

import lombok.Data;

@Data
public class Tick {

	private Date lastTradeDate;
	
	private int lastTradeSize;
	
	private double lastTradePrice;
	
	private Instrument instrument;
	
}

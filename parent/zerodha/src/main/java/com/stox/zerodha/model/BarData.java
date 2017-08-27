package com.stox.zerodha.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stox.core.model.Bar;

import lombok.Data;

@Data
public class BarData {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	@JsonProperty("candles")
	private String[][] candles;
	
	@JsonIgnore
	private final List<Bar> bars = new ArrayList<>();
	
	public synchronized List<Bar> getBars(){
		if((null != candles || 0 < candles.length) && bars.isEmpty()) {
			Arrays.stream(candles).forEach(values -> {
				if(null != values && 6 == values.length) {
					try {
						final Bar bar = new Bar();
						bars.add(bar);
						bar.setDate(DATEFORMAT.parse(values[0]));
						bar.setOpen(Double.parseDouble(values[1]));
						bar.setHigh(Double.parseDouble(values[2]));
						bar.setLow(Double.parseDouble(values[3]));
						bar.setClose(Double.parseDouble(values[4]));
						bar.setVolume(Double.parseDouble(values[5]));
					} catch (ParseException exception) {
						exception.printStackTrace();
					}
				}
			});
			Collections.sort(bars);
		}
		return bars;
	}
	
}

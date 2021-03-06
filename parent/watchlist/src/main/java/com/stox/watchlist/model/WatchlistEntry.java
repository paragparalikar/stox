package com.stox.watchlist.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stox.core.intf.HasName;
import com.stox.core.intf.Identifiable;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;

import lombok.Data;

@Data
public class WatchlistEntry implements Identifiable<String>, Comparable<WatchlistEntry>, HasName  {

	private String id;
	
	private Integer watchlistId;
	
	private String instrumentId;
	
	private BarSpan barSpan;

	@JsonIgnore
	private Instrument instrument;
	
	@Override
	public int compareTo(WatchlistEntry o) {
		return instrument.getName().compareToIgnoreCase(o.getInstrument().getName());
	}
	
	@Override
	@JsonIgnore
	public String getName() {
		return null == instrument ? "" : instrument.getName();
	}

}

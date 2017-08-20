package com.stox.watchlist.model;

import com.stox.core.intf.HasName;
import com.stox.core.intf.Identifiable;

import lombok.Data;

@Data
public class Watchlist implements Identifiable<Integer>, Comparable<Watchlist>, HasName {
	
	private Integer id;
	
	private String name;

	@Override
	public int compareTo(Watchlist o) {
		return name.compareToIgnoreCase(o.getName());
	}

}

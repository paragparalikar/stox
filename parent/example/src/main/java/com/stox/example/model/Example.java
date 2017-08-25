package com.stox.example.model;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stox.core.intf.HasName;
import com.stox.core.intf.Identifiable;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;

import lombok.Data;

@Data
public class Example implements Identifiable<String>, Comparable<Example>, HasName  {

	private String id;
	
	private Integer exampleGroupId;
	
	private String instrumentId;
	
	private BarSpan barSpan;
	
	private Date date;

	@JsonIgnore
	private Instrument instrument;
	
	@Override
	public int compareTo(Example o) {
		return instrument.getName().compareToIgnoreCase(o.getInstrument().getName());
	}
	
	@Override
	@JsonIgnore
	public String getName() {
		return null == instrument ? "" : instrument.getName();
	}

}

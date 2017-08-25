package com.stox.example.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class ExampleExistsException extends RuntimeException {
	private static final long serialVersionUID = 5631930061504364985L;

	private final Example example;
	
	@Override
	public String getMessage() {
		return "Example for \""+example.getInstrument().getName()+"\" with timeframe \""+example.getBarSpan().getName()+"\" already exists";
	}
	
}

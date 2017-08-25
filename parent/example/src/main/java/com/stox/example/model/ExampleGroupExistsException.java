package com.stox.example.model;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class ExampleGroupExistsException extends RuntimeException {
	private static final long serialVersionUID = 4995810143286572603L;
	
	private final ExampleGroup exampleGroup;
	
	@Override
	public String getMessage() {
		return "ExampleGroup with name \""+exampleGroup.getName()+"\" already exists";
	}
	
}

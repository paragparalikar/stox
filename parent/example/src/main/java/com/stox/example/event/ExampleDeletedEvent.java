package com.stox.example.event;

import org.springframework.context.ApplicationEvent;

import com.stox.example.model.Example;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class ExampleDeletedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -196292925245318080L;

	private final Example example;
	
	public ExampleDeletedEvent(Object source, final Example example) {
		super(source);
		this.example = example;
	}

}

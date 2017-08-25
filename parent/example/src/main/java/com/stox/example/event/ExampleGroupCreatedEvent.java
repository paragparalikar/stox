package com.stox.example.event;

import org.springframework.context.ApplicationEvent;

import com.stox.example.model.ExampleGroup;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=true)
public class ExampleGroupCreatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -8338988758665039065L;

	private final ExampleGroup exampleGroup;
	
	public ExampleGroupCreatedEvent(Object source, final ExampleGroup exampleGroup) {
		super(source);
		this.exampleGroup = exampleGroup;
	}

}

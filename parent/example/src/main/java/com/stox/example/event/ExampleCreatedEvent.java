package com.stox.example.event;

import org.springframework.context.ApplicationEvent;

import com.stox.example.model.Example;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ExampleCreatedEvent extends ApplicationEvent {
	private static final long serialVersionUID = 4217763403413279769L;

	private final Example example;
	
	public ExampleCreatedEvent(Object source, final Example example) {
		super(source);
		this.example = example;
	}

}

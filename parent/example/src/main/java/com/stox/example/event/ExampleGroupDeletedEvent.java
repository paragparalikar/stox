package com.stox.example.event;

import org.springframework.context.ApplicationEvent;

import com.stox.example.model.ExampleGroup;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ExampleGroupDeletedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2626654711689209571L;

	private final ExampleGroup exampleGroup;

	public ExampleGroupDeletedEvent(Object source, final ExampleGroup exampleGroup) {
		super(source);
		this.exampleGroup = exampleGroup;
	}
	
}

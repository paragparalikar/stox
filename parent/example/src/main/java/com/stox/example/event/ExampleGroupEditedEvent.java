package com.stox.example.event;

import org.springframework.context.ApplicationEvent;

import com.stox.example.model.ExampleGroup;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ExampleGroupEditedEvent extends ApplicationEvent {
	private static final long serialVersionUID = -6095825616781613013L;

	private final ExampleGroup exampleGroup;

	public ExampleGroupEditedEvent(Object source, final ExampleGroup exampleGroup) {
		super(source);
		this.exampleGroup = exampleGroup;
	}

}

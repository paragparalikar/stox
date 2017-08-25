package com.stox.example.ui;

import com.stox.example.model.ExampleGroup;

import javafx.util.StringConverter;

public class ExampleGroupStringConverter extends StringConverter<ExampleGroup> {

	@Override
	public String toString(ExampleGroup exampleGroup) {
		return null == exampleGroup ? "" : exampleGroup.getName();
	}

	@Override
	public ExampleGroup fromString(String string) {
		return null;
	}

}

package com.stox.core.ui;

import javafx.util.StringConverter;

import com.stox.core.intf.HasName;

public class HasNameStringConverter<T extends HasName> extends StringConverter<T> {

	@Override
	public String toString(T object) {
		return object.getName();
	}

	@Override
	public T fromString(String string) {
		return null;
	}

}
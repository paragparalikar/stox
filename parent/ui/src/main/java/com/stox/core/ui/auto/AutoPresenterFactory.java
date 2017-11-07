package com.stox.core.ui.auto;

import java.lang.reflect.Field;

public class AutoPresenterFactory {

	public AutoPresenter get(final Field field, final Object model) {
		field.setAccessible(true);
		final Class<?> type = field.getType();
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return new AutoCheckBoxPresenter(field, model);
		}else if(type.isEnum()) { 
			return new AutoChoiceBoxPresenter(field, model);
		}else {
			return new AutoTextFieldPresenter(field, model);
		}
	}
	
}

package com.stox.core.util.spring;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;

import com.stox.core.util.StringUtil;

public class DateFormatPropertyEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(StringUtil.hasText(text) ? new SimpleDateFormat(text) : null);
	}

}

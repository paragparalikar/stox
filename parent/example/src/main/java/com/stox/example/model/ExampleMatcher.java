package com.stox.example.model;

import com.stox.core.ui.widget.AbstractSearchTextField.Callback;
import com.stox.core.util.StringUtil;

public class ExampleMatcher implements Callback<Example>{

	@Override
	public boolean call(Example item, String text) {
		return null == item || null == item.getInstrument() || !StringUtil.hasText(text) ? false : item.getInstrument().getName().trim().toLowerCase().contains(text.toLowerCase().trim());
	}
}

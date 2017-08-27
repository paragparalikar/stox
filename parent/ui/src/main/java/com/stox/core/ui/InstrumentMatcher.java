package com.stox.core.ui;

import com.stox.core.model.Instrument;
import com.stox.core.ui.widget.AbstractSearchTextField.Callback;
import com.stox.core.util.StringUtil;

public class InstrumentMatcher implements Callback<Instrument> {

	@Override
	public boolean call(Instrument instrument, String text) {
		text = text.trim().toLowerCase();
		boolean result = false;
		if (null != instrument) {
			result = StringUtil.hasText(instrument.getName()) && instrument.getName().toLowerCase().contains(text);
			if (!result) {
				result = StringUtil.hasText(instrument.getSymbol()) && instrument.getSymbol().toLowerCase().contains(text);
				if (!result) {
					result = StringUtil.hasText(instrument.getIsin()) && instrument.getIsin().toLowerCase().contains(text);
					if (!result) {
						result = StringUtil.hasText(instrument.getSymbol()) && instrument.getSymbol().toLowerCase().contains(text);
					}
				}
			}
		}
		return result;
	}

}
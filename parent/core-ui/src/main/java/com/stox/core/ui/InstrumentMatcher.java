package com.stox.core.ui;

import com.stox.core.model.Instrument;
import com.stox.core.ui.widget.AbstractSearchTextField.Callback;

public class InstrumentMatcher implements Callback<Instrument> {

	@Override
	public boolean call(Instrument instrument, String text) {
		text = text.trim().toLowerCase();
		return instrument.getName().trim().toLowerCase().contains(text) || instrument.getSymbol().trim().toLowerCase().contains(text) || instrument.getExchangeCode().contains(text);
	}

}
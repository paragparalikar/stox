package com.stox.zerodha.ui;

import java.util.List;

import com.stox.core.model.Instrument;

public class InstrumentFilterPresenter {

	private final List<Instrument> source;
	private final List<Instrument> target;
	private final InstrumentFilterView view = new InstrumentFilterView();

	public InstrumentFilterPresenter(final List<Instrument> source, final List<Instrument> target) {
		this.source = source;
		this.target = target;
	}

}

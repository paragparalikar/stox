package com.stox.core.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.stox.core.model.Instrument;

public class InstrumentsChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 3733934408243484605L;

	private final List<Instrument> instruments;

	public InstrumentsChangedEvent(final Object source, final List<Instrument> instruments) {
		super(source);
		this.instruments = instruments;
	}

	public List<Instrument> getInstruments() {
		return instruments;
	}

}

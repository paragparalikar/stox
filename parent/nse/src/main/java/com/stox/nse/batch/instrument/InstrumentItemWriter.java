package com.stox.nse.batch.instrument;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.stox.core.model.Instrument;
import com.stox.core.repository.InstrumentRepository;

public class InstrumentItemWriter implements ItemWriter<Instrument> {

	private final InstrumentRepository instrumentRepository;

	public InstrumentItemWriter(final InstrumentRepository instrumentRepository) {
		this.instrumentRepository = instrumentRepository;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(List<? extends Instrument> instruments) throws Exception {
		instrumentRepository.save((List<Instrument>) instruments);
	}

}

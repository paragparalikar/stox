package com.stox.nse.data.instrument;

import java.io.InputStream;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class IndexDownloader extends CsvDownloader<Instrument> {

	public IndexDownloader() {
		super(null, 1);
	}

	@Override
	public Instrument parse(String[] tokens) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.INDEX);
		instrument.setIsin(tokens[0]);
		instrument.setExchangeCode(tokens[1]);
		instrument.setName(tokens[2]);
		return instrument;
	}

	@Override
	protected InputStream createInputStream() throws Exception {
		return getClass().getResourceAsStream("/indexes.csv");
	}

}

package com.stox.nse.data.bar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Bar;
import com.stox.core.model.Instrument;
import com.stox.core.repository.InstrumentRepository;

public class NseBarIndexDownloader extends CsvDownloader<Bar> {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("dd-MM-yyyy");

	private final InstrumentRepository instrumentRepository;

	public NseBarIndexDownloader(String url, final InstrumentRepository instrumentRepository) {
		super(url, 1);
		this.instrumentRepository = instrumentRepository;
	}

	@Override
	public Bar parse(String[] tokens) throws Exception {
		final Bar bar = new Bar();
		final Instrument instrument = instrumentRepository.findBySymbol(tokens[0]);
		bar.setInstrumentId(instrument.getId());
		bar.setDate(DATEFORMAT.parse(tokens[1]));
		bar.setOpen(Double.parseDouble(tokens[2]));
		bar.setHigh(Double.parseDouble(tokens[3]));
		bar.setLow(Double.parseDouble(tokens[4]));
		bar.setClose(Double.parseDouble(tokens[5]));
		bar.setPreviousClose(bar.getClose() - Double.parseDouble(tokens[6]));
		bar.setVolume(Double.parseDouble(tokens[8]));
		return bar;
	}

}

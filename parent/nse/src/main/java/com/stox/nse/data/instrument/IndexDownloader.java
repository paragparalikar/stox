package com.stox.nse.data.instrument;

import java.io.InputStream;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.StringUtil;

public class IndexDownloader extends CsvDownloader<Instrument> {

	public IndexDownloader() {
		super(null, 1);
	}

	@Override
	public Instrument parse(String[] tokens) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.INDEX);
		instrument.setIsin(StringUtil.trim(tokens[0]));
		instrument.setExchangeCode(StringUtil.trim(tokens[1]));
		instrument.setSymbol(StringUtil.trim(instrument.getExchangeCode()));
		instrument.setName(StringUtil.trim(tokens[2]));
		return instrument;
	}

	@Override
	protected InputStream inputStream() throws Exception {
		return getClass().getResourceAsStream("/indexes.csv");
	}

}

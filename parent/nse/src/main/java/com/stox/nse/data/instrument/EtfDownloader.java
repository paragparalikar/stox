package com.stox.nse.data.instrument;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.StringUtil;

public class EtfDownloader extends CsvDownloader<Instrument> {

	public EtfDownloader(String url) {
		super(url, 1);
	}

	@Override
	public Instrument parse(String[] tokens) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.ETF);
		instrument.setSymbol(StringUtil.get(tokens, 0));
		instrument.setExchangeCode(instrument.getSymbol());
		instrument.setName(StringUtil.get(tokens, 2));
		instrument.setLotSize(StringUtil.parseInt(StringUtil.get(tokens, 4)));
		instrument.setIsin(StringUtil.get(tokens, 5));
		return instrument;
	}

}

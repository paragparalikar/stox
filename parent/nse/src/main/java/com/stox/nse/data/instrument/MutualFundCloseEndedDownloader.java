package com.stox.nse.data.instrument;

import com.stox.core.downloader.CsvDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.StringUtil;

public class MutualFundCloseEndedDownloader extends CsvDownloader<Instrument> {

	public MutualFundCloseEndedDownloader(String url) {
		super(url, 1);
	}

	@Override
	public Instrument parse(String[] tokens) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.MUTUAL_FUND_CE);
		instrument.setSymbol(StringUtil.get(tokens, 0));
		instrument.setName(StringUtil.get(tokens, 1));
		instrument.setLotSize(StringUtil.parseInt(StringUtil.get(tokens, 5)));
		instrument.setIsin(StringUtil.get(tokens, 6));
		return instrument;
	}

}

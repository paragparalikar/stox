package com.stox.nse.data.instrument;

import org.apache.poi.ss.usermodel.Row;

import com.stox.core.downloader.ZipExcelDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class GsecDownloader extends ZipExcelDownloader<Instrument> {

	public GsecDownloader(String url) {
		super(url, 1);
	}

	@Override
	public Instrument parse(Row row) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.GOVERNMENT_SECURITY);
		instrument.setIsin(row.getCell(1).getStringCellValue());
		instrument.setName(row.getCell(2).getStringCellValue());
		try {
			instrument.setExpiry(row.getCell(3).getDateCellValue());
		} catch (NumberFormatException | IllegalStateException e) {
			instrument.setSymbol(row.getCell(3).getStringCellValue());
			instrument.setExchangeCode(instrument.getSymbol());
		}
		return instrument;
	}

}

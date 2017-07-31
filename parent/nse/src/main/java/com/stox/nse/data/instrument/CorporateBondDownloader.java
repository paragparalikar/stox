package com.stox.nse.data.instrument;

import org.apache.poi.ss.usermodel.Row;

import com.stox.core.downloader.ZipExcelDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class CorporateBondDownloader extends ZipExcelDownloader<Instrument> {

	public CorporateBondDownloader(String url) {
		super(url, 1);
	}

	@Override
	public Instrument parse(Row row) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.CORPORATE_BOND);
		instrument.setIsin(row.getCell(1).getStringCellValue());
		instrument.setSymbol(row.getCell(2).getStringCellValue());
		instrument.setExchangeCode(instrument.getSymbol());
		instrument.setName(row.getCell(3).getStringCellValue());
		instrument.setExpiry(row.getCell(5).getDateCellValue());
		return instrument;
	}

}

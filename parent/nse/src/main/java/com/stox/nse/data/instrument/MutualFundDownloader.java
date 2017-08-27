package com.stox.nse.data.instrument;

import org.apache.poi.ss.usermodel.Row;

import com.stox.core.downloader.ZipExcelDownloader;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class MutualFundDownloader extends ZipExcelDownloader<Instrument> {

	public MutualFundDownloader(String url) {
		super(url, 1);
	}

	@Override
	public Instrument parse(Row row) throws Exception {
		final Instrument instrument = new Instrument();
		instrument.setExchange(Exchange.NSE);
		instrument.setType(InstrumentType.MUTUAL_FUND);
		instrument.setSymbol(row.getCell(1).getStringCellValue());
		instrument.setIsin(row.getCell(2).getStringCellValue());
		instrument.setName(row.getCell(3).getStringCellValue());
		return instrument;
	}

}

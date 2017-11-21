package com.stox.nse.data.bar;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.BinaryFileBarRepository;
import com.stox.core.util.Constant;

public class NseIntradayDataDownloadUtil {

	private static final List<BarSpan> barSpans = Arrays.asList(BarSpan.M5, BarSpan.M10, BarSpan.M15, BarSpan.M30,
			BarSpan.H);
	private static final String downloadUrl = "https://www.nseindia.com/ChartApp/install/charts/data/GetDataAll.jsp";
	private static final BarRepository barRepository = new BinaryFileBarRepository();
	private static final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();
	private static final String instrumentFilePath = Constant.PATH + "instrument" + File.separator
			+ "com.stox.instruments.nse.csv";

	public static void main(String[] args) throws Exception {
		final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Instrument.class);
		final List<Instrument> instruments = reader.<Instrument>readValues(new File(instrumentFilePath)).readAll();
		instruments.stream().filter(instrument -> {
			return Exchange.NSE.equals(instrument.getExchange()) && InstrumentType.EQUITY.equals(instrument.getType());
		}).parallel().forEach(instrument -> {
			final NseBarLengthDownloader downloader = new NseBarLengthDownloader(downloadUrl, instrument,
					NseBarLengthDownloader.PERIODICITY_MIN1, NseBarLengthDownloader.PERIOD_TYPE_INTRADAY);
			try {
				final List<Bar> bars = downloader.download();
				barRepository.save(bars, instrument.getId(), BarSpan.M1);
				System.out.printf("%3d\t%2s\t%s\n", bars.size(), BarSpan.M1.getShortName(), instrument.getName());
				barSpans.stream().forEach(barSpan -> {
					final List<Bar> mergedBars = barSpan.merge(bars);
					barRepository.save(mergedBars, instrument.getId(), barSpan);
					System.out.printf("%3d\t%2s\t%s\n", mergedBars.size(), barSpan.getShortName(),
							instrument.getName());
				});
			} catch (Exception e) {
				System.err.println(instrument.getName() + "\t" + e.getMessage());
			}
		});
	}

}

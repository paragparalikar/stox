package com.stox.google.data.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import com.stox.google.data.bar.GoogleBarDownloader;

public class GoogleBarDownloadUtil {

	private static final String downloadedInstrumentsPath = Constant.PATH + "google" + File.separator
			+ "com.stox.google.data.instruments.downloaded.csv";
	private static final List<BarSpan> barSpans = Arrays.asList(BarSpan.M15, BarSpan.M30, BarSpan.H);
	private static final String downloadUrl = "http://www.google.com/finance/getprices?q={symbol}&x={exchange}&i={intervalInSeconds}&p={period}&f=d,c,h,l,o,v&df=cpct&auto=1";
	private static final BarRepository barRepository = new BinaryFileBarRepository();
	private static final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();
	private static final String instrumentFilePath = Constant.PATH + "instrument" + File.separator
			+ "com.stox.instruments.nse.csv";

	public static void main(String[] args) throws Exception {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -20);
		final Date start = calendar.getTime();
		final List<String> downloadedInstrumentIds = getDownloadedInstrumentIds();
		final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Instrument.class);
		final List<Instrument> instruments = reader.<Instrument>readValues(new File(instrumentFilePath)).readAll();
		instruments.stream().filter(instrument -> {
			return Exchange.NSE.equals(instrument.getExchange()) && InstrumentType.EQUITY.equals(instrument.getType())
					&& !downloadedInstrumentIds.contains(instrument.getId());
		}).forEachOrdered(instrument -> {
			barSpans.stream().forEach(barSpan -> {
				final GoogleBarDownloader googleBarDownloader = new GoogleBarDownloader(downloadUrl, start, barSpan, instrument);
				try {
					final List<Bar> bars = googleBarDownloader.download();
					barRepository.save(bars, instrument.getId(), barSpan);
					saveDownloadedInstrumentId(instrument.getId());
					System.out.printf("%3d\t%2s\t%s\n", bars.size(), barSpan.getShortName(), instrument.getName());
				} catch (Exception e) {
					System.out.printf("%2s\t%s - %s\n", barSpan.getShortName(), instrument.getName(), e.getMessage());
				}
			});
		});
		Files.deleteIfExists(Paths.get(downloadedInstrumentsPath));
	}

	private static List<String> getDownloadedInstrumentIds() throws IOException {
		final File file = new File(downloadedInstrumentsPath);
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			return Collections.emptyList();
		}
		return Files.readAllLines(Paths.get(downloadedInstrumentsPath));
	}

	private static void saveDownloadedInstrumentId(final String instrumentId) throws IOException {
		Files.write(Paths.get(downloadedInstrumentsPath), (instrumentId + Constant.LINEFEED).getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND,
				StandardOpenOption.SYNC);
	}

}

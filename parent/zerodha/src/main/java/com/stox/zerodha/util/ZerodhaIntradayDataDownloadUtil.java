package com.stox.zerodha.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import com.stox.zerodha.ZerodhaInstrumentRepository;
import com.stox.zerodha.data.ZerodhaBarDownloader;
import com.stox.zerodha.model.ZerodhaInstrument;
import com.stox.zerodha.model.ZerodhaResponse;
import com.stox.zerodha.model.ZerodhaSession;

public class ZerodhaIntradayDataDownloadUtil {

	private static final String downloadedInstrumentsPath = Constant.PATH + "zerodha" + File.separator
			+ "com.stox.zerodha.data.instruments.bars.downloaded.csv";
	private static final String zerodhaSessionFilePath = Constant.PATH + "zerodha" + File.separator +"com.stox.zerodha.session.json";
	private static final List<BarSpan> barSpans = Arrays.asList(BarSpan.H, BarSpan.M30, BarSpan.M15);
	private static final BarRepository barRepository = new BinaryFileBarRepository();
	private static final CsvSchema schema = Constant.csvMapper.schemaFor(Instrument.class).withHeader();
	private static final String instrumentFilePath = Constant.PATH + "instrument" + File.separator
			+ "com.stox.instruments.nse.csv";
	private static final String zerodhaBarDownloadUrl = "https://kitecharts.zerodha.com/api/chart/";
	private static final ZerodhaInstrumentRepository zerodhaInstrumentRepository = new ZerodhaInstrumentRepository();
	
	private static Date getStart(final BarSpan barSpan) {
		final Calendar calendar = Calendar.getInstance();
		switch(barSpan) {
		case H : 
			calendar.add(Calendar.DATE, -360);
			break;
		case M30: 
			calendar.add(Calendar.DATE, -90);
			break;
		case M15 :
			calendar.add(Calendar.DATE, -90);
		}
		return calendar.getTime();
	}
	

	public static void main(String[] args) throws Exception {
		final Date end = new Date();
		final ZerodhaSession session = getSession();
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final List<String> downloadedInstrumentIds = getDownloadedInstrumentIds();
		final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Instrument.class);
		final List<Instrument> instruments = reader.<Instrument>readValues(new File(instrumentFilePath)).readAll();
		instruments.stream().filter(instrument -> {
			return Exchange.NSE.equals(instrument.getExchange()) && InstrumentType.EQUITY.equals(instrument.getType())
					&& !downloadedInstrumentIds.contains(instrument.getId());
		}).forEachOrdered(instrument -> {
			ZerodhaInstrument zerodhaInstrument;
			try {
				zerodhaInstrument = zerodhaInstrumentRepository.findByInstrument(instrument);
				if (null != zerodhaInstrument) {
					barSpans.stream().forEach(barSpan -> {
						final ZerodhaBarDownloader downloader = new ZerodhaBarDownloader(zerodhaBarDownloadUrl, session,
								dateFormat, zerodhaInstrument, barSpan, getStart(barSpan), end);
						try {
							final List<Bar> bars = downloader.download();
							barRepository.save(bars, instrument.getId(), barSpan);
							saveDownloadedInstrumentId(instrument.getId());
							System.out.printf("%3d\t%2s\t%s\n",bars.size(), barSpan.getShortName(), instrument.getName());
						} catch (Exception e) {
							System.out.printf("ERROR-\t%2s\t%s\t"+e.getMessage()+"\n",barSpan.getShortName(), instrument.getName());
						}
					});
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
	}

	private static ZerodhaSession getSession() throws JsonParseException, JsonMappingException, IOException {
		final ZerodhaResponse<ZerodhaSession> response = Constant.objectMapper.readValue(new File(zerodhaSessionFilePath), new TypeReference<ZerodhaResponse<ZerodhaSession>>() {});
		return response.getData();
	}

	private static List<String> getDownloadedInstrumentIds() throws IOException {
		final File file = new File(downloadedInstrumentsPath);
		if (!file.exists()) {
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

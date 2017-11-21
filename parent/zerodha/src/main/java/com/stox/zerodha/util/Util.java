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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.BinaryFileBarRepository;
import com.stox.core.util.Constant;
import com.stox.zerodha.ZerodhaInstrumentRepository;
import com.stox.zerodha.data.ZerodhaBarDownloader;
import com.stox.zerodha.model.ZerodhaExchange;
import com.stox.zerodha.model.ZerodhaInstrumentType;
import com.stox.zerodha.model.ZerodhaSession;

public class Util {

	private static final List<BarSpan> barSpans = Arrays.asList(BarSpan.H, BarSpan.M30, BarSpan.M15);
	private static final String sessionPath = Constant.PATH + "zerodha" + File.separator
			+ "com.stox.zerodha.session.json";
	private static final String downloadedInstrumentsPath = Constant.PATH + "zerodha" + File.separator
			+ "com.stox.zerodha.instruments.downloaded.csv";
	private static final String barDownloadUrl = "https://kitecharts.zerodha.com/api/chart/";
	private static final BarRepository barRepository = new BinaryFileBarRepository();
	private static final ZerodhaInstrumentRepository instrumentRepository = new ZerodhaInstrumentRepository();

	public static void main(String[] args) throws Exception {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -3);
		final Date start = calendar.getTime();
		final Date end = new Date();
		final ZerodhaSession session = getSession();
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final List<String> downloadedInstrumentTokens = getDownloadedInstrumentTokens();
		instrumentRepository.findAll().stream().filter(instrument -> {
			return ZerodhaExchange.NSE.equals(instrument.getExchange())
					&& ZerodhaInstrumentType.EQ.equals(instrument.getInstrumentType())
					&& !downloadedInstrumentTokens.contains(instrument.getInstrumentToken());
		}).forEach(instrument -> {
			barSpans.stream().forEach(barSpan -> {
				final ZerodhaBarDownloader downloader = new ZerodhaBarDownloader(barDownloadUrl, session, dateFormat,
						instrument, barSpan, start, end);
				try {
					final List<Bar> bars = downloader.download();
					//barRepository.save(bars, instrument.get, barSpan);
				} catch (Exception e) {
					System.out.println(barSpan.getName() + "\t" + instrument.getName() + "\t" + e.getMessage());
				}
			});
		});
	}

	private static ZerodhaSession getSession() throws JsonProcessingException, IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readerFor(ZerodhaSession.class).readValue(new File(sessionPath));
	}

	private static List<String> getDownloadedInstrumentTokens() throws IOException {
		final File file = new File(downloadedInstrumentsPath);
		return file.exists() ? Files.readAllLines(file.toPath()) : Collections.emptyList();
	}

	private static void appendDownloadedInstrumentToken(final String instrumentToken) throws IOException {
		Files.write(Paths.get(downloadedInstrumentsPath), (instrumentToken + Constant.LINEFEED).getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
	}

}

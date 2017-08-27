package com.stox.zerodha.data;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stox.core.client.Secured;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.data.DataProvider;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;
import com.stox.zerodha.Zerodha;
import com.stox.zerodha.ZerodhaInstrumentRepository;
import com.stox.zerodha.model.BarData;
import com.stox.zerodha.model.InstrumentNotFoundException;
import com.stox.zerodha.model.ZerodhaInstrument;
import com.stox.zerodha.model.ZerodhaResponse;
import com.stox.zerodha.model.ZerodhaSession;

@Component
public class ZerodhaDataProvider extends Zerodha implements DataProvider {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private ZerodhaInstrumentRepository zerodhaInstrumentRespository;

	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();

	@Override
	public void register(TickConsumer consumer) {
		synchronized (tickConsumerRegistry) {
			tickConsumerRegistry.register(consumer);
		}
	}

	@Override
	public void unregister(TickConsumer consumer) {
		synchronized (tickConsumerRegistry) {
			tickConsumerRegistry.unregister(consumer);
		}
	}

	@Override
	public String getCode() {
		return "zerodha";
	}

	@Override
	public String getName() {
		return "Zerodha";
	}

	/*
	 * private void loadInstruments() throws Exception { if (null == instruments ||
	 * instruments.isEmpty()) { if
	 * (DateUtil.isToday(instrumentRespository.getLastUpdatedDate(getCode()))) {
	 * instruments = instrumentRespository.getAllInstruments(getCode()); } else {
	 * try { instruments = doGetInstruments(); instrumentRespository.save(getCode(),
	 * instruments); } catch (Exception e) { instruments =
	 * instrumentRespository.getAllInstruments(getCode()); } } final Date today =
	 * new Date(); instruments = instruments.stream().filter(instrument -> { final
	 * String symbol = instrument.getSymbol(); final Date expiry =
	 * instrument.getExpiry(); if (null != expiry && expiry.before(today)) { return
	 * false; } return null != symbol && 3 < symbol.length() && '-' !=
	 * symbol.charAt(symbol.length() - 3); }).sorted(new
	 * HasName.HasNameComaparator<>()).collect(Collectors.toList()); cache =
	 * instruments.stream().collect(Collectors.toMap(Instrument::getId,
	 * Function.identity(), (p1, p2) -> p1)); } }
	 * 
	 * private List<Instrument> doGetInstruments() throws Exception { final
	 * HttpResponse response = HttpClientBuilder.create().build().execute(new
	 * HttpGet("https://api.kite.trade/instruments")); final CsvSchema csvSchema =
	 * CsvSchema.builder().setUseHeader(true).build(); final
	 * MappingIterator<ZerodhaInstrument> iterator =
	 * Constant.csvMapper.readerFor(ZerodhaInstrument.class).with(csvSchema).
	 * readValues(response.getEntity().getContent()); final List<Instrument>
	 * instruments = iterator.readAll().stream().map(zerodhaInstrument ->
	 * zerodhaInstrument.toInstrument()).collect(Collectors.toList());
	 * instruments.sort(new HasNameComaparator<>()); return instruments; }
	 */

	@Secured
	@Override
	public List<Bar> getBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to)
			throws Exception {
		final ZerodhaSession session = getSession();
		final ZerodhaInstrument zerodhaInstrument = zerodhaInstrumentRespository.findByInstrument(instrument);
		if (null != zerodhaInstrument) {
			final String url = "https://kitecharts.zerodha.com/api/chart/" + zerodhaInstrument.getInstrumentToken()
					+ "/" + stringValue(barSpan) + "?public_token=" + session.getPublicToken() + "&user_id="
					+ session.getUser().getClientId() + "&api_key=kitefront&access_token=" + session.getAccessToken()
					+ "&from=" + DATEFORMAT.format(from) + "&to=" + DATEFORMAT.format(to);
			final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("upgrade-insecure-requests", "1");
			connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/7.0.6.1021 Safari/537.36");
			final ZerodhaResponse<BarData> response = Constant.objectMapper.readValue(connection.getInputStream(), new TypeReference<ZerodhaResponse<BarData>>() {});
			return response.getData().getBars();
		}
		throw new InstrumentNotFoundException();
	}

	private String stringValue(final BarSpan barSpan) {
		switch (barSpan) {
		case M:
		case W:
		case D:
			return "day";
		case H:
			return "60minute";
		case M1:
			return "60minute";
		case M10:
			return "10minute";
		case M15:
			return "15minute";
		case M30:
			return "30minute";
		case M5:
			return "5minute";
		default:
			return null;
		}
	}

}

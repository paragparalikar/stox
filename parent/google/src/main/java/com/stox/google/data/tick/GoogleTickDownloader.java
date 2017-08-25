package com.stox.google.data.tick;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.core.util.StringUtil;
import com.stox.google.data.util.GoogleUtil;

public class GoogleTickDownloader {

	private final String url;
	private final List<Instrument> instruments;

	public GoogleTickDownloader(final String url, final List<Instrument> instruments) {
		this.url = url;
		this.instruments = instruments;
	}

	public Map<String, GoogleTick> download() {
		final Map<String, GoogleTick> result = new HashMap<>();
		final Map<Exchange, List<Instrument>> map = instruments.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(Instrument::getExchange));
		final Map<String, Instrument> codeMapping = instruments.stream().filter(Objects::nonNull).collect(Collectors.toMap(Instrument::getExchangeCode, Function.identity()));
		map.keySet().parallelStream().forEach(exchange -> {
			final String query = GoogleUtil.getGoogleExchangeCode(exchange) + ":"
					+ map.get(exchange).stream().map(Instrument::getExchangeCode).collect(Collectors.joining(","));
			try {
				final String data = StringUtil.toString(new URL(url + query).openStream());
				final GoogleTick[] ticks = Constant.objectMapper.readValue(data.substring(3),
						new TypeReference<GoogleTick[]>() {
						});
				if (null != ticks && 0 < ticks.length) {
					Arrays.stream(ticks).forEach(tick -> {
						result.put(tick.getInstrumentCode(), tick);
						tick.setInstrument(codeMapping.get(tick.getInstrumentCode()));
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return result;
	}

}

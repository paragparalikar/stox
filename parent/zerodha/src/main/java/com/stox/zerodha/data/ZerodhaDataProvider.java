package com.stox.zerodha.data;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stox.core.intf.HasLogin;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentNotFoundException;
import com.stox.core.util.Constant;
import com.stox.data.DataProvider;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;
import com.stox.zerodha.ZerodhaInstrumentRepository;
import com.stox.zerodha.ZerodhaSessionManager;
import com.stox.zerodha.model.BarData;
import com.stox.zerodha.model.ZerodhaInstrument;
import com.stox.zerodha.model.ZerodhaResponse;
import com.stox.zerodha.model.ZerodhaSession;
import com.stox.zerodha.util.ZerodhaUtil;

import lombok.experimental.Delegate;

@Component
@PropertySource("classpath:zerodha.properties")
public class ZerodhaDataProvider implements DataProvider {
	
	@Value("${com.stox.zerodha.code}")
	private String code;
	
	@Value("${com.stox.zerodha.name}")
	private String name;
	
	@Value("${com.stox.zerodha.dateformat.bar.criteria}")
	private DateFormat dateFormat;
	
	@Autowired
	@Delegate(types=HasLogin.class)
	private ZerodhaSessionManager zerodhaSessionManager;

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
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Bar> getBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to)
			throws Exception {
		final ZerodhaSession session = zerodhaSessionManager.getSession();
		final ZerodhaInstrument zerodhaInstrument = zerodhaInstrumentRespository.findByInstrument(instrument);
		if (null != zerodhaInstrument) {
			final String url = "https://kitecharts.zerodha.com/api/chart/" + zerodhaInstrument.getInstrumentToken()
					+ "/" + ZerodhaUtil.stringValue(barSpan) + "?public_token=" + session.getPublicToken() + "&user_id="
					+ session.getUser().getClientId() + "&api_key=kitefront&access_token=" + session.getAccessToken()
					+ "&from=" + dateFormat.format(from) + "&to=" + dateFormat.format(to);
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("upgrade-insecure-requests", "1");
			connection.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/7.0.6.1021 Safari/537.36");
			final ZerodhaResponse<BarData> response = Constant.objectMapper.readValue(connection.getInputStream(),
					new TypeReference<ZerodhaResponse<BarData>>() {
					});
			return trim(response.getData().getBars(), from, to);
		}
		throw new InstrumentNotFoundException();
	}

	private List<Bar> trim(final List<Bar> bars, final Date from, final Date to) {
		return bars.stream()
				.filter(bar -> (bar.getDate().after(from) || bar.getDate().equals(from))
						&& (bar.getDate().before(to) || bar.getDate().equals(to)))
				.sorted().collect(Collectors.toList());
	}

}

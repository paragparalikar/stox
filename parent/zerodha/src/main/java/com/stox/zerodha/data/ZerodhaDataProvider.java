package com.stox.zerodha.data;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasLogin;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentNotFoundException;
import com.stox.data.DataProvider;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;
import com.stox.zerodha.ZerodhaInstrumentRepository;
import com.stox.zerodha.ZerodhaSessionManager;
import com.stox.zerodha.model.ZerodhaInstrument;
import com.stox.zerodha.model.ZerodhaSession;

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

	@Value("${com.stox.zerodha.url.download.bar}")
	private String barDownloadUrl;

	@Autowired
	@Delegate(types = HasLogin.class)
	private ZerodhaSessionManager zerodhaSessionManager;

	@Autowired
	private ZerodhaInstrumentRepository zerodhaInstrumentRespository;

	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();

	@Override
	public boolean isLocal() {
		return false;
	}
	
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
			final ZerodhaBarDownloader zerodhaBarDownloader = new ZerodhaBarDownloader(barDownloadUrl, session,
					dateFormat, zerodhaInstrument, barSpan, from, to);
			final List<Bar> bars = zerodhaBarDownloader.download();
			return bars;
		}
		throw new InstrumentNotFoundException();
	}

}

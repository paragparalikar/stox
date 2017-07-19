package com.stox.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.client.AbstractClient;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.DateUtil;

@Async
@Component
public class CachingDataClient extends AbstractClient implements DataClient {

	private final Map<String, List<Instrument>> cache = new HashMap<String, List<Instrument>>();

	@Autowired
	private InstrumentRepository instrumentRespository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private DataProviderManager dataProviderManager;

	@Override
	public void getAllInstruments(ResponseCallback<List<Instrument>> callback) {
		dataProviderManager.execute(dataProvider -> {
			execute(callback, () -> {
				final String dataProviderCode = dataProvider.getCode();
				List<Instrument> instruments = cache.get(dataProviderCode);
				if (null == instruments || instruments.isEmpty()) {
					if (DateUtil.isToday(instrumentRespository.getLastUpdatedDate(dataProviderCode))) {
						instruments = instrumentRespository.getAllInstruments(dataProviderCode);
					} else {
						instruments = dataProvider.getInstruments();
						instrumentRespository.save(dataProviderCode, instruments);
					}
					cache.put(dataProviderCode, instruments);
				}
				return new Response<>(instruments);
			});
			return null;
		});
	}

	@Override
	public void getInstrument(String code, ResponseCallback<Instrument> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadBars(Instrument instrument, BarSpan barSpan, Date from, Date to, ResponseCallback<List<Bar>> callback) {
		// TODO Auto-generated method stub

	}

}

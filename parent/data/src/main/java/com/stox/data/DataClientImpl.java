package com.stox.data;

import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.data.event.DataProviderChangedEvent;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;

@Async
@Component
public class DataClientImpl implements DataClient {

	@Autowired
	private DataProviderManager dataProviderManager;

	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();

	@Override
	public void register(TickConsumer consumer) {
		tickConsumerRegistry.register(consumer);
		dataProviderManager.execute(dataProvider -> {
			dataProvider.register(consumer);
		});
	}

	@Override
	public void unregister(TickConsumer consumer) {
		tickConsumerRegistry.unregister(consumer);
		dataProviderManager.execute(dataProvider -> {
			dataProvider.unregister(consumer);
		});
	}

	@EventListener
	public void onDataProviderChanged(final DataProviderChangedEvent event) {
		final DataProvider oldDataProvider = event.getOldDataProvider();
		if (null != oldDataProvider) {
			tickConsumerRegistry.getTickConsumers().forEach(consumer -> oldDataProvider.unregister(consumer));
		}
		final DataProvider dataProvider = event.getDataProvider();
		if (null != dataProvider) {
			tickConsumerRegistry.getTickConsumers().forEach(consumer -> dataProvider.register(consumer));
		}
	}

	@PreDestroy
	public void preDestroy() {
		final DataProvider dataProvider = dataProviderManager.getSelectedDataProvider();
		if (null != dataProvider) {
			tickConsumerRegistry.getTickConsumers().forEach(consumer -> dataProvider.unregister(consumer));
		}
	}

	@Async
	@Override
	public void loadBars(Instrument instrument, BarSpan barSpan, Date from, Date to,
			ResponseCallback<List<Bar>> callback) {
		dataProviderManager.execute(dataProvider -> {
			try {
				callback.onSuccess(new Response<>(dataProvider.getBars(instrument, barSpan, from, to)));
			} catch (Exception e) {
				callback.onFailure(null, e);
			} finally {
				callback.onDone();
			}
		});
	}

}

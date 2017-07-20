package com.stox.data;

import java.util.List;

import javafx.scene.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.model.Instrument;
import com.stox.data.event.DataProviderChangedEvent;
import com.stox.data.event.InstrumentFilterChangedEvent;

@Component
public class InstrumentFilterProvider {

	private DataProvider dataProvider;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@EventListener(DataProviderChangedEvent.class)
	public void onDataProviderChanged(final DataProviderChangedEvent event) {
		dataProvider = event.getDataProvider();
		eventPublisher.publishEvent(new InstrumentFilterChangedEvent(this));
	}

	public Node getInstrumentFilterView(final List<Instrument> target) {
		return null == dataProvider ? null : dataProvider.getInstrumentFilterView(target);
	}

}

package com.stox.data.event;

import org.springframework.context.ApplicationEvent;

import com.stox.data.DataProvider;

public class DataProviderChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final DataProvider dataProvider;

	public DataProviderChangedEvent(final Object source, final DataProvider dataProvider) {
		super(source);
		this.dataProvider = dataProvider;
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}

}

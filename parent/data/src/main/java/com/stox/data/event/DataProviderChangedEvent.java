package com.stox.data.event;

import org.springframework.context.ApplicationEvent;

import com.stox.data.DataProvider;

public class DataProviderChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final DataProvider dataProvider;
	private final DataProvider oldDataProvider;

	public DataProviderChangedEvent(final Object source, final DataProvider oldDataProvider, final DataProvider dataProvider) {
		super(source);
		this.dataProvider = dataProvider;
		this.oldDataProvider = oldDataProvider;
	}

	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public DataProvider getOldDataProvider() {
		return oldDataProvider;
	}

}

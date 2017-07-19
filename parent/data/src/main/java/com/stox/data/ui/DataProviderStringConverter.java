package com.stox.data.ui;

import javafx.util.StringConverter;

import com.stox.data.DataProvider;

public class DataProviderStringConverter extends StringConverter<DataProvider> {

	@Override
	public String toString(final DataProvider dataProvider) {
		return dataProvider.getName();
	}

	@Override
	public DataProvider fromString(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}

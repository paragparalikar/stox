package com.stox.core.batch;

public interface CsvRowCallback {

	void row(final String[] tokens);

}

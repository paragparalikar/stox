package com.stox.core.batch;

public interface CsvRowMapper<T> {

	T map(final String[] tokens);

}

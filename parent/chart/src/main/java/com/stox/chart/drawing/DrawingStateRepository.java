package com.stox.chart.drawing;

import java.util.List;

import com.stox.core.model.Instrument;

public interface DrawingStateRepository {

	List<Drawing.State<?>> load(final Instrument instrument);
	
	void save(final Instrument instrument, final List<Drawing.State<?>> states);
	
}

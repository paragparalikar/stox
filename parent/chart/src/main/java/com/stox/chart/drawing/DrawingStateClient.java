package com.stox.chart.drawing;

import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Instrument;

public interface DrawingStateClient {
	
	void load(final Instrument instrument, final ResponseCallback<List<Drawing.State<?>>> callback);
	
	void save(final Instrument instrument, final List<Drawing.State<?>> states, final ResponseCallback<Void> callback);

}

package com.stox.data;

import java.util.Date;
import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.tick.TickConsumer;

public interface DataClient {
	
	void register(final TickConsumer consumer);
	
	void unregister(final TickConsumer consumer);

	void loadBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to, final ResponseCallback<List<Bar>> callback);

}

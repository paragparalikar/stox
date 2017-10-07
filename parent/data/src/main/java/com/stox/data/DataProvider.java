package com.stox.data;

import java.util.Date;
import java.util.List;

import com.stox.core.intf.HasLogin;
import com.stox.core.intf.HasName;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.tick.TickConsumer;

public interface DataProvider extends HasLogin, HasName {

	String getCode();
	
	boolean isLocal();

	void register(final TickConsumer consumer);

	void unregister(final TickConsumer consumer);

	List<Bar> getBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to)
			throws Exception;

}

package com.stox.data;

import java.util.Date;
import java.util.List;

import com.stox.core.intf.DelayedResponseCallback;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;

public interface DataClient {

	void loadBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to, final ResponseCallback<List<Bar>> callback);

	void loadBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to, final DelayedResponseCallback<List<Bar>> callback);

}

package com.stox.data;

import java.util.Date;
import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;

public interface DataClient {

	void getAllInstruments(final ResponseCallback<List<Instrument>> callback);

	void getInstrument(final String code, final ResponseCallback<Instrument> callback);

	void loadBars(final String instrumentCode, final BarSpan barSpan, final Date from, final Date to, final ResponseCallback<List<Bar>> callback);

}

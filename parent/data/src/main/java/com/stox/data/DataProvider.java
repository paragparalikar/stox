package com.stox.data;

import java.util.Date;
import java.util.List;

import com.stox.core.client.HasLogin;
import com.stox.core.intf.HasName;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.ui.FilterPresenter;

public interface DataProvider extends HasLogin, HasName {

	String getCode();

	FilterPresenter getFilterPresenter(final List<Instrument> target);

	Instrument getInstrument(final String code) throws Exception;

	List<Instrument> getInstruments() throws Exception;

	List<Bar> getBars(final String exchangeCode, final BarSpan barSpan, final Date from, final Date to) throws Exception;

}

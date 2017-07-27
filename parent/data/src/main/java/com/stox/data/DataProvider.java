package com.stox.data;

import java.util.Date;
import java.util.List;

import com.stox.core.client.HasLogin;
import com.stox.core.intf.HasName;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;

public interface DataProvider extends HasLogin, HasName {

	String getCode();

	List<Bar> getBars(final String exchangeCode, final BarSpan barSpan, final Date from, final Date to) throws Exception;

}

package com.stox.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.tick.TickConsumer;

@Component
public class DefaultDataProvider implements DataProvider {
	
	@Override
	public void login(Runnable runnable) throws Throwable {
		runnable.run();
	}

	@Override
	public boolean isLoggedIn() {
		return true;
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public String getCode() {
		return "DEFAULT";
	}

	@Override
	public void register(TickConsumer consumer) {

	}

	@Override
	public void unregister(TickConsumer consumer) {

	}

	@Override
	public List<Bar> getBars(Instrument instrument, BarSpan barSpan, Date from, Date to) throws Exception {
		return Collections.emptyList();
	}

}

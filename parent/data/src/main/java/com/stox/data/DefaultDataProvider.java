package com.stox.data;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.repository.BarRepository;
import com.stox.data.tick.TickConsumer;

@Component
public class DefaultDataProvider implements DataProvider {
	
	@Autowired
	private BarRepository barRepository;
	
	@Override
	public void login(Runnable runnable) throws Throwable {
		runnable.run();
	}
	
	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean isLoggedIn() {
		return true;
	}

	@Override
	public String getName() {
		return "None";
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
		return barRepository.find(instrument.getId(), barSpan, from, to);
	}

}

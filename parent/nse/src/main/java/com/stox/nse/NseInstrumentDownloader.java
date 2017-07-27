package com.stox.nse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.core.model.Instrument;
import com.stox.core.repository.InstrumentRepository;

@Component
public class NseInstrumentDownloader {

	@Autowired
	private InstrumentRepository intrumentRepository;

	@Async
	public void download(final Callback<List<Instrument>, Void> callback) {

	}

}

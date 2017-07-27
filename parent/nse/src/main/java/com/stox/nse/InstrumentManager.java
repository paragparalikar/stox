package com.stox.nse;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.nse.batch.instrument.InstrumentBatchJobManager;

@Component
@Lazy(false)
public class InstrumentManager {

	@Autowired
	private InstrumentBatchJobManager instrumentBatchJobManager;

	@PostConstruct
	public void postContruct() {
		instrumentBatchJobManager.executeInstrumentDownloadJob();
	}
}

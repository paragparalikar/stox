package com.stox.nse;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.repository.InstrumentRepository;

@Component
@Lazy(false)
public class InstrumentManager {

	@Autowired
	private InstrumentRepository intrumentRepository;

	@Async
	@PostConstruct
	public void postContruct() {
		System.out.println("async post construct :" + Thread.currentThread().getName());
	}

}

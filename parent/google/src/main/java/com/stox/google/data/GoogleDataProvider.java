package com.stox.google.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.DataProvider;
import com.stox.google.data.bar.GoogleBarDownloader;

@Component
public class GoogleDataProvider implements DataProvider {

	@Autowired
	private Environment environment;

	@Override
	public void login(Callback<Void, Void> callback) throws Throwable {
		callback.call(null);
	}

	@Override
	public boolean isLoggedIn() {
		return true;
	}

	@Override
	public String getName() {
		return "Google Finance";
	}

	@Override
	public String getCode() {
		return "google";
	}

	@Override
	public List<Bar> getBars(Instrument instrument, BarSpan barSpan, Date from, Date to) throws Exception {
		final GoogleBarDownloader downloader = new GoogleBarDownloader(environment.getProperty("com.stox.google.data.download.url.bar"), from, barSpan, instrument);
		final List<Bar> bars = downloader.download();
		Collections.sort(bars);
		return bars;
	}

}

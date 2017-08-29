package com.stox.zerodha.data;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.util.Constant;
import com.stox.core.util.StringUtil;
import com.stox.zerodha.model.BarData;
import com.stox.zerodha.model.ZerodhaInstrument;
import com.stox.zerodha.model.ZerodhaResponse;
import com.stox.zerodha.model.ZerodhaSession;
import com.stox.zerodha.util.ZerodhaUtil;

import lombok.NonNull;
import lombok.Value;

@Value
public class ZerodhaBarDownloader {

	@NonNull
	private final String url;
	@NonNull
	private final ZerodhaSession session;
	@NonNull
	private final DateFormat dateFormat;
	@NonNull
	private final ZerodhaInstrument zerodhaInstrument;
	@NonNull
	private final BarSpan barSpan;
	@NonNull
	private final Date from;
	@NonNull
	private final Date to;

	protected String getUrl() {
		return url + zerodhaInstrument.getInstrumentToken() + "/" + ZerodhaUtil.stringValue(barSpan) + "?public_token="
				+ session.getPublicToken() + "&user_id=" + session.getUser().getClientId()
				+ "&api_key=kitefront&access_token=" + session.getAccessToken() + "&from=" + dateFormat.format(from)
				+ "&to=" + dateFormat.format(to);
	}

	protected InputStream getInputStream(@NonNull final String url) throws Exception {
		final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("upgrade-insecure-requests", "1");
		connection.setRequestProperty("user-agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/7.0.6.1021 Safari/537.36");
		return connection.getInputStream();
	}

	protected ZerodhaResponse<BarData> parse(@NonNull final InputStream inputStream) throws Exception {
		return Constant.objectMapper.readValue(inputStream, new TypeReference<ZerodhaResponse<BarData>>() {
		});
	}

	public List<Bar> download() throws Exception {
		final String url = getUrl();
		if (StringUtil.hasText(url)) {
			final InputStream inputStream = getInputStream(url);
			if (null != inputStream) {
				final ZerodhaResponse<BarData> response = parse(inputStream);
				if (null != response && null != response.getData()) {
					final List<Bar> bars = response.getData().getBars();
					if (null != bars && !bars.isEmpty()) {
						return bars;
					}
				}
			}
		}
		return new ArrayList<>();
	}

}

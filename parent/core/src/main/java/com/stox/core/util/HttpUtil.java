package com.stox.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	private static final int TIMEOUT = 30000;

	private HttpUtil() {
	}

	public static HttpURLConnection getConnection(final String url) throws MalformedURLException, IOException {
		final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setConnectTimeout(TIMEOUT);
		connection.setReadTimeout(TIMEOUT);
		connection.setInstanceFollowRedirects(false);
		connection.setAllowUserInteraction(false);
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		return connection;
	}

	public static HttpURLConnection getConnection(final String url, final String method, final String contentType, final String host, final String accept, final String referer,
			final String enchoding, final String language, final String origin) throws MalformedURLException, IOException {
		final HttpURLConnection connection = getConnection(url);
		if (StringUtil.hasText(host))
			connection.setRequestProperty("Host", host);
		if (StringUtil.hasText(method))
			connection.setRequestMethod(method);
		if (StringUtil.hasText(accept))
			connection.setRequestProperty("Accept", accept);
		if (StringUtil.hasText(referer))
			connection.setRequestProperty("Referer", referer);
		if (StringUtil.hasText(enchoding))
			connection.setRequestProperty("Accept-Encoding", enchoding);
		if (StringUtil.hasText(language))
			connection.setRequestProperty("Accept-Language", language);
		if (StringUtil.hasText(contentType))
			connection.setRequestProperty("Content-Type", contentType);
		if (StringUtil.hasText(origin))
			connection.setRequestProperty("Origin", origin);
		return connection;
	}

	public static byte[] body(final String... params) throws UnsupportedEncodingException {
		if (0 != params.length % 2) {
			throw new IllegalArgumentException("Odd number of strings for parameter key value pair");
		}
		final Map<String, String> map = new HashMap<String, String>();
		for (int index = 0; index < params.length; index += 2) {
			map.put(params[index], params[index + 1]);
		}
		return body(map);
	}

	public static byte[] body(final Map<String, String> map) throws UnsupportedEncodingException {
		final StringBuilder stringBuilder = new StringBuilder();
		for (Map.Entry<String, String> param : map.entrySet()) {
			if (stringBuilder.length() != 0)
				stringBuilder.append('&');
			stringBuilder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			stringBuilder.append('=');
			stringBuilder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		return stringBuilder.toString().getBytes("UTF-8");
	}

}

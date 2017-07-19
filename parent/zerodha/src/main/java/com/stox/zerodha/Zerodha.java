package com.stox.zerodha;

import java.net.CookieManager;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import com.stox.core.intf.Callback;

public class Zerodha {

	private static Map<String, List<String>> cookies;

	public Map<String, List<String>> getCookies() {
		return cookies;
	}

	public WebView createLoginView(final Callback<Void, Void> callback) {
		final WebView webView = new WebView();
		final WebEngine engine = webView.getEngine();
		engine.locationProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String location) {
				if (location.contains("dashboard")) {
					try {
						cookies = CookieManager.getDefault().get(URI.create(location), new HashMap<>());
						callback.call(null);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});
		engine.load("https://kite.zerodha.com");
		return webView;
	}

}

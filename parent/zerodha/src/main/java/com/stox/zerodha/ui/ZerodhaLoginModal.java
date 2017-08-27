package com.stox.zerodha.ui;

import java.net.CookieManager;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stox.core.ui.widget.modal.Modal;
import com.stox.core.util.Constant;
import com.stox.zerodha.model.ZerodhaResponse;
import com.stox.zerodha.model.ZerodhaSession;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ZerodhaLoginModal extends Modal {
	
	private final Consumer<ZerodhaSession> consumer;
	
	public ZerodhaLoginModal(final Consumer<ZerodhaSession> consumer) {
		this.consumer = consumer;
		addStylesheets("styles/zerodha.css");
		getStyleClass().add("zerodha-login-modal");
		getStyleClass().add("primary");
		setTitle("Login to Zerodha"); // TODO I18N here
		setContent(createLoginView());
	}
	
	public WebView createLoginView() {
		final WebView webView = new WebView();
		final WebEngine engine = webView.getEngine();
		engine.locationProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String location) {
				if (location.contains("dashboard")) {
					hide();
					try {
						final String url = "https://kite.zerodha.com/api/session";
						final Map<String, List<String>> cookies = CookieManager.getDefault().get(URI.create(location), new HashMap<>());
						final ZerodhaResponse<ZerodhaSession> response = Constant.objectMapper.readValue(new URL(url), new TypeReference<ZerodhaResponse<ZerodhaSession>>() {});
						response.getData().getCookies().putAll(cookies);
						consumer.accept(response.getData());
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

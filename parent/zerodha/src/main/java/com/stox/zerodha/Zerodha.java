package com.stox.zerodha;

import com.stox.core.client.HasLogin;
import com.stox.core.intf.Callback;
import com.stox.zerodha.model.ZerodhaSession;
import com.stox.zerodha.ui.ZerodhaLoginModal;

import javafx.application.Platform;

public class Zerodha implements HasLogin {

	private static ZerodhaSession session;

	@Override
	public void login(final Callback<Void, Object> callback) throws Throwable {
		if (Platform.isFxApplicationThread()) {
			doLogin(callback);
		} else {
			Platform.runLater(() -> {
				try {
					doLogin(callback);
				} catch (Throwable e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			});
		}
	}

	private void doLogin(final Callback<Void, Object> callback) throws Throwable {
		final ZerodhaLoginModal zerodhaLoginModel = new ZerodhaLoginModal(session ->  {
			Zerodha.session = session;
			callback.call(null);
		});
		zerodhaLoginModel.show();
	}

	@Override
	public boolean isLoggedIn() {
		return null != session;
	}
	
	protected ZerodhaSession getSession() {
		return Zerodha.session;
	}

}

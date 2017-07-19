package com.stox.core.client;

import com.stox.core.intf.Callback;

public interface HasLogin {

	void login(final Callback<Void, Void> callback) throws Throwable;

	boolean isLoggedIn();

}

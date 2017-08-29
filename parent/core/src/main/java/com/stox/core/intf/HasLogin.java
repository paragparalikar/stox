package com.stox.core.intf;

public interface HasLogin {

	void login(final Runnable runnable) throws Throwable;

	boolean isLoggedIn();

}

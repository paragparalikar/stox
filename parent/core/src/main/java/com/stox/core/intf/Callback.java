package com.stox.core.intf;

public interface Callback<T, R> {

	R call(T payload);

}

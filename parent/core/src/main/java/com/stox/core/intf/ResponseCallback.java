package com.stox.core.intf;

import com.stox.core.model.Response;

@FunctionalInterface
public interface ResponseCallback<T> {

	void onSuccess(final Response<T> response);

	default void onFailure(final Response<T> response, final Throwable throwable) {

	}

	default void onDone() {

	}

}

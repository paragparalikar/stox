package com.stox.core.intf;

import com.stox.core.model.Response;

public interface ResponseCallback<T> {

	void onSuccess(final Response<T> response);

	void onFailure(final Response<T> response, final Throwable throwable);

	default void onDone() {

	}

}

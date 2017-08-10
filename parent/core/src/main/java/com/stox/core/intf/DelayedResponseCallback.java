package com.stox.core.intf;

import com.stox.core.model.Response;

@FunctionalInterface
public interface DelayedResponseCallback<T> extends ResponseCallback<T> {

	default void onDelayedSuccess(final Response<T> response) {

	}

}

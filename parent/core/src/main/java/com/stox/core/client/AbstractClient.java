package com.stox.core.client;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;

public abstract class AbstractClient {

	@SuppressWarnings("unchecked")
	public <T> void execute(final ResponseCallback<T> responseCallback, final ClientCallback clientCallback) {
		boolean called = false;
		try {
			final Response<?> response = clientCallback.call();
			if (null == response || response.isSuccess()) {
				called = true;
				responseCallback.onSuccess((Response<T>) response);
			} else {
				called = true;
				responseCallback.onFailure((Response<T>) response, null);
			}
		} catch (final Throwable throwable) {
			if (!called) {
				responseCallback.onFailure(null, throwable);
			}
		} finally {
			responseCallback.onDone();
		}
	}

}

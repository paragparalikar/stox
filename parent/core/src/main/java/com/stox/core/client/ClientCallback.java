package com.stox.core.client;

import com.stox.core.model.Response;

public interface ClientCallback {

	Response<?> call() throws Throwable;

}

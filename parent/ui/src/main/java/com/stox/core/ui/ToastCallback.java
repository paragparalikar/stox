package com.stox.core.ui;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.stox.core.intf.Callback;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.model.Response;

public class ToastCallback<T, R> implements ResponseCallback<T> {
	private static final Log log = LogFactory.getLog(ToastCallback.class);

	private final Callback<T, R> callback;

	public ToastCallback(final Callback<T, R> callback) {
		this.callback = callback;
	}

	@Override
	public void onSuccess(Response<T> response) {
		callback.call(response.getPayload());
	}

	@Override
	public void onFailure(final Response<T> response, Throwable throwable) {
		if (null == response && null != throwable) {
			toast(throwable.getMessage(), MessageType.ERROR);
			throwable.printStackTrace();
		} else if (null != response && null == throwable) {
			toast(response.getMessages(), MessageType.ERROR);
		} else {
			// TODO I18N here
			toast("something went wrong", MessageType.ERROR);
		}
	}

	protected void toast(final List<Message> messages, final MessageType messageType) {
		if (null != messages) {
			messages.forEach(message -> toast(message.getText(), messageType));
		}
	}

	protected void toast(final String message, final MessageType messageType) {
		log.error(message);
	}

}

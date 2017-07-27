package com.stox.workbench.client;

import java.io.File;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.core.util.Constant;
import com.stox.workbench.model.WorkbenchState;

@Component
public class FileWorkbenchClient implements WorkbenchClient {

	private String getPath() {
		return Constant.PATH + "com.stox.workbench.state.json";
	}

	@Override
	public void save(WorkbenchState workbenchState, ResponseCallback<Void> callback) {
		try {
			Constant.objectMapper.writeValue(new File(getPath()), workbenchState);
			callback.onSuccess(new Response<Void>());
		} catch (final Exception e) {
			callback.onFailure(null, e);
		} finally {
			callback.onDone();
		}
	}

	@Async
	@Override
	public void load(ResponseCallback<WorkbenchState> callback) {
		try {
			final File file = new File(getPath());
			final WorkbenchState workbenchState = Constant.objectMapper.readValue(file, WorkbenchState.class);
			callback.onSuccess(new Response<WorkbenchState>(workbenchState));
		} catch (final Exception e) {
			callback.onFailure(null, e);
		} finally {
			callback.onDone();
		}
	}
}

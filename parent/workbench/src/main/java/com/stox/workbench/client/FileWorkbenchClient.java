package com.stox.workbench.client;

import java.io.File;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.client.AbstractClient;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.core.util.Constant;
import com.stox.workbench.model.WorkbenchState;

@Async
@Component
public class FileWorkbenchClient extends AbstractClient implements WorkbenchClient {

	private String getPath() {
		return Constant.PATH + "com.stox.workbench.state.json";
	}

	@Override
	public void save(WorkbenchState workbenchState, ResponseCallback<Void> callback) {
		execute(callback, () -> {
			Constant.objectMapper.writeValue(new File(getPath()), workbenchState);
			return new Response<Void>();
		});
	}

	@Override
	public void load(ResponseCallback<WorkbenchState> callback) {
		execute(callback, () -> {
			final File file = new File(getPath());
			return new Response<WorkbenchState>(Constant.objectMapper.readValue(file, WorkbenchState.class));
		});
	}
}

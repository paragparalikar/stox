package com.stox.workbench.client;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.core.util.Constant;
import com.stox.core.util.FileUtil;
import com.stox.core.util.StringUtil;
import com.stox.workbench.model.WorkbenchState;

@Component
public class FileWorkbenchClient implements WorkbenchClient {

	private String getPath() {
		return Constant.PATH + "workbench" + File.separator + "com.stox.workbench.state.json";
	}

	@Override
	public void save(WorkbenchState workbenchState, ResponseCallback<Void> callback) {
		try {
			Constant.objectMapper.writeValue(FileUtil.getFile(getPath()), workbenchState);
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
			if(file.exists()) {
				final String content = StringUtil.toString(Files.newInputStream(file.toPath(), StandardOpenOption.READ));
				if(StringUtil.hasText(content)) {
					final WorkbenchState workbenchState = Constant.objectMapper.readValue(FileUtil.getFile(getPath()),
							WorkbenchState.class);
					callback.onSuccess(new Response<WorkbenchState>(workbenchState));
				}
			}
		} catch (final Exception e) {
			callback.onFailure(null, e);
		} finally {
			callback.onDone();
		}
	}
}

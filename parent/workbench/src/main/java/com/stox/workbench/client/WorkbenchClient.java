package com.stox.workbench.client;

import com.stox.core.intf.ResponseCallback;
import com.stox.workbench.model.WorkbenchState;

public interface WorkbenchClient {

	void save(final WorkbenchState workbenchState, final ResponseCallback<Void> callback);

	void load(final ResponseCallback<WorkbenchState> callback);

}

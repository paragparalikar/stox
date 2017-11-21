package com.stox.data;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class DataProviderManager {

	@Autowired
	private OfflineDataProvider offlineDataProvider;
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	public DataProvider getSelectedDataProvider() {
		return offlineDataProvider;
	}

	public void execute(final Consumer<DataProvider> callback) {
		taskExecutor.execute(() -> callback.accept(offlineDataProvider));
	}
	

}

package com.stox.example.client;

import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.example.model.ExampleGroup;

public interface ExampleGroupClient {
	
	void loadAll(final ResponseCallback<List<ExampleGroup>> callback);
	
	void save(final ExampleGroup exampleGroup, final ResponseCallback<ExampleGroup> callback);
	
	void delete(final Integer exampleGroupId, final ResponseCallback<ExampleGroup> callback);

}

package com.stox.example.client;

import java.util.List;

import com.stox.core.intf.ResponseCallback;
import com.stox.example.model.Example;

public interface ExampleClient {

	void load(final Integer exampleGroupId, final ResponseCallback<List<Example>> callback);
	
	void save(final Example example, final ResponseCallback<Example> callback);
	
	void delete(Integer exampleGroupId, String exampleId, ResponseCallback<Example> callback);
}

package com.stox.example.repository;

import java.util.List;

import com.stox.example.model.Example;

public interface ExampleRepository {
	
	List<Example> load(final Integer exampleGroupId) throws Exception;
	
	Example save(final Example example) throws Exception;

	Example delete(Integer exampleGroupId, String exampleId) throws Exception;

}

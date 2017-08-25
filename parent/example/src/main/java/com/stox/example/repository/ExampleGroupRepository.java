package com.stox.example.repository;

import java.util.List;

import com.stox.example.model.ExampleGroup;

public interface ExampleGroupRepository {
	
	List<ExampleGroup> loadAll() throws Exception;
	
	ExampleGroup save(final ExampleGroup exampleGroup) throws Exception;
	
	ExampleGroup delete(final Integer exampleGroupId) throws Exception;

}

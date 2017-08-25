package com.stox.example.repository;

import com.stox.example.util.ExampleConstant;

public class ExampleRepositoryUtil {
	
	public static final String getExampleGroupFilePath(final Integer exampleGroupId) {
		return ExampleConstant.PATH + String.valueOf(exampleGroupId) + ".csv";
	}

}

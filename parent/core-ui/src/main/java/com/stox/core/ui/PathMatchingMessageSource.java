package com.stox.core.ui;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class PathMatchingMessageSource extends AbstractMessageSource {

	private Map<String, Map<String, MessageFormat>> cache = new HashMap<>();
	private final StringProperty languageProperty = new SimpleStringProperty("");
	private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	public PathMatchingMessageSource() {
		languageProperty.addListener((observable, old, language) -> {
			try {
				final Resource[] resources = resolver.getResources("classpath*:messages/" + language + ".prperties");
				Arrays.asList(resources).stream().filter(resource -> resource.exists()).map(resource -> {
					try {
						resource.getFile();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {

		return null;
	}

}

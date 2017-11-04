package com.stox.example.ui;

import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.ui.StylesheetProvider;
import com.stox.example.event.ExampleGroupCreatedEvent;
import com.stox.example.event.ExampleGroupDeletedEvent;
import com.stox.example.event.ExampleGroupEditedEvent;
import com.stox.example.event.ExampleCreatedEvent;
import com.stox.example.event.ExampleDeletedEvent;
import com.stox.workbench.ui.view.AbstractDockablePublishingPresenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class ExamplePresenterProvider implements PresenterProvider, StylesheetProvider {
	
	@Autowired
	private BeanFactory beanFactory;
	
	private final Map<ExamplePresenter,ExamplePresenter> cache = new WeakHashMap<>();
	
	@Override
	public String[] getStylesheets() {
		return new String[]{"styles/example.css"};
	}

	@Override
	public String getViewCode() {
		return ExampleUiConstant.CODE;
	}

	@Override
	public String getViewName() {
		return ExampleUiConstant.NAME;
	}

	@Override
	public String getViewIcon() {
		return ExampleUiConstant.ICON;
	}

	@Override
	public AbstractDockablePublishingPresenter<?, ?> create() {
		final ExamplePresenter examplePresenter = beanFactory.getBean(ExamplePresenter.class);
		cache.put(examplePresenter, examplePresenter);
		return examplePresenter;
	}
	
	@EventListener
	public void onExampleGroupCreated(final ExampleGroupCreatedEvent event) {
		cache.keySet().forEach(examplePresenter -> examplePresenter.onExampleGroupCreated(event));
	}
	
	@EventListener
	public void onExampleGroupDeleted(final ExampleGroupDeletedEvent event) {
		cache.keySet().forEach(examplePresenter -> examplePresenter.onExampleGroupDeleted(event));
	}
	
	@EventListener
	public void onExampleGroupEdited(final ExampleGroupEditedEvent event) {
		cache.keySet().forEach(examplePresenter -> examplePresenter.onExampleGroupEdited(event));
	}

	@EventListener
	public void onExampleCreated(final ExampleCreatedEvent event) {
		cache.keySet().forEach(examplePresenter -> examplePresenter.onExampleCreated(event));
	}
	
	@EventListener
	public void onExampleDeleted(final ExampleDeletedEvent event) {
		cache.keySet().forEach(examplePresenter -> examplePresenter.onExampleDeleted(event));
	}
}

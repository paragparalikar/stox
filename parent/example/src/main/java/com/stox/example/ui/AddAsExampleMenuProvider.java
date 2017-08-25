package com.stox.example.ui;

import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasInstrument;
import com.stox.core.ui.TargetAwareMenuItemProvider;
import com.stox.example.event.ExampleGroupCreatedEvent;
import com.stox.example.event.ExampleGroupDeletedEvent;
import com.stox.example.event.ExampleGroupEditedEvent;

import javafx.scene.control.MenuItem;

@Lazy
@Component
public class AddAsExampleMenuProvider implements TargetAwareMenuItemProvider {
	
	@Autowired
	private BeanFactory beanFactory; 
	
	private final Map<AddAsExampleMenu<?>, AddAsExampleMenu<?>> cache = new WeakHashMap<>();

	@Override
	public boolean supports(Object target) {
		return target instanceof HasInstrument && target instanceof HasBarSpan;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  MenuItem build(Object target) {
		if(!supports(target)) {
			throw new IllegalArgumentException("Target is not supported");
		}
		final AddAsExampleMenu menu = beanFactory.getBean(AddAsExampleMenu.class);
		menu.setExampleProvider((HasInstrument) target);
		cache.put(menu, menu);
		return menu;
	}
	
	@EventListener
	public void onExampleGroupCreated(final ExampleGroupCreatedEvent event) {
		cache.keySet().forEach(menu -> menu.onExampleGroupCreated(event));
	}
	
	@EventListener
	public void onExampleGroupDeleted(final ExampleGroupDeletedEvent event) {
		cache.keySet().forEach(menu -> menu.onExampleGroupDeleted(event));
	}
	
	@EventListener
	public void onExampleGroupEdited(final ExampleGroupEditedEvent event) {
		cache.keySet().forEach(menu -> menu.onExampleGroupEdited(event));
	}

}

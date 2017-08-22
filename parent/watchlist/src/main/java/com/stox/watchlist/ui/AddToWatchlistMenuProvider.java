package com.stox.watchlist.ui;

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
import com.stox.watchlist.event.WatchlistCreatedEvent;
import com.stox.watchlist.event.WatchlistDeletedEvent;
import com.stox.watchlist.event.WatchlistEditedEvent;

import javafx.scene.control.MenuItem;

@Lazy
@Component
public class AddToWatchlistMenuProvider implements TargetAwareMenuItemProvider {
	
	@Autowired
	private BeanFactory beanFactory; 
	
	private final Map<AddToWatchlistMenu<?>, AddToWatchlistMenu<?>> cache = new WeakHashMap<>();

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
		final AddToWatchlistMenu menu = beanFactory.getBean(AddToWatchlistMenu.class);
		menu.setEntryProvider((HasInstrument) target);
		cache.put(menu, menu);
		return menu;
	}
	
	@EventListener
	public void onWatchlistCreated(final WatchlistCreatedEvent event) {
		cache.keySet().forEach(menu -> menu.onWatchlistCreated(event));
	}
	
	@EventListener
	public void onWatchlistDeleted(final WatchlistDeletedEvent event) {
		cache.keySet().forEach(menu -> menu.onWatchlistDeleted(event));
	}
	
	@EventListener
	public void onWatchlistEdited(final WatchlistEditedEvent event) {
		cache.keySet().forEach(menu -> menu.onWatchlistEdited(event));
	}

}

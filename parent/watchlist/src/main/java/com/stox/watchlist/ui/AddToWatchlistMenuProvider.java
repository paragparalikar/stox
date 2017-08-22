package com.stox.watchlist.ui;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasInstrument;
import com.stox.core.ui.TargetAwareMenuItemProvider;

import javafx.scene.control.MenuItem;

@Lazy
@Component
public class AddToWatchlistMenuProvider implements TargetAwareMenuItemProvider {
	
	@Autowired
	private BeanFactory beanFactory; 

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
		return menu;
	}

}

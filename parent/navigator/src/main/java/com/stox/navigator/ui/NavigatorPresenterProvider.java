package com.stox.navigator.ui;

import java.util.Map;
import java.util.WeakHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class NavigatorPresenterProvider implements PresenterProvider {

	@Autowired
	private BeanFactory beanFactory;
	
	private final Map<NavigatorPresenter, NavigatorPresenter> cache = new WeakHashMap<>();

	@Override
	public String getViewCode() {
		return NavigatorUiConstant.CODE;
	}

	@Override
	public String getViewName() {
		return NavigatorUiConstant.NAME;
	}

	@Override
	public String getViewIcon() {
		return NavigatorUiConstant.ICON;
	}

	@Override
	public Presenter<?, ?> create() {
		final NavigatorPresenter navigatorPresenter = beanFactory.getBean(NavigatorPresenter.class);
		cache.put(navigatorPresenter, navigatorPresenter);
		return navigatorPresenter;
	}
	
	@EventListener
	public void onInstrumentsChanged(final InstrumentsChangedEvent event) {
		cache.keySet().forEach(navigatorPresenter -> navigatorPresenter.onInstrumentsChanged(event));
	}

}

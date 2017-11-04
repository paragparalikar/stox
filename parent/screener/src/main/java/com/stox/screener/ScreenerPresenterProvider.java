package com.stox.screener;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stox.workbench.ui.view.AbstractDockablePublishingPresenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class ScreenerPresenterProvider implements PresenterProvider {

	@Autowired
	private BeanFactory beanFactory;
	
	private final Set<ScreenerPresenter> cache = Collections.newSetFromMap(new WeakHashMap<>());
	
	@Override
	public String getViewCode() {
		return ScreenerUiConstant.CODE;
	}

	@Override
	public String getViewName() {
		return ScreenerUiConstant.NAME;
	}

	@Override
	public String getViewIcon() {
		return ScreenerUiConstant.ICON;
	}

	@Override
	public AbstractDockablePublishingPresenter<?, ?> create() {
		final ScreenerPresenter screenerPresenter = beanFactory.getBean(ScreenerPresenter.class);
		cache.add(screenerPresenter);
		return screenerPresenter;
	}

}

package com.stox.navigator.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class NavigatorPresenterProvider implements PresenterProvider {

	@Autowired
	private ApplicationContext context;

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
		return context.getBean(NavigatorPresenter.class);
	}

}

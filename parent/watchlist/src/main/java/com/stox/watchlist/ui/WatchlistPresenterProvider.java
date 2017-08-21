package com.stox.watchlist.ui;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stox.core.ui.StylesheetProvider;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class WatchlistPresenterProvider implements PresenterProvider, StylesheetProvider {
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Override
	public String[] getStylesheets() {
		return new String[]{"styles/watchlist.css"};
	}

	@Override
	public String getViewCode() {
		return WatchlistUiConstant.CODE;
	}

	@Override
	public String getViewName() {
		return WatchlistUiConstant.NAME;
	}

	@Override
	public String getViewIcon() {
		return WatchlistUiConstant.ICON;
	}

	@Override
	public Presenter<?, ?> create() {
		return beanFactory.getBean(WatchlistPresenter.class);
	}

}

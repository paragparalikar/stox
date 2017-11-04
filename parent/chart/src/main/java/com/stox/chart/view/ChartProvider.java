package com.stox.chart.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.stox.chart.util.ChartConstant;
import com.stox.core.ui.StylesheetProvider;
import com.stox.workbench.ui.view.AbstractDockablePublishingPresenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class ChartProvider implements PresenterProvider, StylesheetProvider {

	@Autowired
	private ApplicationContext context;

	@Override
	public String[] getStylesheets() {
		return new String[] { "styles/chart.css" };
	}

	@Override
	public String getViewCode() {
		return ChartConstant.CODE;
	}

	@Override
	public String getViewName() {
		return ChartConstant.NAME;
	}

	@Override
	public String getViewIcon() {
		return ChartConstant.ICON;
	}

	@Override
	public AbstractDockablePublishingPresenter<?, ?> create() {
		return context.getBean(ChartPresenter.class);
	}

}

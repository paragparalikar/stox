package com.stox.chart.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.stox.chart.util.ChartConstant;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;

@Component
public class ChartPresenterProvider implements PresenterProvider {

	@Autowired
	private ApplicationContext context;

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
	public Presenter<?, ?> create() {
		return context.getBean(ChartPresenter.class);
	}

}

package com.stox.screener;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

import com.stox.core.ui.HasSpinner;
import com.stox.screener.selection.ScreenSelectionPresenter;
import com.stox.workbench.ui.view.Presenter;

import lombok.NonNull;

public class ScreenerWizardPresenterFactory {

	private interface Wrapper {
		ScreenerWizardPresenterFactory INSTANCE = new ScreenerWizardPresenterFactory();
	}

	public static ScreenerWizardPresenterFactory getInstance() {
		return Wrapper.INSTANCE;
	}

	private ScreenerWizardPresenterFactory() {
	}

	public Presenter<?,String> get(@NonNull final String code, final ApplicationContext applicationContext,
			final HasSpinner hasSpinner, final TaskExecutor taskExecutor) {
		switch (code) {
		case ScreenerUiConstant.SCREEN_CONFIGURATION_PRESENTER:
		case ScreenerUiConstant.SCREEN_EXECUTION_PRESENTER:
		case ScreenerUiConstant.SCREEN_SELECTION_PRESENTER:
		default:
			return new ScreenSelectionPresenter(hasSpinner, taskExecutor, applicationContext);
		}
	}

}

package com.stox.screener.selection;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

import com.stox.core.ui.HasSpinner;
import com.stox.screen.Screen;
import com.stox.screener.ScreenerUiConstant;
import com.stox.workbench.ui.view.Container;
import com.stox.workbench.ui.view.WizardPresenter;

import lombok.NonNull;

public class ScreenSelectionPresenter implements WizardPresenter {

	private final HasSpinner hasSpinner;
	private final TaskExecutor taskExecutor;
	private final ApplicationContext applicationContext;
	private final ScreenSelectionView view = new ScreenSelectionView();

	public ScreenSelectionPresenter(@NonNull final HasSpinner hasSpinner, @NonNull final TaskExecutor taskExecutor,
			@NonNull final ApplicationContext beanFactory) {
		this.hasSpinner = hasSpinner;
		this.taskExecutor = taskExecutor;
		this.applicationContext = beanFactory;
	}

	@Override
	public void present(Container container) {
		container.add(view);
		hasSpinner.showSpinner(true);
		taskExecutor.execute(() -> {
			try {
				view.getListView().getItems().setAll(applicationContext.getBeansOfType(Screen.class).values());
			} finally {
				hasSpinner.showSpinner(false);
			}
		});
	}

	@Override
	public String getCode() {
		return ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	}

	@Override
	public String getViewState() {
		return null;
	}

	@Override
	public void setViewState(String state) {

	}

}

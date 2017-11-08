package com.stox.screener.selection;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.Container;
import com.stox.screen.Screen;
import com.stox.screener.ScreenerUiConstant;
import com.stox.screener.ScreenerViewState;
import com.stox.workbench.ui.view.WizardPresenter;

import lombok.NonNull;

public class ScreenSelectionPresenter implements WizardPresenter<String> {

	private final TaskExecutor taskExecutor;
	private final ApplicationContext applicationContext;
	private final ScreenerViewState screenerViewState;
	private final ScreenSelectionView view;

	public ScreenSelectionPresenter(@NonNull final ScreenerViewState screenerViewState,
			@NonNull final TaskExecutor taskExecutor, @NonNull final ApplicationContext beanFactory) {
		this.taskExecutor = taskExecutor;
		this.applicationContext = beanFactory;
		this.screenerViewState = screenerViewState;

		view = new ScreenSelectionView(screenerViewState.getScreenConfigurations());
	}

	@Override
	public String getId() {
		return ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	}

	@Override
	public void present(Container container) {
		container.add(view);
		container.showSpinner(true);
		taskExecutor.execute(() -> {
			try {
				view.getItems().setAll(applicationContext.getBeansOfType(Screen.class).values());
				screenerViewState.getScreenConfigurations().forEach(config -> {
					view.getSelectionModel().select(config.getScreen());
				});
			} finally {
				container.showSpinner(false);
			}
		});
	}

	@Override
	public String getTitleText() {
		return "Select Screens";
	}

	@Override
	public Message validate() {
		if (screenerViewState.getScreenConfigurations().isEmpty()) {
			return new Message("Please select at least one screen", MessageType.ERROR);
		}
		return null;
	}

	@Override
	public String getNextPresenterId() {
		return ScreenerUiConstant.SCREEN_CONFIGURATION_PRESENTER;
	}

	@Override
	public String getPreviousPresenterId() {
		return null;
	}

}

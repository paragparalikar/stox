package com.stox.screener.selection;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;

import com.stox.core.ui.HasSpinner;
import com.stox.screen.Screen;
import com.stox.screener.ScreenerUiConstant;
import com.stox.workbench.ui.view.Container;
import com.stox.workbench.ui.view.Presenter;

import lombok.NonNull;

public class ScreenSelectionPresenter implements Presenter<ScreenSelectionView, String> {

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
	public void present(Container container, String viewState) {
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

	public String getCode() {
		return ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(DataInput input) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(DataOutput output) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Container container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ScreenSelectionView getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getViewState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setViewSate(String viewState) {
		// TODO Auto-generated method stub
		
	}


}

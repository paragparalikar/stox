package com.stox.screener.configuration;

import com.stox.core.model.Message;
import com.stox.core.ui.Container;
import com.stox.screener.ScreenerUiConstant;
import com.stox.screener.ScreenerViewState;
import com.stox.workbench.ui.view.WizardPresenter;

public class ScreenConfigurationPresenter implements WizardPresenter<String> {

	private final ScreenConfigurationView view;
	private final ScreenerViewState screenerViewState;
	
	public ScreenConfigurationPresenter(final ScreenerViewState screenerViewState) {
		this.screenerViewState = screenerViewState;
		view = new ScreenConfigurationView(screenerViewState);
	}
	
	@Override
	public String getId() {
		return ScreenerUiConstant.SCREEN_CONFIGURATION_PRESENTER;
	}

	@Override
	public void present(Container container) {
		container.add(view);
	}

	@Override
	public String getTitleText() {
		return "Configurations";
	}

	@Override
	public Message validate() {
		view.updateModel();
		return null;
	}

	@Override
	public String getNextPresenterId() {
		return ScreenerUiConstant.SCREEN_EXECUTION_PRESENTER;
	}

	@Override
	public String getPreviousPresenterId() {
		return ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	}

}

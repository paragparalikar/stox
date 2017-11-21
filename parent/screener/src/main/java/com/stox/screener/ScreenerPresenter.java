package com.stox.screener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.model.Message;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.StringUtil;
import com.stox.screener.configuration.ScreenConfigurationPresenter;
import com.stox.screener.execution.ScreenExecutionPresenter;
import com.stox.screener.selection.ScreenSelectionPresenter;
import com.stox.workbench.ui.view.StatePublisherPresenter;
import com.stox.workbench.ui.view.WizardController;
import com.stox.workbench.ui.view.WizardPresenter;

import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class ScreenerPresenter extends StatePublisherPresenter<ScreenerView, ScreenerViewState> implements WizardController<String> {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private BarRepository barRepository;
	
	@Autowired
	private InstrumentRepository instrumentRepository;
	
	private WizardPresenter<String> currentPresenter;
	private final ScreenerView view = new ScreenerView();
	private final ScreenerViewState screenerViewState = new ScreenerViewState();
	
	public ScreenerPresenter() {
		view.getNextButton().addEventHandler(ActionEvent.ACTION, event -> {
			final Message message = currentPresenter.validate();
			if(null == message) {
				view.clearMessages();
				next(currentPresenter.getNextPresenterId());
			}else {
				view.setMessage(message);
			}
		});
		view.getPreviousButton().addEventHandler(ActionEvent.ACTION, event -> {
			previous(currentPresenter.getPreviousPresenterId());
		});
	}

	@Override
	public void start() {
		super.start();
		navigate(screenerViewState.getWizardPresenterId());
	}
	
	@Override
	public void next(String nextPresenterId) {
		navigate(nextPresenterId);
	}
	
	@Override
	public void previous(String previousPresenterId) {
		navigate(previousPresenterId);
	}
	
	private void navigate(final String presenterId) {
		if(!StringUtil.hasText(presenterId) || ScreenerUiConstant.SCREEN_SELECTION_PRESENTER.equals(presenterId)) {
			view.getNextButton().setVisible(true);
			view.getPreviousButton().setVisible(false);
			currentPresenter = new ScreenSelectionPresenter(screenerViewState, taskExecutor, applicationContext);
		}else if(ScreenerUiConstant.SCREEN_CONFIGURATION_PRESENTER.equals(presenterId)) {
			view.getNextButton().setVisible(true);
			view.getPreviousButton().setVisible(true);
			currentPresenter = new ScreenConfigurationPresenter(screenerViewState);
		}else if(ScreenerUiConstant.SCREEN_EXECUTION_PRESENTER.equals(presenterId)) {
			view.getNextButton().setVisible(false);
			view.getPreviousButton().setVisible(true);
			currentPresenter = new ScreenExecutionPresenter(screenerViewState, instrumentRepository, barRepository, taskExecutor, this);
		}
		
		currentPresenter.present(view);
		view.getTitleLabel().setText(currentPresenter.getTitleText());
	}
	

	@Override
	public ScreenerView getView() {
		return view;
	}

	@Override
	public ScreenerViewState getViewState() {
		populateViewState(screenerViewState);
		return screenerViewState;
	}

	@Override
	public void setDefaultPosition() {
		if (null != view.getParent()) {
			final Pane pane = (Pane) view.getParent();
			setPosition(0, 0, pane.getWidth() / 6, pane.getHeight());
		}
	}

	@Override
	public void publish(ApplicationEvent event) {
		eventPublisher.publishEvent(event);
	}

}

package com.stox.screener.execution;

import java.util.List;

import org.springframework.core.task.TaskExecutor;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.model.Message;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.ui.Container;
import com.stox.core.ui.util.Icon;
import com.stox.screener.ScreenConfiguration;
import com.stox.screener.ScreenerUiConstant;
import com.stox.screener.ScreenerViewState;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.StatePublisherPresenter;
import com.stox.workbench.ui.view.WizardPresenter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ScreenExecutionPresenter implements WizardPresenter<String> {

	private final BarRepository barRepository;
	private final TaskExecutor taskExecutor;
	private final ScreenerViewState screenerViewState;
	private final InstrumentRepository instrumentRepository;
	private final ScreenExecutionView view = new ScreenExecutionView();

	public ScreenExecutionPresenter(final ScreenerViewState screenerViewState,
			final InstrumentRepository instrumentRepository, final BarRepository barRepository,
			final TaskExecutor taskExecutor, final StatePublisherPresenter<?,?> publisherPresenter) {
		this.barRepository = barRepository;
		this.taskExecutor = taskExecutor;
		this.screenerViewState = screenerViewState;
		this.instrumentRepository = instrumentRepository;

		view.getMatches().setItems(screenerViewState.getMatches());
		final Button button = view.getActionButton();
		button.addEventHandler(ActionEvent.ACTION, event -> {
			if (!screenerViewState.isRunning()) {
				start();
			} else {
				stop();
			}
		});
		view.getMatches().getSelectionModel().selectedItemProperty().addListener((observable, old, instrument) -> {
			if (null != instrument) {
				publisherPresenter.publish(new State(instrument.getId(), null, 0));
			}
		});
	}

	private void start() {
		if (!screenerViewState.isRunning() && !screenerViewState.getScreenConfigurations().isEmpty()) {
			screenerViewState.setRunning(true);
			Platform.runLater(() -> {
				screenerViewState.getMatches().clear();
				view.getActionButton().setText(Icon.STOP);
			});
			taskExecutor.execute(() -> {
				final List<Instrument> instruments = instrumentRepository.getInstruments(Exchange.NSE,
						InstrumentType.EQUITY);
				match(instruments);
				stop();
			});
		}
	}

	private void match(final List<Instrument> instruments) {
		final int minBarCount = getMinBarCount();
		for (int index = 0; index < instruments.size() && screenerViewState.isRunning(); index++) {
			final Instrument instrument = instruments.get(index);
			final List<Bar> bars = barRepository.find(instrument.getId(), BarSpan.D, minBarCount);
			if (null != bars && minBarCount <= bars.size() && match(index, instrument, bars)) {
				Platform.runLater(() -> screenerViewState.getMatches().add(instrument));
			}
			view.updateProgress(((double) index) / ((double) instruments.size()));
		}
	}

	@SuppressWarnings("unchecked")
	private boolean match(final int index, final Instrument instrument, final List<Bar> bars) {
		boolean matched = true;
		for (final ScreenConfiguration screenConfiguration : screenerViewState.getScreenConfigurations()) {
			if (!screenConfiguration.getScreen().isMatch(screenConfiguration.getConfiguration(), bars)) {
				matched = false;
				break;
			}
		}
		return matched;
	}

	@SuppressWarnings("unchecked")
	private int getMinBarCount() {
		return screenerViewState.getScreenConfigurations().stream()
				.map(config -> config.getOffset() + config.getSpan()
						+ config.getScreen().getMinBarCount(config.getConfiguration()))
				.max((x, y) -> Integer.compare(x, y)).orElse(1);
	}

	private void stop() {
		view.updateProgress(1);
		screenerViewState.setRunning(false);
		Platform.runLater(() -> view.getActionButton().setText(Icon.PLAY));
	}

	@Override
	public String getId() {
		return ScreenerUiConstant.SCREEN_EXECUTION_PRESENTER;
	}

	@Override
	public void present(Container container) {
		container.add(view);
		start();
	}

	@Override
	public String getTitleText() {
		return "Matches";
	}

	@Override
	public Message validate() {
		return null;
	}

	@Override
	public String getNextPresenterId() {
		return null;
	}

	@Override
	public String getPreviousPresenterId() {
		return ScreenerUiConstant.SCREEN_CONFIGURATION_PRESENTER;
	}

}

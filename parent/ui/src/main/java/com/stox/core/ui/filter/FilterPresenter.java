package com.stox.core.ui.filter;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.util.StringConverter;

import org.springframework.core.task.TaskExecutor;

import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.Constant;

public class FilterPresenter {
	private static final String ALL = "All";

	private final List<Instrument> source;
	private final TaskExecutor taskExecutor;
	private final FilterView view = new FilterView();
	private final InstrumentRepository instrumentRepository;

	public Node getView() {
		return view;
	}

	public FilterPresenter(final List<Instrument> source, final InstrumentRepository instrumentRepository, final TaskExecutor taskExecutor) {
		this.source = source;
		this.taskExecutor = taskExecutor;
		this.instrumentRepository = instrumentRepository;

		bind();
		populateView();
	}

	public void filter(final Consumer<List<Instrument>> consumer) {
		if (null != consumer) {
			taskExecutor.execute(() -> {
				List<Instrument> instruments = Collections.emptyList();
				if (view.getChildren().contains(view.getIndexFormGroup())) {
					final Instrument index = view.getIndexChoiceBox().getValue();
					if (ALL.equals(index.getId())) {
						instruments = source
								.stream()
								.filter(instrument -> instrument.getExchange().equals(view.getExchangeChoiceBox().getValue())
										&& instrument.getType().equals(view.getTypeChoiceBox().getValue())).collect(Collectors.toList());
					} else {
						instruments = instrumentRepository.getComponentInstruments(index);
					}
				} else if (view.getChildren().contains(view.getExpiryFormGroup())) {
					final Date expiry = view.getExpiryChoiceBox().getValue();
					if (0 == expiry.getTime()) {
						instruments = source
								.stream()
								.filter(instrument -> instrument.getExchange().equals(view.getExchangeChoiceBox().getValue())
										&& instrument.getType().equals(view.getTypeChoiceBox().getValue())).collect(Collectors.toList());
					} else {
						instruments = source
								.stream()
								.filter(instrument -> instrument.getExchange().equals(view.getExchangeChoiceBox().getValue())
										&& instrument.getType().equals(view.getTypeChoiceBox().getValue()) && expiry.equals(instrument.getExpiry())).collect(Collectors.toList());
					}
				}
				consumer.accept(instruments);
			});
		}
	}

	private void populateView() {
		if (Platform.isFxApplicationThread()) {
			doPopulateView();
		} else {
			Platform.runLater(() -> {
				doPopulateView();
			});
		}
	}

	// TODO default selection should be handled after the state is set
	private void doPopulateView() {
		view.getExchangeChoiceBox().getItems().setAll(Exchange.values());
		view.getExchangeChoiceBox().getSelectionModel().select(Exchange.NSE);
		view.getTypeChoiceBox().getItems().setAll(InstrumentType.values());
		view.getTypeChoiceBox().getSelectionModel().select(InstrumentType.EQUITY);
	}

	private void bind() {
		view.getTypeChoiceBox().getSelectionModel().selectedItemProperty().addListener((observable, old, type) -> {
			if (InstrumentType.EQUITY.equals(type)) {
				addIndexFilter();
				view.getChildren().remove(view.getExpiryFormGroup());
			} else if (InstrumentType.CALL.equals(type) || InstrumentType.PUT.equals(type) || InstrumentType.FUTURE.equals(type)) {
				addExpiryFilter();
				view.getChildren().remove(view.getIndexFormGroup());
			}
		});
		view.getExpiryChoiceBox().setConverter(new StringConverter<Date>() {
			@Override
			public String toString(Date date) {
				return 0 == date.getTime() ? ALL : Constant.dateFormat.format(date);
			}

			@Override
			public Date fromString(String text) {
				try {
					if (ALL.equals(text)) {
						return createDummyExpiry();
					}
					return Constant.dateFormat.parse(text);
				} catch (ParseException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		});
	}

	private void addIndexFilter() {
		final Exchange exchange = view.getExchangeChoiceBox().getValue();
		final List<Instrument> indexes = instrumentRepository.getInstruments(exchange, InstrumentType.INDEX);
		final Instrument dummyInstrument = createDummyInstrument();
		indexes.add(0, dummyInstrument);
		view.getIndexChoiceBox().getItems().setAll(indexes);
		if (!view.getChildren().contains(view.getIndexFormGroup())) {
			view.getChildren().add(view.getIndexFormGroup());
		}
		view.getIndexChoiceBox().getSelectionModel().select(dummyInstrument);
	}

	private Instrument createDummyInstrument() {
		final Instrument instrument = new Instrument();
		instrument.setIsin(ALL);
		instrument.setName(ALL);
		return instrument;
	}

	private void addExpiryFilter() {
		final Exchange exchange = view.getExchangeChoiceBox().getValue();
		final InstrumentType type = view.getTypeChoiceBox().getValue();
		final List<Date> expiries = source.stream().filter(instrument -> exchange.equals(instrument.getExchange()) && type.equals(instrument.getType())).map(Instrument::getExpiry)
				.distinct().sorted().collect(Collectors.toList());
		expiries.remove(null);
		final Date dummyExpiry = createDummyExpiry();
		expiries.add(0, dummyExpiry);
		view.getExpiryChoiceBox().getItems().setAll(expiries);
		if (!view.getChildren().contains(view.getExpiryFormGroup())) {
			view.getChildren().add(view.getExpiryFormGroup());
		}
		view.getExpiryChoiceBox().getSelectionModel().select(dummyExpiry);
	}

	private Date createDummyExpiry() {
		final Date date = new Date();
		date.setTime(0);
		return date;
	}

}
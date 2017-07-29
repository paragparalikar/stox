package com.stox.core.ui.filter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Node;

import org.springframework.core.task.TaskExecutor;

import com.stox.core.intf.Callback;
import com.stox.core.model.Exchange;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.util.Constant;
import com.stox.core.util.StringUtil;

public class FilterPresenter {
	private static final String ALL = "All";

	private final List<Instrument> source;
	private final TaskExecutor taskExecutor;
	private final FilterView view = new FilterView();

	public Node getView() {
		return view;
	}

	public FilterPresenter(final List<Instrument> source, final TaskExecutor taskExecutor) {
		this.source = source;
		this.taskExecutor = taskExecutor;
		bind();
		setExchanges();
		setTypes();
		setExpiries(source);
	}

	private void bind() {
		view.getExchangeChoiceBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, exchange) -> {
			taskExecutor.execute(() -> {
				final List<Instrument> filtered = filterByExchange(exchange, source);
				setTypes(filtered);
				setExpiries(filtered);
			});
		});
		view.getTypeChoiceBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, type) -> {
			taskExecutor.execute(() -> {
				final List<Instrument> filtered = filterByExchange(view.getExchangeChoiceBox().getValue(), source);
				setExpiries(filterByType(type, filtered));
			});
		});
	}

	public void filter(final Callback<List<Instrument>, Void> callback) {
		taskExecutor.execute(() -> {
			final List<Instrument> filteredByExchange = filterByExchange(view.getExchangeChoiceBox().getValue(), source);
			final List<Instrument> filteredByType = filterByType(view.getTypeChoiceBox().getValue(), filteredByExchange);
			final List<Instrument> filteredByExpiry = filterByExpiry(view.getExpiryChoiceBox().getValue(), filteredByType);
			callback.call(filteredByExpiry);
		});
	}

	private List<Instrument> filterByExchange(final String exchange, final List<Instrument> source) {
		return filter(exchange, instrument -> exchange.equals(instrument.getExchange().getName()), source);
	}

	private List<Instrument> filterByType(final String type, final List<Instrument> source) {
		return filter(type, instrument -> type.equals(instrument.getType().getName()), source);
	}

	private List<Instrument> filterByExpiry(final String expiry, final List<Instrument> source) {
		return filter(expiry, instrument -> expiry.equals(null == instrument.getExpiry() ? "" : Constant.dateFormat.format(instrument.getExpiry())), source);
	}

	private List<Instrument> filter(final String text, Predicate<? super Instrument> predicate, final List<Instrument> source) {
		return StringUtil.hasText(text) && !ALL.equals(text) ? source.stream().filter(predicate).collect(Collectors.toList()) : source;
	}

	private void setExchanges() {
		final List<String> exchanges = Arrays.asList(Exchange.values()).stream().map(exchange -> exchange.getName()).collect(Collectors.toList());
		exchanges.add(0, ALL); // TODO I18N here
		Platform.runLater(() -> {
			view.getExchangeChoiceBox().getItems().setAll(exchanges);
			view.getExchangeChoiceBox().getSelectionModel().select(Exchange.NSE.getName());
		});
	}

	private void setTypes() {
		final List<String> types = Arrays.asList(InstrumentType.values()).stream().map(type -> type.getName()).collect(Collectors.toList());
		types.add(0, ALL); // TODO I18N here
		Platform.runLater(() -> {
			view.getTypeChoiceBox().getItems().setAll(types);
			view.getTypeChoiceBox().getSelectionModel().select(InstrumentType.EQUITY.getName());
		});
	}

	private void setTypes(final List<Instrument> instruments) {
		final List<String> types = instruments.stream().map(Instrument::getType).filter(type -> null != type).distinct().map(InstrumentType::getName).sorted()
				.collect(Collectors.toList());
		types.add(0, ALL); // TODO I18N here
		Platform.runLater(() -> {
			view.getTypeChoiceBox().getItems().setAll(types);
			view.getTypeChoiceBox().getSelectionModel().select(InstrumentType.EQUITY.getName());
		});
	}

	private void setExpiries(final List<Instrument> instruments) {
		final List<String> expiries = instruments.stream().map(Instrument::getExpiry).filter(date -> null != date).distinct().sorted().map(date -> {
			return Constant.dateFormat.format(date);
		}).collect(Collectors.toList());
		expiries.remove("");
		expiries.add(0, ALL); // TODO I18N here
		Platform.runLater(() -> {
			view.getExpiryChoiceBox().getItems().setAll(expiries);
			view.getExpiryChoiceBox().getSelectionModel().select(ALL);
		});
	}

}
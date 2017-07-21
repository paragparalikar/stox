package com.stox.zerodha.ui;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;

import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.core.util.StringUtil;

public class ZerodhaInstrumentFilterPresenter {
	private static final String ALL = "All";

	private final List<Instrument> source;
	private final List<Instrument> target;
	private final ZerodhaInstrumentFilterModal view = new ZerodhaInstrumentFilterModal();

	public ZerodhaInstrumentFilterModal getView() {
		return view;
	}

	public ZerodhaInstrumentFilterPresenter(final List<Instrument> source, final List<Instrument> target) {
		this.source = source;
		this.target = target;
		bind();
		setExchanges(source);
	}

	private void bind() {
		view.getExchangeChoiceBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, exchange) -> {
			final List<Instrument> filtered = filterByExchange(exchange, source);
			setTypes(filtered);
			setExpiries(filtered);
		});
		view.getTypeChoiceBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, type) -> {
			final List<Instrument> filtered = filterByExchange(view.getExchangeChoiceBox().getValue(), source);
			setExpiries(filterByType(type, filtered));
		});
		view.getFilterButton().addEventHandler(ActionEvent.ACTION, event -> filter());
	}

	private void filter() {
		List<Instrument> filtered = filterByExchange(view.getExchangeChoiceBox().getValue(), source);
		filtered = filterByType(view.getTypeChoiceBox().getValue(), filtered);
		filtered = filterByExpiry(view.getExpiryChoiceBox().getValue(), filtered);
		target.clear();
		target.addAll(filtered);
		view.hide();
	}

	private List<Instrument> filterByExchange(final String exchange, final List<Instrument> source) {
		return filter(exchange, instrument -> exchange.equals(instrument.getExchange()), source);
	}

	private List<Instrument> filterByType(final String type, final List<Instrument> source) {
		return filter(type, instrument -> type.equals(instrument.getType()), source);
	}

	private List<Instrument> filterByExpiry(final String expiry, final List<Instrument> source) {
		return filter(expiry, instrument -> expiry.equals(null == instrument.getExpiry() ? "" : Constant.dateFormat.format(instrument.getExpiry())), source);
	}

	private List<Instrument> filter(final String text, Predicate<? super Instrument> predicate, final List<Instrument> source) {
		return StringUtil.hasText(text) && !ALL.equals(text) ? source.stream().filter(predicate).collect(Collectors.toList()) : source;
	}

	private void setExchanges(final List<Instrument> instruments) {
		final List<String> exchanges = source.stream().map(Instrument::getExchange).distinct().sorted().collect(Collectors.toList());
		exchanges.add(0, ALL); // TODO I18N here
		view.getExchangeChoiceBox().getItems().setAll(exchanges);
	}

	private void setTypes(final List<Instrument> instruments) {
		view.getTypeChoiceBox().getSelectionModel().clearSelection();
		final List<String> types = instruments.stream().map(Instrument::getType).distinct().sorted().collect(Collectors.toList());
		types.add(0, ALL); // TODO I18N here
		view.getTypeChoiceBox().getItems().setAll(types);
	}

	private void setExpiries(final List<Instrument> instruments) {
		view.getExpiryChoiceBox().getSelectionModel().clearSelection();
		final List<String> expiries = instruments.stream().map(instrument -> {
			return null == instrument.getExpiry() ? "" : Constant.dateFormat.format(instrument.getExpiry());
		}).distinct().sorted().collect(Collectors.toList());
		expiries.remove("");
		expiries.add(0, ALL); // TODO I18N here
		view.getExpiryChoiceBox().getItems().setAll(expiries);
	}

}

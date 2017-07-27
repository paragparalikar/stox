package com.stox.core.ui.filter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.scene.Node;

import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.core.util.StringUtil;

public class FilterPresenter {
	private static final String ALL = "All";

	private final List<Instrument> source;
	private final List<Instrument> target;
	private final FilterView view = new FilterView();

	public Node getView() {
		return view;
	}

	public FilterPresenter(final List<Instrument> source, final List<Instrument> target) {
		this.source = source;
		this.target = target;
		bind();
		setExchanges(source);
		setTypes(source);
		setExpiries(source);
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
	}

	public void filter() {
		List<Instrument> filtered = filterByExchange(view.getExchangeChoiceBox().getValue(), source);
		filtered = filterByType(view.getTypeChoiceBox().getValue(), filtered);
		filtered = filterByExpiry(view.getExpiryChoiceBox().getValue(), filtered);
		target.clear();
		target.addAll(filtered);
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
		final List<String> exchanges = source.stream().map(instrument -> null == instrument.getExchange() ? "" : instrument.getExchange().getName()).distinct().sorted()
				.collect(Collectors.toList());
		exchanges.add(0, ALL); // TODO I18N here
		view.getExchangeChoiceBox().getItems().setAll(exchanges);
		view.getExchangeChoiceBox().getSelectionModel().select(ALL);
	}

	private void setTypes(final List<Instrument> instruments) {
		final List<String> types = instruments.stream().map(instrument -> null == instrument.getType() ? "" : instrument.getType().getName()).distinct().sorted()
				.collect(Collectors.toList());
		types.add(0, ALL); // TODO I18N here
		view.getTypeChoiceBox().getItems().setAll(types);
		view.getTypeChoiceBox().getSelectionModel().select(ALL);
	}

	private void setExpiries(final List<Instrument> instruments) {
		view.getExpiryChoiceBox().getSelectionModel().clearSelection();
		final List<String> expiries = instruments.stream().map(Instrument::getExpiry).filter(date -> null != date).distinct().sorted().map(date -> {
			return Constant.dateFormat.format(date);
		}).collect(Collectors.toList());
		expiries.remove("");
		expiries.add(0, ALL); // TODO I18N here
		view.getExpiryChoiceBox().getItems().setAll(expiries);
		view.getExpiryChoiceBox().getSelectionModel().select(ALL);
	}

}
package com.stox.zerodha.ui;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.stox.core.model.Instrument;

public class ZerodhaInstrumentFilterPresenter {

	private final List<Instrument> source;
	private final List<Instrument> target;
	private final ZerodhaInstrumentFilterModal view = new ZerodhaInstrumentFilterModal();

	public ZerodhaInstrumentFilterModal getView() {
		return view;
	}

	public ZerodhaInstrumentFilterPresenter(final List<Instrument> source, final List<Instrument> target) {
		this.source = source;
		this.target = target;

		if (null != source && null != target) {
			final List<String> exchanges = source.stream().map(Instrument::getExchange).distinct().collect(Collectors.toList());
			final List<String> types = source.stream().map(Instrument::getType).distinct().collect(Collectors.toList());
			final List<Date> expiries = source.stream().map(Instrument::getExpiry).distinct().collect(Collectors.toList());
			final List<Double> strikes = source.stream().map(Instrument::getStrike).distinct().collect(Collectors.toList());

			exchanges.add(0, "All"); // TODO I18N here
			view.getExchangeChoiceBox().getItems().setAll(exchanges);
			view.getExchangeChoiceBox().getSelectionModel().selectedItemProperty().addListener(exchange -> {

			});

		}
	}

}

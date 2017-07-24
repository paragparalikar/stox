package com.stox.chart.plot;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.stox.chart.chart.Chart;
import com.stox.chart.unit.LineUnit;
import com.stox.chart.unit.Unit;
import com.stox.core.model.Bar;
import com.stox.core.model.Instrument;

public class PricePlot extends Plot<Bar> {

	private final ObjectProperty<Instrument> instrumentProperty = new SimpleObjectProperty<>();

	public PricePlot(final Chart chart) {
		super(chart);
	}

	@Override
	protected Unit<Bar> create(final int index, final Bar model) {
		// TODO Auto-generated method stub
		return new LineUnit<>(index, model, this);
	}

	public Instrument getInstrument() {
		return instrumentProperty.get();
	}

	public ObjectProperty<Instrument> instrumentProperty() {
		return instrumentProperty;
	}

	public void setInstrument(final Instrument instrument) {
		instrumentProperty.set(instrument);
	}

}

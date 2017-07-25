package com.stox.chart.plot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.chart.Chart;
import com.stox.chart.event.BarRequestEvent;
import com.stox.chart.unit.LineUnit;
import com.stox.chart.unit.Unit;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;
import com.stox.core.model.Instrument;

@Data
@EqualsAndHashCode(callSuper = true)
public class PricePlot extends Plot<Bar> {

	private Instrument instrument;

	public PricePlot(final Chart chart) {
		super(chart);
	}

	@Override
	protected Unit<Bar> create(final int index, final Bar model) {
		return new LineUnit<>(index, model, this);
	}

	@Override
	public void load() {
		final ChartView chartView = getChart().getChartView();
		chartView.fireEvent(new BarRequestEvent(instrument.getExchangeCode(), chartView.getBarSpan(), chartView.getFrom(), chartView.getTo(), bars -> {
			getModels().setAll(bars);
			return null;
		}));
	}
}

package com.stox.chart.plot;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.event.BarRequestEvent;
import com.stox.chart.unit.PriceUnitType;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Instrument;
import com.stox.core.util.StringUtil;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chart", "priceUnitType" })
public class PrimaryPricePlot extends PricePlot {

	private final PrimaryChart chart;
	private PriceUnitType priceUnitType;

	public PrimaryPricePlot(final PrimaryChart chart) {
		super(chart);
		this.chart = chart;
	}

	@Override
	public void load() {
		final Instrument instrument = getInstrument();
		if (null != instrument && StringUtil.hasText(instrument.getId())) {
			chart.getPricePlots().forEach(Plot::load);
			final ChartView chartView = chart.getChartView();
			chartView.fireEvent(new BarRequestEvent(instrument.getId(), chartView.getBarSpan(), chartView.getFrom(), chartView.getTo(), bars -> {
				getModels().setAll(bars);
				chartView.getCharts().forEach(c -> {
					c.getPlots().forEach(Plot::load);
				});
				return null;
			}));
		}
	}
}

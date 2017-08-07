package com.stox.chart.plot;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.unit.AreaUnit;
import com.stox.chart.unit.CandlePriceUnit;
import com.stox.chart.unit.HlcPriceUnit;
import com.stox.chart.unit.LineUnit;
import com.stox.chart.unit.OhlcPriceUnit;
import com.stox.chart.unit.PriceUnitType;
import com.stox.chart.unit.Unit;
import com.stox.chart.view.ChartView;
import com.stox.core.model.Bar;
import com.stox.core.model.Instrument;

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
	protected Unit<Bar> create(int index, Bar model) {
		switch (chart.getChartView().getPriceUnitType()) {
		case LINE:
			return new LineUnit<>(index, model, this);
		case AREA:
			return new AreaUnit<>(index, model, this);
		case HLC:
			return new HlcPriceUnit(index, model, this);
		case OHLC:
			return new OhlcPriceUnit(index, model, this);
		case CANDLE:
			return new CandlePriceUnit(index, model, this);
		}
		return null;
	}

	@Override
	public void setInstrument(Instrument instrument) {
		chart.getChartView().clearMessages();
		chart.getChartView().getDateAxis().reset();
		chart.getChartView().getTitleBar().setTitleText(instrument.getName());
		super.setInstrument(instrument);
	}

	@Override
	public void update() {
		chart.reset();
		super.update();
	}

	@Override
	protected void updateChartValueBounds() {
		super.updateChartValueBounds();
		chart.getValueAxis().setDirty();
	}

	@Override
	public void load() {
		super.load();
		chart.getPricePlots().forEach(Plot::load);
	}

	@Override
	protected void addModels(List<Bar> bars) {
		super.addModels(bars);
		final ChartView chartView = chart.getChartView();
		chart.getPlots().forEach(Plot::load);
		chartView.getCharts().forEach(c -> {
			c.getPlots().forEach(Plot::load);
		});
	}

	long previousDate = 0;

	@Override
	public void preLayout() {
		previousDate = 0;
		getChart().getChartView().getDateAxis().clear();
		super.preLayout();
	}

	@Override
	public void layoutUnit(final Unit<Bar> unit, final double x, final double width) {
		final Bar bar = unit.getModel();
		getChart().getChartView().getDateAxis().addTick(x, bar.getDate().getTime(), previousDate);
		previousDate = bar.getDate().getTime();
		super.layoutUnit(unit, x, width);
	}

}

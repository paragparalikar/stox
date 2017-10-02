package com.stox.chart.plot;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.unit.CandlePriceUnit;
import com.stox.chart.unit.HlcPriceUnit;
import com.stox.chart.unit.OhlcPriceUnit;
import com.stox.chart.unit.Unit;
import com.stox.chart.unit.UnitType;
import com.stox.chart.view.ChartView;
import com.stox.chart.widget.PrimaryPricePlotInfoPanel;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Tick;
import com.stox.data.tick.TickConsumer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chart", "priceUnitType" })
public class PrimaryPricePlot extends PricePlot implements TickConsumer {

	private UnitType priceUnitType;
	private final PrimaryChart chart;
	private PrimaryPricePlotInfoPanel primaryPricePlotInfoPanel;

	public PrimaryPricePlot(final PrimaryChart chart) {
		super(chart);
		this.chart = chart;
		primaryPricePlotInfoPanel.attach();
	}

	@Override
	protected PrimaryPricePlotInfoPanel createPlotInfoPanel() {
		primaryPricePlotInfoPanel = new PrimaryPricePlotInfoPanel(this);
		return primaryPricePlotInfoPanel;
	}

	@Override
	protected Unit<Bar> create(int index, Bar model) {
		switch (getUnitType()) {
		case HLC:
			return new HlcPriceUnit(index, model, this);
		case OHLC:
			return new OhlcPriceUnit(index, model, this);
		case CANDLE:
			return new CandlePriceUnit(index, model, this);
		default:
			return super.create(index, model);
		}
	}

	@Override
	public UnitType getUnitType() {
		return chart.getChartView().getUnitType();
	}

	@Override
	public void setInstrument(Instrument instrument) {
		primaryPricePlotInfoPanel.clear();
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
	protected void addModels(int index, List<Bar> bars) {
		super.addModels(index, bars);
		chart.setDirty();
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

	@Override
	public void accept(final Tick tick) {
		final List<Bar> bars = getModels();
		final Instrument instrument = getInstrument();
		final BarSpan barSpan = chart.getChartView().getBarSpan();
		if(null != instrument && !bars.isEmpty() && null != barSpan && instrument.equals(tick.getInstrument())) {
			final Bar bar = bars.get(0);
			final Date barDate = bar.getDate();
			final Date tickDate = tick.getLastTradeDate();
			if(!tickDate.before(barDate)) {
				final double lastTradingPrice = tick.getLastTradePrice();
				if(tickDate.getTime() >= barSpan.next(barDate.getTime())) {
					final Bar nextBar = new Bar();
					nextBar.setOpen(lastTradingPrice);
					nextBar.setHigh(lastTradingPrice);
					nextBar.setLow(lastTradingPrice);
					nextBar.setClose(lastTradingPrice);
					nextBar.setPreviousClose(bar.getClose());
					nextBar.setVolume(tick.getLastTradeSize());
					nextBar.setDate(tick.getLastTradeDate());
					nextBar.setInstrumentId(bar.getInstrumentId());
					addModels(0, Arrays.asList(new Bar[] {nextBar}));
				}else {
					bar.setHigh(Math.max(bar.getHigh(), lastTradingPrice));
					bar.setLow(Math.min(bar.getLow(), lastTradingPrice));
					bar.setClose(lastTradingPrice);
					bar.setVolume(bar.getVolume() + tick.getLastTradeSize());
					update();
				}
			}
		}
	}
}

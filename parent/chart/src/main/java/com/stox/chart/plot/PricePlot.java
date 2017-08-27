package com.stox.chart.plot;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.event.BarRequestEvent;
import com.stox.chart.unit.UnitType;
import com.stox.chart.util.ChartUtil;
import com.stox.chart.view.ChartView;
import com.stox.chart.widget.PricePlotInfoPanel;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.core.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PricePlot extends Plot<Bar> {

	private Instrument instrument;
	private volatile boolean locked;
	private boolean dataAvailable = true;
	private final PrimaryChart primaryChart;

	public PricePlot(final PrimaryChart chart) {
		super(chart);
		this.primaryChart = chart;
	}

	@Override
	public PrimaryChart getChart() {
		return null == primaryChart ? (PrimaryChart) super.getChart() : primaryChart;
	}

	@Override
	protected PricePlotInfoPanel createPlotInfoPanel() {
		return new PricePlotInfoPanel(this);
	}

	public void setInstrument(final Instrument instrument) {
		if (this.instrument != instrument) {
			this.instrument = instrument;
			getPlotInfoPane().setName(getName());
			dataAvailable = true;
		}
	}

	@Override
	public String getName() {
		return null == instrument ? null : instrument.getName();
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.LINE;
	}

	protected void addModels(final int index, final List<Bar> bars) {
		getModels().addAll(index, bars);
		update();
	}

	private void addData(final Date from, final BarSpan barSpan, final List<Bar> bars) {
		if (bars.isEmpty()) {
			dataAvailable = false;
		}
		addModels(getModels().size(), bars);
		if (dataAvailable) {
			loadExtra();
		}
	}

	@Override
	public void load() {
		if (null != instrument && StringUtil.hasText(instrument.getId())) {
			final ChartView chartView = getChart().getChartView();
			final Date to = chartView.getTo();
			final BarSpan barSpan = chartView.getBarSpan();
			final Date from = ChartUtil.getFrom(to, barSpan);
			getModels().clear();
			chartView.fireEvent(
					new BarRequestEvent(instrument.getId(), barSpan, from, to, new ResponseCallback<List<Bar>>() {
						@Override
						public void onSuccess(Response<List<Bar>> response) {
							addData(from, barSpan, response.getPayload());
						}
					}));
		}
	}

	@SuppressWarnings("unused")
	private void merge(final List<Bar> existingBars, final List<Bar> newBars, final BarSpan barSpan) {
		if (!existingBars.isEmpty()) {
			final Bar first = existingBars.get(0);
			final long nextDate = barSpan.next(first.getDate().getTime());
			final Iterator<Bar> iterator = newBars.iterator();
			while (iterator.hasNext()) {
				final Bar bar = iterator.next();
				if (bar.getDate().getTime() <= nextDate) {
					for (final Bar model : existingBars) {
						if (barSpan.next(model.getDate().getTime()) >= bar.getDate().getTime()
								&& model.getDate().getTime() <= bar.getDate().getTime()) {
							model.copy(bar);
							break;
						}
					}
					iterator.remove();
				}
			}
		}
	}

	public void loadExtra() {
		final List<Bar> bars = getModels();
		final ChartView chartView = getChart().getChartView();
		if (null != instrument && 0 < getModels().size() && !locked) {
			if (dataAvailable && getModels().size() < chartView.getDateAxis().getLowerBoundIndex()) {
				locked = true;
				final Bar oldestBar = bars.get(bars.size() - 1);
				final Date to = oldestBar.getDate();
				final BarSpan barSpan = chartView.getBarSpan();
				final Date from = ChartUtil.getFrom(to, barSpan);
				chartView.fireEvent(
						new BarRequestEvent(instrument.getId(), barSpan, from, to, new ResponseCallback<List<Bar>>() {

							@Override
							public void onSuccess(Response<List<Bar>> response) {
								locked = false;
								addData(from, barSpan, response.getPayload());
							}

							@Override
							public void onFailure(Response<List<Bar>> response, Throwable throwable) {
								locked = false;
								dataAvailable = false;
							}

						}));
			} else {
				update();
			}
		} else {
			update();
		}
	}
}

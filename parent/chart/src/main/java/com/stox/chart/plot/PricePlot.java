package com.stox.chart.plot;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.chart.Chart;
import com.stox.chart.event.BarRequestEvent;
import com.stox.chart.unit.LineUnit;
import com.stox.chart.unit.Unit;
import com.stox.chart.util.ChartUtil;
import com.stox.chart.view.ChartView;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.core.util.StringUtil;

@Data
@EqualsAndHashCode(callSuper = true)
public class PricePlot extends Plot<Bar> {

	private Instrument instrument;
	private volatile boolean locked;
	private boolean dataAvailable = true;

	public PricePlot(final Chart chart) {
		super(chart);
	}

	@Override
	protected Unit<Bar> create(final int index, final Bar model) {
		return new LineUnit<>(index, model, this);
	}

	public void setInstrument(final Instrument instrument) {
		if (this.instrument != instrument) {
			this.instrument = instrument;
			dataAvailable = true;
		}
	}

	protected void addModels(final List<Bar> bars) {
		getModels().addAll(bars);
	}

	private void addData(final Date from, final BarSpan barSpan, final List<Bar> bars) {
		if (bars.isEmpty()) {
			dataAvailable = false;
		} else {
			final Bar bar = bars.get(bars.size() - 1);
			final Date date = new Date(barSpan.next(from.getTime()));
			dataAvailable = date.after(bar.getDate()) || date.equals(bar.getDate());
		}
		addModels(bars);
		if (dataAvailable) {
			loadExtra();
		}
	}

	@Override
	public void load() {
		if (null != instrument && StringUtil.hasText(instrument.getId())) {
			final ChartView chartView = getChart().getChartView();
			final Date to = chartView.getTo();
			final Date from = chartView.getFrom();
			final BarSpan barSpan = chartView.getBarSpan();
			getModels().clear();
			chartView.fireEvent(new BarRequestEvent(instrument.getExchangeCode(), barSpan, from, to, new ResponseCallback<List<Bar>>() {
				@Override
				public void onSuccess(Response<List<Bar>> response) {
					addData(from, barSpan, response.getPayload());
				}

				@Override
				public void onFailure(Response<List<Bar>> response, Throwable throwable) {
					dataAvailable = false;
				}
			}));
		}
	}

	public void loadExtra() {
		final List<Bar> bars = getModels();
		final ChartView chartView = getChart().getChartView();
		if (null != instrument && 0 < getModels().size() && !locked && dataAvailable && getModels().size() < chartView.getDateAxis().getLowerBoundIndex()) {
			locked = true;
			final Bar oldestBar = bars.get(bars.size() - 1);
			final Date to = oldestBar.getDate();
			final BarSpan barSpan = chartView.getBarSpan();
			final Date from = ChartUtil.getFrom(to, barSpan);

			chartView.fireEvent(new BarRequestEvent(instrument.getExchangeCode(), barSpan, from, to, new ResponseCallback<List<Bar>>() {

				@Override
				public void onSuccess(Response<List<Bar>> response) {
					addData(from, barSpan, response.getPayload());
				}

				@Override
				public void onFailure(Response<List<Bar>> response, Throwable throwable) {
					dataAvailable = false;
				}

				@Override
				public void onDone() {
					locked = false;
				}
			}));

			if (dataAvailable) {
				loadExtra();
			}
		}
	}
}

package com.stox.chart.plot;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.axis.DateAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.unit.Unit;
import com.stox.core.intf.Range;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chart", "units", "models" })
public abstract class Plot<M extends Range> extends Group {

	private Chart chart;
	private boolean dirty;
	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	private int lastMinIndex = Integer.MIN_VALUE, lastMaxIndex = Integer.MAX_VALUE;
	private final ObservableList<M> models = FXCollections.observableArrayList();
	private final ObservableList<Unit<M>> units = FXCollections.observableArrayList();

	public Plot(final Chart chart) {
		this.chart = chart;
		models.addListener((ListChangeListener<M>) (change) -> {
			lastMinIndex = Integer.MIN_VALUE;
			lastMaxIndex = Integer.MAX_VALUE;
			while (change.next()) {
				if (change.wasAdded()) {
					for (int index = change.getFrom(); index < change.getTo(); index++) {
						final M model = models.get(index);
						final Unit<M> unit = create(index, model);
						units.add(unit);
						getChildren().add(unit);
					}
				}
				if (change.wasRemoved()) {
					units.clear();
					getChildren().clear();
				}
			}
			setDirty();
		});
	}

	public abstract void load();

	protected abstract Unit<M> create(final int index, final M model);

	public void setDirty() {
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		final DateAxis dateAxis = chart.getChartView().getDateAxis();
		final int lowerBoundIndex = dateAxis.getLowerBoundIndex();
		final int upperBoundIndex = dateAxis.getUpperBoundIndex();
		for (int index = lowerBoundIndex; index >= upperBoundIndex; index--) {
			if (index >= 0 && index < units.size()) {
				final Unit<M> unit = units.get(index);
				if (null != unit) {
					min = Math.min(min, min(unit.getModel()));
					max = Math.max(max, max(unit.getModel()));
				}
			}
		}
		dirty = true;
	}

	public double min(M model) {
		return model.getLow();
	}

	public double max(M model) {
		return model.getHigh();
	}

	@Override
	protected final void layoutChildren() {
		if (dirty) {
			dirty = false;
			final DateAxis dateAxis = chart.getChartView().getDateAxis();
			final int minIndex = dateAxis.getUpperBoundIndex();
			final int maxIndex = dateAxis.getLowerBoundIndex();
			final double width = dateAxis.getUnitWidth();
			lastMinIndex = Math.max(lastMinIndex, 0);
			lastMaxIndex = Math.min(lastMaxIndex, units.size() - 1);
			for (int index = lastMaxIndex; index >= lastMinIndex; index--) {
				final Unit<M> unit = units.get(index);
				if (null != unit) {
					if (index <= maxIndex && index >= minIndex) {
						unit.setVisible(true);
						final double x = dateAxis.getDisplayPosition(index);
						unit.layoutChartChildren(x, width);
					} else {
						unit.setVisible(false);
					}
				}
			}
			lastMinIndex = minIndex;
			lastMaxIndex = maxIndex;
		}
	}
}

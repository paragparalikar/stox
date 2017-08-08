package com.stox.chart.plot;

import java.util.stream.IntStream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.axis.DateAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.unit.AreaPlotNode;
import com.stox.chart.unit.AreaUnit;
import com.stox.chart.unit.BarUnit;
import com.stox.chart.unit.LinePlotNode;
import com.stox.chart.unit.LineUnit;
import com.stox.chart.unit.PlotNode;
import com.stox.chart.unit.Unit;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "chart", "units", "models" })
public abstract class Plot<M extends Range> extends Group {

	private Chart chart;
	private boolean dirty;
	private Color color = Color.GRAY;
	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	private int lastMinIndex = Integer.MIN_VALUE, lastMaxIndex = Integer.MAX_VALUE;
	private final ObjectProperty<PlotNode> plotNodeProperty = new SimpleObjectProperty<>();
	private final ObservableList<M> models = FXCollections.observableArrayList();
	private final ObservableList<Unit<M>> units = FXCollections.observableArrayList();

	public Plot(final Chart chart) {
		setChart(chart);
		plotNodeProperty.addListener((observable, old, node) -> {
			getChildren().remove(old);
			if (null != node) {
				getChildren().add((Node) node);
			}
		});
		models.addListener((ListChangeListener<M>) (change) -> {
			lastMinIndex = Integer.MIN_VALUE;
			lastMaxIndex = Integer.MAX_VALUE;
			while (change.next()) {
				if (change.wasRemoved()) {
					clearUnits();
				}
				if (change.wasAdded()) {
					createUnits(change.getFrom(), change.getTo());
				}
			}
		});
	}

	public void clearUnits() {
		units.clear();
		getChildren().clear();
	}

	public void createUnits(final int from, final int to) {
		switch (getUnitType()) {
		case LINE:
			plotNodeProperty.set(new LinePlotNode(this));
			break;
		case AREA:
			plotNodeProperty.set(new AreaPlotNode(this));
			break;
		default:
			plotNodeProperty.set(null);
		}
		IntStream.range(from, to).forEach(index -> {
			final M model = models.get(index);
			final Unit<M> unit = create(index, model);
			units.add(unit);
			getChildren().add(unit);
		});
	}

	public void setChart(final Chart chart) {
		this.chart = chart;
	}

	public abstract void load();

	public abstract UnitType getUnitType();

	protected Unit<M> create(final int index, final M model) {
		switch (getUnitType()) {
		case LINE:
			return new LineUnit<>(index, model, this);
		case BAR:
			return new BarUnit<>(index, model, this);
		case AREA:
			return new AreaUnit<>(index, model, this);
		}
		return null;
	}

	public void setDirty() {
		dirty = true;
		requestLayout();
	}

	public void update() {
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		final DateAxis dateAxis = chart.getChartView().getDateAxis();
		final int lowerBoundIndex = dateAxis.getLowerBoundIndex();
		final int upperBoundIndex = dateAxis.getUpperBoundIndex();
		for (int index = lowerBoundIndex; index >= upperBoundIndex; index--) {
			if (index >= 0 && index < units.size()) {
				final Unit<M> unit = units.get(index);
				if (null != unit) {
					min = Math.min(min, unit.getModel().getLow());
					max = Math.max(max, unit.getModel().getHigh());
				}
			}
		}
		updateChartValueBounds();
		setDirty();
	}

	protected void updateChartValueBounds() {
		chart.setMin(Math.min(chart.getMin(), min));
		chart.setMax(Math.max(chart.getMax(), max));
	}

	protected void preLayout() {
		lastMinIndex = Math.max(lastMinIndex, 0);
		lastMaxIndex = Math.min(lastMaxIndex, units.size() - 1);
		final PlotNode plotNode = plotNodeProperty.get();
		if (null != plotNode) {
			plotNode.preLayout();
		}
	}

	protected void postLayout() {
		final PlotNode plotNode = plotNodeProperty.get();
		if (null != plotNode) {
			plotNode.postLayout();
		}
	}

	protected void layoutUnit(final Unit<M> unit, final double x, final double width) {
		unit.layoutChartChildren(x, width);
	}

	@Override
	protected final void layoutChildren() {
		if (dirty) {
			dirty = false;
			final DateAxis dateAxis = chart.getChartView().getDateAxis();
			final int minIndex = dateAxis.getUpperBoundIndex();
			final int maxIndex = dateAxis.getLowerBoundIndex();
			final double width = dateAxis.getUnitWidth();
			preLayout();
			for (int index = lastMaxIndex; index >= lastMinIndex; index--) {
				final Unit<M> unit = units.get(index);
				if (null != unit) {
					if (index <= maxIndex && index >= minIndex) {
						unit.setVisible(true);
						final double x = dateAxis.getDisplayPosition(index);
						layoutUnit(unit, x, width);
					} else {
						unit.setVisible(false);
					}
				}
			}
			lastMinIndex = minIndex;
			lastMaxIndex = maxIndex;
			postLayout();
		}
	}

}

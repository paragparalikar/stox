package com.stox.chart.plot;

import java.util.stream.IntStream;

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
import com.stox.chart.widget.PlotInfoPanel;
import com.stox.core.intf.HasName;
import com.stox.core.intf.Range;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude= {"chart","units","models"})
public abstract class Plot<M extends Range> extends Group implements HasName {

	private Chart chart;
	private boolean dirty;
	private Color color = Color.GRAY;
	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	private int lastMinIndex = Integer.MIN_VALUE, lastMaxIndex = Integer.MAX_VALUE;
	private final PlotInfoPanel<M> plotInfoPane;
	private final ObjectProperty<PlotNode> plotNodeProperty = new SimpleObjectProperty<>();
	private final ObservableList<M> models = FXCollections.observableArrayList();
	private final ObservableList<Unit<M>> units = FXCollections.observableArrayList();

	public Plot(final Chart chart) {
		setChart(chart);
		plotInfoPane = createPlotInfoPanel();
		plotNodeProperty.addListener((observable, old, node) -> {
			getChildren().remove(old);
			if (null != node) {
				getChildren().add((Node) node);
			}
		});
		models.addListener((ListChangeListener<M>) (change) -> {
			if(Platform.isFxApplicationThread()) {
				onModelsChanged(change);
			}else {
				Platform.runLater(() -> {
					onModelsChanged(change);
				});
			}
		});
	}
	
	public void setColor(final Color color) {
		this.color = color;
		plotInfoPane.setColor(color);
		units.forEach(Unit::update);
	}
	
	protected void onModelsChanged(final Change<? extends M> change) {
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
	}
	
	protected PlotInfoPanel<M> createPlotInfoPanel() {
		return new PlotInfoPanel<M>(this);
	}

	public void clearUnits() {
		units.clear();
		getChildren().clear();
	}

	public void createUnits(final int from, final int to) {
		final UnitType unitType = getUnitType();
		if(null != unitType) {
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
		}
		
		IntStream.range(from, to).forEach(index -> {
			final M model = models.get(index);
			final Unit<M> unit = create(index, model);
			units.add(index, unit);
			getChildren().add(unit);
		});
	}

	public Unit<M> getUnitAt(final double screenX, final double screenY) {
		final DateAxis dateAxis = getChart().getChartView().getDateAxis();
		final int index = dateAxis.getIndexForDisplay(dateAxis.screenToLocal(screenX, screenY).getX());
		if (index >= 0 && index < units.size()) {
			return units.get(index);
		}
		return null;
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
		default:
			return null;
		}
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
			if (index >= 0 && index < models.size()) {
				final M model = models.get(index);
				if (null != model) {
					min = Math.min(min, model.getLow());
					max = Math.max(max, model.getHigh());
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
	protected void layoutChildren() {
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
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}

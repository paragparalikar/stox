package com.stox.chart.view;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.stox.chart.axis.DateAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.plot.VolumePlot;
import com.stox.chart.unit.UnitType;
import com.stox.chart.util.ChartConstant;
import com.stox.chart.widget.Crosshair;
import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasDate;
import com.stox.core.intf.HasInstrument;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.View;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import lombok.Data;

@Data
public class ChartView extends View implements HasInstrument, HasBarSpan, HasDate {

	private boolean semilog = false;
	private BarSpan barSpan = BarSpan.D;
	private UnitType unitType = UnitType.CANDLE;
	private Date to;
	private Date from;
	private Date date; // bar date at which right mouse click happened

	/* configurable properties, these should go into a different object */
	private Color upBarColor = Color.GREEN;
	private Color downBarColor = Color.RED;
	private final List<Color> plotColors = Arrays.asList(Color.BLACK, Color.BLUE, Color.GREEN, Color.BROWN, Color.AQUA, Color.BLUEVIOLET, Color.CADETBLUE);

	private final SplitPane splitPane;
	private final VolumePlot volumePlot;
	private final PrimaryChart primaryChart;
	private final DateAxis dateAxis;
	private final BorderPane content;
	private final Crosshair crosshair;
	private final ContextMenu contextMenu;
	private final ObservableList<Chart> charts = FXCollections.observableArrayList();

	private MouseHandler mouseHandler;
	private final MouseHandler defaultMouseHandler = new ChartViewPanMouseHandler(this);

	public ChartView() {
		super(ChartConstant.CODE, ChartConstant.NAME, ChartConstant.ICON);
		splitPane = UiUtil.classes(new SplitPane(), "transparent");
		primaryChart = new PrimaryChart(this);
		volumePlot = new VolumePlot(primaryChart);
		dateAxis = new DateAxis(this);
		splitPane.setOrientation(Orientation.VERTICAL);
		content = new BorderPane(splitPane, null, null, dateAxis, null);
		crosshair = new Crosshair(this);
		contextMenu = new ContextMenu();

		UiUtil.classes(this, "chart-view");
		add(content);
		setMouseHandler(defaultMouseHandler);
		getChildren().addAll(crosshair);
		splitPane.getItems().add(primaryChart);
		primaryChart.getPlots().add(volumePlot);
		
		charts.addListener(new ListChangeListener<Chart>() {
            @Override
            public void onChanged(
                final javafx.collections.ListChangeListener.Change<? extends Chart> change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        splitPane.getItems().addAll(change.getAddedSubList());
                    }
                    if (change.wasRemoved()) {
                        splitPane.getItems().removeAll(change.getRemoved());
                    }
                    final int size = charts.size();
                    for (int index = 0; index < size; index++) {
                        splitPane.setDividerPosition(index, 1 - ((size - index) * 0.2));
                    }
                }
                requestLayout();
            }
        });
	}

	public void setDirty() {
		primaryChart.setDirty();
		charts.forEach(Chart::setDirty);
	}

	public void update() {
		primaryChart.update();
		charts.forEach(Chart::update);
	}

	public void setMouseHandler(MouseHandler mouseHandler) {
		if (null != this.mouseHandler) {
			this.mouseHandler.detach();
		}
		if (null == mouseHandler) {
			this.mouseHandler = defaultMouseHandler;
			defaultMouseHandler.attach();
		} else {
			this.mouseHandler = mouseHandler;
			mouseHandler.attach();
		}
	}

	public Chart getChartAt(final double screenX, final double screenY) {
		if (primaryChart.contains(primaryChart.screenToLocal(screenX, screenY))) {
			return primaryChart;
		}
		for (final Chart chart : charts) {
			if (chart.contains(chart.screenToLocal(screenX, screenY))) {
				return chart;
			}
		}
		return null;
	}

	@Override
	public Instrument getInstrument() {
		return primaryChart.getPrimaryPricePlot().getInstrument();
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

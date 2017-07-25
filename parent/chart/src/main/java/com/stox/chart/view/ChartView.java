package com.stox.chart.view;

import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.ChartViewPanMouseHandler;
import com.stox.chart.MouseHandler;
import com.stox.chart.axis.DateAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.plot.VolumePlot;
import com.stox.chart.util.ChartConstant;
import com.stox.core.model.BarSpan;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.View;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "primaryChart", "charts", "volumePlot", "dateAxis", "content" })
public class ChartView extends View {

	private boolean semilog = false;
	private BarSpan barSpan = BarSpan.D;
	private Date to;
	private Date from;

	/* configurable properties, these should go into a different object */
	private Color upBarColor = Color.GREEN;
	private Color downBarColor = Color.RED;

	private final PrimaryChart primaryChart = new PrimaryChart(this);
	private final VolumePlot volumePlot = new VolumePlot(primaryChart);
	private final SplitPane splitPane = UiUtil.classes(new SplitPane(primaryChart), "transparent");
	private final DateAxis dateAxis = new DateAxis(this);
	private final BorderPane content = new BorderPane(splitPane, null, null, dateAxis, null);
	private final ObservableList<Chart> charts = FXCollections.observableArrayList();

	private MouseHandler mouseHandler;
	private final MouseHandler defaultMouseHandler = new ChartViewPanMouseHandler(this);

	public ChartView() {
		super(ChartConstant.CODE, ChartConstant.NAME, ChartConstant.ICON);
		setContent(content);
		setMouseHandler(defaultMouseHandler);
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

}

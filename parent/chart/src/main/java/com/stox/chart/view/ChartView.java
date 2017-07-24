package com.stox.chart.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.stox.chart.axis.DateAxis;
import com.stox.chart.chart.Chart;
import com.stox.chart.chart.PrimaryChart;
import com.stox.chart.util.ChartConstant;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.View;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "primaryChart", "charts" })
public class ChartView extends View {

	private boolean semilog = false;

	private final PrimaryChart primaryChart = new PrimaryChart(this);
	private final SplitPane splitPane = UiUtil.classes(new SplitPane(primaryChart), "transparent");
	private final DateAxis dateAxis = new DateAxis(this);
	private final BorderPane content = new BorderPane(splitPane, null, null, dateAxis, null);
	private final ObservableList<Chart> charts = FXCollections.observableArrayList();

	public ChartView() {
		super(ChartConstant.CODE, ChartConstant.NAME, ChartConstant.ICON);
		setContent(content);
	}

}

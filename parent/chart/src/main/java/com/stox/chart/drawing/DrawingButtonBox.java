package com.stox.chart.drawing;

import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import org.springframework.stereotype.Component;

import com.stox.chart.view.ChartView;
import com.stox.chart.widget.ChartingTool;

@Component
public class DrawingButtonBox extends ChartingTool {

	private final VerticalLineToggleButton verticalLineToggleButton;
	private final HorizontalLineToggleButton horizontalLineToggleButton;
	private final VerticalSegmentToggleButton verticalSegmentToggleButton;
	private final HorizontalSegmentToggleButton horizontalSegmentToggleButton;
	private final SegmentToggleButton segmentToggleButton;
	private final ClearDrawingsButton clearDrawingsButton;
	private final LevelToggleButton levelToggleButton;
	private final HBox container = new HBox();
	private final ToggleGroup toggleGroup = new ToggleGroup();

	public DrawingButtonBox() {
		verticalLineToggleButton = new VerticalLineToggleButton(this);
		verticalLineToggleButton.setToggleGroup(toggleGroup);
		horizontalLineToggleButton = new HorizontalLineToggleButton(this);
		horizontalLineToggleButton.setToggleGroup(toggleGroup);
		verticalSegmentToggleButton = new VerticalSegmentToggleButton(this);
		verticalSegmentToggleButton.setToggleGroup(toggleGroup);
		horizontalSegmentToggleButton = new HorizontalSegmentToggleButton(this);
		horizontalSegmentToggleButton.setToggleGroup(toggleGroup);
		segmentToggleButton = new SegmentToggleButton(this);
		segmentToggleButton.setToggleGroup(toggleGroup);
		levelToggleButton = new LevelToggleButton(this);
		levelToggleButton.setToggleGroup(toggleGroup);
		clearDrawingsButton = new ClearDrawingsButton(this);
		container.getChildren().addAll(verticalLineToggleButton, horizontalLineToggleButton, verticalSegmentToggleButton, horizontalSegmentToggleButton, segmentToggleButton,
				levelToggleButton, clearDrawingsButton);
	}

	@Override
	public void onChartViewSelected(ChartView chartView) {

	}

	@Override
	public Node getNode() {
		return container;
	}

}

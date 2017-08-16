package com.stox.chart.drawing;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.util.Callback;

import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.chart.view.MouseHandler;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.UiUtil;

public class VerticalSegmentToggleButton extends SegmentToggleButton {

	public VerticalSegmentToggleButton(final ChartingTool chartingButtonBox) {
		super(chartingButtonBox);
		setGraphic(new Group(new Line(8, 2, 8, 14)));
		UiUtil.classes(this, "icon");
	}

	@Override
	protected MouseHandler createMouseHandler(ChartView chartView) {
		return new VerticalSegmentMouseHandler(chartView, this);
	}

}

class VerticalSegmentMouseHandler extends SegmentMouseHandler {

	public VerticalSegmentMouseHandler(ChartView chartView, final Callback<Segment, Void> onFinishCallback) {
		super(chartView, onFinishCallback);
	}

	@Override
	protected Segment createSegment(Chart chart) {
		return new VerticalSegment(chart);
	}

}

class VerticalSegment extends Segment {

	public VerticalSegment(Chart chart) {
		super(chart);
		one.centerXProperty().bindBidirectional(two.centerXProperty());
	}

	@Override
	protected void move(double xDelta, double yDelta) {
		one.setCenterY(one.getCenterY() + yDelta);
		two.setCenterX(two.getCenterX() + xDelta);
		two.setCenterY(two.getCenterY() + yDelta);
	}

}
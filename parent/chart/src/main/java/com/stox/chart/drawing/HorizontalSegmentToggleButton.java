package com.stox.chart.drawing;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.util.Callback;

import com.stox.chart.chart.Chart;
import com.stox.chart.view.ChartView;
import com.stox.chart.view.MouseHandler;
import com.stox.chart.widget.ChartingTool;
import com.stox.core.ui.util.UiUtil;

public class HorizontalSegmentToggleButton extends SegmentToggleButton {

	public HorizontalSegmentToggleButton(final ChartingTool chartingButtonBox) {
		super(chartingButtonBox);
		setGraphic(new Group(new Line(2, 8, 14, 8)));
		UiUtil.classes(this, "icon");
	}

	@Override
	protected MouseHandler createMouseHandler(ChartView chartView) {
		return new HorizontalSegmentMouseHandler(chartView, this);
	}

}

class HorizontalSegmentMouseHandler extends SegmentMouseHandler {

	public HorizontalSegmentMouseHandler(ChartView chartView, final Callback<Segment, Void> onFinishCallback) {
		super(chartView, onFinishCallback);
	}

	@Override
	protected Segment createSegment(Chart chart) {
		return new HorizontalSegment(chart);
	}

}

class HorizontalSegment extends Segment {

	public HorizontalSegment(Chart chart) {
		super(chart);
		one.centerYProperty().bindBidirectional(two.centerYProperty());
	}

	@Override
	protected void move(double xDelta, double yDelta) {
		one.setCenterX(one.getCenterX() + xDelta);
		two.setCenterX(two.getCenterX() + xDelta);
		two.setCenterY(two.getCenterY() + yDelta);
	}

}

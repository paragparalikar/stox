package com.stox.chart.drawing;

import com.stox.chart.chart.Chart;

public class DrawingFactory {

	public Drawing<?> create(final String code, final Chart chart) {
		switch (code) {
		case Segment.CODE:
			return new Segment(chart);
		case HorizontalLine.CODE:
			return new HorizontalLine(chart);
		case VerticalLine.CODE:
			return new VerticalLine(chart);
		case Level.CODE:
			return new Level(chart);
		default:
			return null;
		}
	}

}

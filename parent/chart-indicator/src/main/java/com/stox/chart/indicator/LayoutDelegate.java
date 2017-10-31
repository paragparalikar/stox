package com.stox.chart.indicator;

import com.stox.core.intf.Range;

public interface LayoutDelegate<V extends Range> {

	void layout(IndicatorPlot<V> plot);
	
}

package com.stox.chart.indicator;

import com.stox.chart.plot.Plot;
import com.stox.chart.unit.Unit;
import com.stox.core.intf.Range;

public interface UnitFactory<M extends Range> {

	Unit<M> create(int index, M model, Plot<M> plot);
	
}

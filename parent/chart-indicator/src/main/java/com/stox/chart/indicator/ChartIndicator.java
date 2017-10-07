package com.stox.chart.indicator;

import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range;
import com.stox.indicator.Indicator;

public interface ChartIndicator<T, V extends Range> extends Indicator<T,V>{

	Underlay getUnderlay(T config);
	
	UnitType getUnitType(T config);
	
}

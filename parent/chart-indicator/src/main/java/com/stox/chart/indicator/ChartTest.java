package com.stox.chart.indicator;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range.DoubleRange;
import com.stox.indicator.Test;
import com.stox.indicator.Test.Config;

@Lazy
@Component
public class ChartTest extends Test implements ChartIndicator<Config, DoubleRange> {

	@Override
	public Underlay getUnderlay(Config config) {
		return Underlay.NONE;
	}

	@Override
	public UnitType getUnitType(Config config) {
		return UnitType.BAR;
	}

	@Override
	public Style getStyle() {
		return null;
	}

}

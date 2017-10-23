package com.stox.chart.indicator;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range.DoubleRange;
import com.stox.indicator.RelativeStrengthIndex;
import com.stox.indicator.RelativeStrengthIndex.Config;

@Lazy
@Component
public class ChartRelativeStrengthIndex extends RelativeStrengthIndex implements ChartIndicator<Config, DoubleRange>{

	@Override
	public Underlay getUnderlay(Config config) {
		return Underlay.NONE;
	}

	@Override
	public UnitType getUnitType(Config config) {
		return UnitType.LINE;
	}

}

package com.stox.chart.indicator;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.chart.plot.Underlay;
import com.stox.chart.unit.UnitType;
import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.BarValueType;
import com.stox.indicator.SimpleMovingAverage;
import com.stox.indicator.SimpleMovingAverage.Config;

@Lazy
@Component
public class ChartSimpleMovingAverage extends SimpleMovingAverage implements ChartIndicator<Config, DoubleRange> {

	@Override
	public Underlay getUnderlay(Config config) {
		return BarValueType.VOLUME.equals(config.getBarValueType()) ? Underlay.VOLUME : Underlay.PRICE;
	}

	@Override
	public UnitType getUnitType(Config config) {
		return UnitType.LINE;
	}
	
	@Override
	public Style getStyle() {
		return null;
	}

}

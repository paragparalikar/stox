package com.stox.indicator;

import java.util.Arrays;
import java.util.List;

import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.Bar;
import com.stox.core.model.BarValueType;
import com.stox.core.model.Language;
import com.stox.indicator.SimpleMovingAverage.Config;

import lombok.Data;

public class SimpleMovingAverage implements Indicator<Config, DoubleRange> {
	
	@Data
	public static class Config{
		
		private int span = 14;
		
		private BarValueType barValueType = BarValueType.CLOSE;
		
		public String toString() {
			return String.valueOf(span)+","+barValueType.getName();
		}
		
	}

	@Override
	public String getId() {
		return "SMA";
	}

	@Override
	public String getName() {
		return "Simple Moving Average";
	}

	@Override
	public Language getLanguage() {
		return Language.JAVA;
	}

	@Override
	public Config buildDefaultConfig() {
		return new Config();
	}
	
	@Override
	public List<DoubleRange> compute(Config config, List<Bar> bars) {
		final DoubleRange[] array = new DoubleRange[bars.size()];
		double sum = 0;
		for(int index = bars.size() - 1; index >= 0; index--) {
			final Bar bar = bars.get(index);
			sum += bar.getValue(config.getBarValueType());
			if(index < bars.size() - config.getSpan()) {
				final Bar last = bars.get(index + config.getSpan());
				sum -= last.getValue(config.getBarValueType());
				array[index] = new DoubleRange(bar.getLow(), sum/config.getSpan(), bar.getHigh());
			}
		}
		return Arrays.asList(array);
	}

	@Override
	public DoubleRange computeSingle(Config config, List<Bar> bars) {
		double sum = 0;
		for(int index = 0; index < config.getSpan(); index++) {
			sum += bars.get(index).getValue(config.getBarValueType());
		}
		return new DoubleRange(sum/config.getSpan());
	}

}

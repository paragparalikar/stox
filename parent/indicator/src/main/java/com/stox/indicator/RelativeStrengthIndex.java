package com.stox.indicator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.Bar;
import com.stox.core.model.BarValueType;
import com.stox.core.model.Language;
import com.stox.indicator.RelativeStrengthIndex.Config;

import lombok.Data;

public class RelativeStrengthIndex implements Indicator<Config, DoubleRange> {

	@Data
	public static class Config {

		private int span = 14;

		private BarValueType barValueType = BarValueType.CLOSE;

		public String toString() {
			return "SMA(" + String.valueOf(span) + "," + (null == barValueType ? "" : barValueType.getName()) + ")";
		}

	}

	@Override
	public String getId() {
		return "RSI";
	}

	@Override
	public String getName() {
		return "Relative Strength Index";
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
		final int size = bars.size();
		final int span = config.getSpan();
		final Double[] values = new Double[size];
		final BarValueType barValueType = config.getBarValueType();
		for (int index = size - 2 - span; index >= 0; index--) {
			double gainSum = 0;
			double lossSum = 0;
			for (int i = index; i < index + span; i++) {
				final double change = barValueType.getValue(bars.get(i)) - barValueType.getValue(bars.get(i + 1));
				gainSum += Math.max(0, change);
				lossSum += Math.max(0, -change);
			}
			final double sum = gainSum + lossSum;
			values[index] = 0 == sum ? 50 : (100 * gainSum / sum);
		}
		return Arrays.stream(values).map(value -> null == value ? null : new DoubleRange(value))
				.collect(Collectors.toList());
	}

	@Override
	public DoubleRange computeSingle(Config config, List<Bar> bars) {
		double gainSum = 0;
		double lossSum = 0;
		final int span = config.getSpan();
		final BarValueType barValueType = config.getBarValueType();
		for (int i = 0; i < span; i++) {
			final double delta = barValueType.getValue(bars.get(i)) - barValueType.getValue(bars.get(i + 1));
			if (0 < delta) {
				gainSum += delta;
			} else if (0 > delta) {
				lossSum -= delta;
			}
		}
		return new DoubleRange(100 - (100 / (1 + (gainSum / lossSum))));
	}

}

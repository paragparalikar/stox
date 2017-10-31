package com.stox.indicator;

import java.util.Arrays;
import java.util.List;

import com.stox.core.intf.Range;
import com.stox.core.model.Bar;
import com.stox.core.model.BarValueType;
import com.stox.core.model.Language;
import com.stox.indicator.BollingerBands.Bollinger;
import com.stox.indicator.BollingerBands.Config;

import lombok.AllArgsConstructor;
import lombok.Data;

public class BollingerBands implements Indicator<Config, Bollinger>{
	
	@Data
	public static class Config{
		
		private int span = 14;
		
		private double multiple = 2;
		
		private BarValueType barValueType = BarValueType.CLOSE;
		
		public String toString() {
			return "BOL("+span+","+multiple+")";
		}
		
	}
	
	@Data
	@AllArgsConstructor
	public static class Bollinger implements Range{
		
		private double average;
		private double deviation;
		private double multiple;

		@Override
		public double getHigh() {
			return average + (deviation * multiple);
		}

		@Override
		public double getValue() {
			return average;
		}

		@Override
		public double getLow() {
			return average - (deviation * multiple);
		}
		
	}

	@Override
	public String getId() {
		return "BOL";
	}

	@Override
	public String getName() {
		return "Bollinger Bands";
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
	public List<Bollinger> compute(Config config, List<Bar> bars) {
		 final int size = bars.size();
	        final int span = config.getSpan();
	        final BarValueType barValueType = config.getBarValueType();
	        final Bollinger[] values = new Bollinger[size];

	        for (int index = size - 1 - span; index >= 0; index--) {
	            double sum = 0;
	            for (int i = index; i < (index + span); i++) {
	                sum += barValueType.getValue(bars.get(i));
	            }
	            final double average = sum / span;
	            double devSqSum = 0;
	            for (int i = index; i < (index + span); i++) {
	                devSqSum += Math.pow(average - barValueType.getValue(bars.get(i)), 2);
	            }
	            final double stdDev = Math.sqrt(devSqSum / span);
	            values[index] = new Bollinger(average, stdDev, config.getMultiple());
	        }
	        return Arrays.asList(values);
	}

	@Override
	public Bollinger computeSingle(Config config, List<Bar> bars) {
		final int span = config.getSpan();
		final BarValueType barValueType = config.getBarValueType();
		
        double sum = 0;
        for (int i = 0; i < span; i++) {
            sum += barValueType.getValue(bars.get(i));
        }
        final double average = sum / span;
        double devSqSum = 0;
        for (int i = 0; i < span; i++) {
            devSqSum += Math.pow(average - barValueType.getValue(bars.get(i)), 2);
        }
        final double stdDev = Math.sqrt(devSqSum / span);
        return new Bollinger(average, stdDev, config.getMultiple());
	}

}

package com.stox.indicator;

import java.util.ArrayList;
import java.util.List;

import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.Bar;
import com.stox.core.model.Language;
import com.stox.indicator.Test.Config;

public class Test implements Indicator<Config, DoubleRange>{
	
	public static class Config{
		
		@Override
		public String toString() {
			return "Test";
		}
		
	}

	@Override
	public String getId() {
		return "TEST";
	}

	@Override
	public String getName() {
		return "Test";
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
		final List<DoubleRange> result = new ArrayList<>();
		for(int index = 0; index < bars.size() - 3; index ++) {
			result.add(computeSingle(config, bars.subList(index, index + 3)));
		}
		return result;
	}

	@Override
	public DoubleRange computeSingle(Config config, List<Bar> bars) {
		if(3 <= bars.size()) {
			final Bar one = bars.get(0);
			final Bar two = bars.get(1);
			
			final double bull = one.getHigh() - two.getLow();
			final double bear = two.getHigh() - one.getLow();
			return new DoubleRange(bull - bear);
		}
		return null;
	}

}

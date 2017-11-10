package com.stox.screen;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.Bar;
import com.stox.core.model.Language;
import com.stox.indicator.RelativeStrengthIndex;
import com.stox.screen.BullishKeyReversalBarScreen.Config;

import lombok.Data;

@Lazy
@Component
public class BullishKeyReversalBarScreen implements Screen<Config>{

	private final RelativeStrengthIndex rsi = new RelativeStrengthIndex();
	private final RelativeStrengthIndex.Config rsiConfig = rsi.buildDefaultConfig();
	
	@Data
	public static class Config{
		
		private int span = 14;
		
		private int maximumRsi = 30;
		
		public String toString() {
			return "Bullish Key Reversal Bar";
		}
	}

	@Override
	public String getId() {
		return "BULLKRB";
	}

	@Override
	public String getName() {
		return "Bullish Key Reversal Bar";
	}

	@Override
	public int getMinBarCount(Config config) {
		return config.getSpan() + 1;
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
	public ScreenType getScreenType() {
		return ScreenType.BULLISH;
	}

	@Override
	public boolean isMatch(Config config, List<Bar> bars) {
		rsiConfig.setSpan(config.getSpan());
		final DoubleRange doubleRange = rsi.computeSingle(rsiConfig, bars);
		if(null != doubleRange && config.getMaximumRsi() >= doubleRange.getValue() && 2 <= bars.size()) {
			final Bar bar = bars.get(0);
			final Bar previous = bars.get(1);
			
			// Bar closes in top 1/3rd of the range
			if(bar.getClose() < bar.getLow() + (bar.getHigh() - bar.getLow())*2/3) {
				return false;
			}
			
			//Bar's low is lower than previous bar's low
			if(bar.getLow() >= previous.getLow()) {
				return false;
			}
			
			//Bar's closes above previous bar's close
			if(bar.getClose() <= previous.getClose()) {
				return false;
			}
			
			//Previous bar closes in lower 1/3rd of range
			if(previous.getClose() > previous.getLow() + (previous.getHigh() - previous.getLow())/3) {
				return false;
			}
			
			return true;
		}
		return false;
	}
	
}

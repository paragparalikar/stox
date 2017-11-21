package com.stox.screen;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Range.DoubleRange;
import com.stox.core.model.Bar;
import com.stox.core.model.Language;
import com.stox.core.model.Swing;
import com.stox.indicator.RelativeStrengthIndex;
import com.stox.screen.BullishDivergenceScreen.Config;

import lombok.Data;

@Lazy
@Component
public class BullishDivergenceScreen implements Screen<Config>{
	
	private final RelativeStrengthIndex rsi = new RelativeStrengthIndex();
	
	@Data
	public static class Config{
		
		private double tolerance = 2;
		
		private int maximumStartRsi = 40;
		
		private int minimumEndRsi = 45;
		
		public String toString() {
			return "Bullish Divergence";
		}
	}

	@Override
	public String getId() {
		return "BULLDI";
	}

	@Override
	public String getName() {
		return "Bullish Divergence";
	}

	@Override
	public int getMinBarCount(Config config) {
		return (int)config.getTolerance()*20;
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
		final List<Swing> swings = Swing.parse(bars, config.getTolerance(), 3);
		if(3 <= swings.size()) {
			final Swing current = swings.get(0);
			final Swing previous = swings.get(1);
			final Swing three = swings.get(2);
			
			if(current.isUp()) {
				return false;
			}
			
			if(3 > current.getBars().size()) {
				return false;
			}
			
			if(previous.getEnd().getClose() - previous.getStart().getClose() > (three.getStart().getClose() - three.getEnd().getClose())/2) {
				return false;
			}
			
			if(current.getBars().size() < previous.getBars().size()) {
				return false;
			}
			
			if(current.getEnd().getLow() > previous.getStart().getClose()) {
				return false;
			}
			
			final int startIndex = bars.indexOf(previous.getStart());
			final int rsiSpan = current.getSpan()+previous.getSpan();
			if(bars.size() <= startIndex + rsiSpan + 1) {
				return false;
			}
			
			
			final RelativeStrengthIndex.Config rsiConfig = rsi.buildDefaultConfig();
			rsiConfig.setSpan(rsiSpan);
			final DoubleRange startRsiRange = rsi.computeSingle(rsiConfig, bars.subList(startIndex, bars.size()));
			final double startRsi = startRsiRange.getValue();
			final DoubleRange endRsiRange = rsi.computeSingle(rsiConfig, bars);
			final double endRsi = endRsiRange.getValue();
			
			if(endRsi < startRsi) {
				return false;
			}
			
			return true;
		}
		return false;
	}
	
}

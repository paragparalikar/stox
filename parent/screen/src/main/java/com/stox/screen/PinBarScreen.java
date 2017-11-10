package com.stox.screen;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.Language;
import com.stox.screen.PinBarScreen.Config;

import lombok.Data;

@Lazy
@Component
public class PinBarScreen implements Screen<Config> {

	@Data
	public static class Config{
		
		public String toString() {
			return "PinBar";
		}
	}

	@Override
	public String getId() {
		return "PinBar";
	}

	@Override
	public String getName() {
		return "Pin Bar";
	}

	@Override
	public int getMinBarCount(Config config) {
		return 2;
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
		final Bar bar = bars.get(0);
		if(isMatch(bar)) {
			return true;
		}
		
		final Bar combined = new Bar();
		final Bar previous = bars.get(1);
		combined.setOpen(previous.getOpen());
		combined.setHigh(Math.max(bar.getHigh(), previous.getHigh()));
		combined.setLow(Math.min(bar.getLow(), previous.getLow()));
		combined.setClose(bar.getClose());
		
		return isMatch(combined);
	}
	
	private boolean isMatch(final Bar bar) {
		final double mark = bar.getHigh() - (bar.getHigh() - bar.getLow())/3;
		return bar.getOpen() > mark && bar.getClose() > mark;
	}
	
	private double upperWick(final Bar bar) {
		return bar.getHigh() - Math.max(bar.getOpen(), bar.getClose());
	}
	
	private double body(final Bar bar) {
		return Math.abs(bar.getOpen() - bar.getClose());
	}
	
	private double lowerWick(final Bar bar) {
		return Math.min(bar.getOpen(), bar.getClose()) - bar.getLow();
	}
	
}

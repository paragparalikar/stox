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
		return 1;
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
		return bar.getClose() > (bar.getHigh() + bar.getLow())/2;
	}
	
	
}

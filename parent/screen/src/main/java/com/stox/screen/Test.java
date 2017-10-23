package com.stox.screen;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.Language;
import com.stox.screen.Test.Config;

import lombok.Data;

@Lazy
@Component
public class Test implements Screen<Config> {

	@Data
	public static class Config{
		
		private int span = 14;
		
		public String toString() {
			return "Test("+span+")";
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
	public int getMinBarCount(Config config) {
		return config.getSpan();
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
		return ScreenType.BEARISH;
	}

	@Override
	public boolean isMatch(Config config, List<Bar> bars) {
		final Bar bar = bars.get(0);
		return bar.getClose() > (bar.getHigh() + bar.getLow())/2;
	}
	
	
}

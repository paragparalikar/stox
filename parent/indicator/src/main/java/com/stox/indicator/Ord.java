package com.stox.indicator;

import java.util.List;

import com.stox.core.model.Bar;
import com.stox.core.model.Language;
import com.stox.core.model.Swing;
import com.stox.indicator.Ord.Config;

import lombok.Data;

public class Ord implements Indicator<Config, Swing>{

	@Data
	public static class Config{
		
		private double tolerance = 5;
		
	}

	@Override
	public String getId() {
		return "ORD";
	}

	@Override
	public String getName() {
		return "Ord";
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
	public List<Swing> compute(Config config, List<Bar> bars) {
		return Swing.parse(bars, config.getTolerance());
	}

	@Override
	public Swing computeSingle(Config config, List<Bar> bars) {
		final List<Swing> swings = Swing.parse(bars, config.getTolerance());
		return null == swings || swings.isEmpty() ? null : swings.get(0);
	}
	
}

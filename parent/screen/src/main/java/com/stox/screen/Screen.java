package com.stox.screen;

import java.util.List;

import com.stox.core.intf.HasId;
import com.stox.core.intf.HasName;
import com.stox.core.model.Bar;
import com.stox.core.model.Language;

public interface Screen<T> extends HasId<String>, HasName{
	
	int getMinBarCount(T config);

	Language getLanguage();

	T buildDefaultConfig();
	
	ScreenType getScreenType();
	
	public boolean isMatch(final T config, final List<Bar> bars);

}

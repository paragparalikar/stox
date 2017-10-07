package com.stox.indicator;

import java.util.List;

import com.stox.core.intf.HasId;
import com.stox.core.intf.HasName;
import com.stox.core.intf.Range;
import com.stox.core.model.Bar;
import com.stox.core.model.Language;

public interface Indicator<T, V extends Range> extends HasId<String>, HasName{

	Language getLanguage();
	
	T buildDefaultConfig();
	
	List<V> compute(final T config, final List<Bar> bars);
	
	V computeSingle(final T config, final List<Bar> bars);
	
}

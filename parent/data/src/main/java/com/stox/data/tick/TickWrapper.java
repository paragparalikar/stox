package com.stox.data.tick;

import java.util.List;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasInstrument;
import com.stox.core.model.Bar;

public interface TickWrapper extends HasInstrument, HasBarSpan{

	Object getTick();
	
	/**
	 * @param bars
	 * @return boolean - true when a new bar is added, 
	 * false if bar is merged with provided data without changing
	 * the size of list
	 */
	boolean mergeWith(final List<Bar> bars);
	
}

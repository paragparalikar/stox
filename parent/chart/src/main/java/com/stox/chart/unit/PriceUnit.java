package com.stox.chart.unit;

import com.stox.chart.plot.Plot;
import com.stox.core.model.Bar;

public abstract class PriceUnit extends Unit<Bar> {

	public PriceUnit(final int index, final Bar model, final Plot<Bar> plot) {
		super(index, model, plot);
	}

}

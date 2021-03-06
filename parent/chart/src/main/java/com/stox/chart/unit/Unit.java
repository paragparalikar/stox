package com.stox.chart.unit;

import com.stox.chart.plot.Plot;
import com.stox.core.intf.Range;

import javafx.scene.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude= {"plot"})
@EqualsAndHashCode(callSuper = true, exclude = { "plot", "model" })
public abstract class Unit<M extends Range> extends Group {

	private final int index;
	private final M model;
	private final Plot<M> plot;

	public Unit(final int index, final M model, final Plot<M> plot) {
		this.plot = plot;
		this.model = model;
		this.index = index;
		update();
	}

	/**
	 * This method should be overridden to update styles like color and other plot level stuff
	 * in units
	 */
	public void update() {
		
	}
	
	@Override
	protected final void layoutChildren() {

	}

	public abstract void layoutChartChildren(final double x, final double width);

	public double getDisplayPosition(final double value) {
		return plot.getChart().getValueAxis().getDisplayPosition(value, plot.getMin(), plot.getMax());
	}

}

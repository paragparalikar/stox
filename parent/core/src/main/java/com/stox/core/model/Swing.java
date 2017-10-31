package com.stox.core.model;

import java.util.ArrayList;
import java.util.List;

import com.stox.core.intf.Range;

import lombok.Data;

@Data
public class Swing implements Range {

	public static List<Integer> parseIndices(final List<Bar> bars, final double percentage) {
		final List<Integer> indices = new ArrayList<Integer>();
		int pivot = bars.size() - 1;
		double pivotValue = bars.get(pivot).getClose(), endValue = pivotValue;
		double upperLimit = pivotValue * (1 + (percentage / 100));
		double lowerLimit = pivotValue * (1 - (percentage / 100));
		for (int index = pivot; index >= 0; index--) {
			final double value = bars.get(index).getClose();
			if (pivotValue == endValue) {
				if ((value > upperLimit) || (value < lowerLimit)) {
					pivot = index;
					pivotValue = value;
					upperLimit = pivotValue * (1 + (percentage / 100));
					lowerLimit = pivotValue * (1 - (percentage / 100));
				}
			} else if (pivotValue > endValue) {
				if (value > pivotValue) {
					pivot = index;
					pivotValue = value;
					lowerLimit = pivotValue * (1 - (percentage / 100));
				} else if (value < lowerLimit) {
					indices.add(pivot);
					endValue = pivotValue;
					pivot = index;
					pivotValue = value;
					upperLimit = pivotValue * (1 + (percentage / 100));
				}
			} else {
				if (value < pivotValue) {
					pivot = index;
					pivotValue = value;
					upperLimit = pivotValue * (1 + (percentage / 100));
				} else if (value > upperLimit) {
					indices.add(pivot);
					endValue = pivotValue;
					pivot = index;
					pivotValue = value;
					lowerLimit = pivotValue * (1 - (percentage / 100));
				}
			}
		}
		if (!indices.contains(0)) {
			indices.add(0);
		}
		return indices;
	}

	public static List<Swing> parse(final List<Bar> bars, final double percentage) {
		final List<Integer> indices = Swing.parseIndices(bars, percentage);
		final List<Swing> swings = new ArrayList<Swing>();
		if (!indices.isEmpty()) {
			if (1 == indices.size()) {
				swings.add(new Swing(bars));
			} else {
				for (int i = 0; i < (indices.size() - 1); i++) {
					swings.add(new Swing(bars.subList(indices.get(i + 1), indices.get(i) + 1)));
				}
			}
		}
		return swings;
	}

	private Bar start;
	private Bar end;
	private int span;
	private double volume;

	public Swing(final List<Bar> bars) {
		for (final Bar bar : bars) {
			if (null == end) {
				end = bar;
			}
			start = bar;
			span += 1;
			volume += bar.getVolume();
		}
	}

	public boolean isUp() {
		return start.getClose() < end.getClose();
	}

	public boolean isDown() {
		return start.getClose() >= end.getClose();
	}

	@Override
	public double getHigh() {
		return Math.max(start.getHigh(), end.getHigh());
	}

	@Override
	public double getValue() {
		return 0;
	}

	@Override
	public double getLow() {
		return Math.min(start.getLow(), end.getLow());
	}

}

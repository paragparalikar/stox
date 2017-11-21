package com.stox.core.model;

import java.util.ArrayList;
import java.util.List;

import com.stox.core.intf.Range;

import lombok.Data;

@Data
public class Swing implements Range {

	public static List<Swing> parse(final List<Bar> bars, final double percentage, final int count) {
		final List<Swing> swings = new ArrayList<>();
		boolean up = false;
		int lastIndex = 0;
		Bar pivot = bars.get(lastIndex);
		double previousValue = pivot.getClose();
		double max = pivot.getClose() * (1 + (percentage / 100));
		double min = pivot.getClose() * (1 - (percentage / 100));
		for (int index = 1; index < bars.size() && count > swings.size(); index++) {
			final double value = bars.get(index).getClose();

			if (up) {
				if (value > max) {
					swings.add(new Swing(bars.subList(lastIndex, index)));
					up = false;
					lastIndex = index;
					previousValue = value;
					min = value * (1 - (percentage / 100));
				} else if (value < previousValue) {
					previousValue = value;
					max = value * (1 + (percentage / 100));
				}
			} else {
				if (value < min) {
					swings.add(new Swing(bars.subList(lastIndex, index)));
					up = true;
					lastIndex = index;
					previousValue = value;
					max = value * (1 + (percentage / 100));
				} else if (value > previousValue) {
					min = value * (1 - (percentage / 100));
					previousValue = value;
				}
			}
		}
		return swings;
	}

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
	private List<Bar> bars;

	public Swing(final List<Bar> bars) {
		this.bars = bars;
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

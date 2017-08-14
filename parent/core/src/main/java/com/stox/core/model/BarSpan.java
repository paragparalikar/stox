package com.stox.core.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.stox.core.intf.HasName;

public enum BarSpan implements HasName {

	M(Calendar.MONTH, 1, "M", "Monthly"), W(Calendar.WEEK_OF_YEAR, 1, "W", "Weekly"), D(Calendar.DATE, 1, "D", "Daily"), H(Calendar.HOUR, 1, "H", "Hourly"), M30(Calendar.MINUTE,
			30, "M30", "30 Minutes"), M15(Calendar.MINUTE, 15, "M15", "15 Minutes"), M10(Calendar.MINUTE, 10, "M10", "10 Minutes"), M5(Calendar.MINUTE, 5, "M5", "5 Minutes"), M1(
			Calendar.MINUTE, 1, "M1", "1 Minute");

	private final int unit;
	private final int count;
	private final String name;
	private final String shortName;

	public static BarSpan getByShortName(final String shortName) {
		for (final BarSpan barSpan : values()) {
			if (shortName.equals(barSpan.getShortName())) {
				return barSpan;
			}
		}
		return null;
	}

	BarSpan(final int unit, final int count, final String shortName, final String name) {
		this.unit = unit;
		this.count = count;
		this.name = name;
		this.shortName = shortName;
	}

	public long getMillis() {
		switch (unit) {
		case Calendar.MINUTE:
			return 1000 * 60 * count;
		case Calendar.HOUR:
			return 1000 * 60 * 60 * count;
		case Calendar.DATE:
			return 1000 * 60 * 60 * 24 * count;
		case Calendar.WEEK_OF_YEAR:
			return 1000 * 60 * 60 * 24 * 7 * count;
		case Calendar.MONTH:
			return 1000 * 60 * 60 * 24 * 30 * count;
		}
		return 0;
	}

	public int getUnit() {
		return unit;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	/**
	 * This method assumes that provided bars are at lower frequency. If not the list will be returned as it is, no exception will be thrown.
	 *
	 * @param bars
	 *            lower frequency bars to be merged
	 * @return bars at the frequency of current {@link BarSpan}
	 */
	public List<Bar> merge(final List<Bar> bars) {
		if ((null == bars) || bars.isEmpty()) {
			return bars;
		}
		Collections.sort(bars);
		final Calendar calendar = Calendar.getInstance();
		final List<Bar> higherBars = new ArrayList<Bar>();
		int previousIndex = -1;
		Bar higherBar = null;

		for (final Bar bar : bars) {
			calendar.setTimeInMillis(bar.getDate().getTime());
			final int currentIndex = calendar.get(getUnit()) / getCount();
			if (previousIndex != currentIndex) {
				higherBar = new Bar();
				higherBar.setDate(bar.getDate());
				higherBar.setHigh(Double.MIN_VALUE);
				higherBar.setLow(Double.MAX_VALUE);
				higherBar.setClose(bar.getClose());
				higherBars.add(higherBar);
				previousIndex = currentIndex;
			}

			if (bar.getHigh() > higherBar.getHigh()) {
				higherBar.setHigh(bar.getHigh());
			}
			if (bar.getLow() < higherBar.getLow()) {
				higherBar.setLow(bar.getLow());
			}
			higherBar.setOpen(bar.getOpen());
			higherBar.setVolume(bar.getVolume() + higherBar.getVolume());
		}
		return higherBars;
	}

	private final Calendar calendar = Calendar.getInstance();

	public long next(final long date) {
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.add(unit, count);
			return calendar.getTimeInMillis();
		}
	}

	public long previous(final long date) {
		synchronized (calendar) {
			calendar.setTimeInMillis(date);
			calendar.add(unit, -1 * count);
			return calendar.getTimeInMillis();
		}
	}

}

package com.stox.chart.util;

import java.util.Calendar;
import java.util.Date;

import com.stox.core.model.BarSpan;

public class ChartUtil {

	public static Date getFrom(final Date to, final BarSpan barSpan) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(to);
		switch (barSpan) {
		default:
		case D:
			calendar.add(Calendar.YEAR, -1);
			return calendar.getTime();
		case W:
			calendar.add(Calendar.YEAR, -5);
			return calendar.getTime();
		case M:
			calendar.add(Calendar.YEAR, -25);
			return calendar.getTime();
		case H:
		case M30:
			calendar.add(Calendar.DATE, -10);
			return calendar.getTime();
		case M15:
		case M10:
			calendar.add(Calendar.DATE, -3);
			return calendar.getTime();
		case M5:
		case M1:
			calendar.add(Calendar.DATE, -1);
			return calendar.getTime();
		}
	}

}

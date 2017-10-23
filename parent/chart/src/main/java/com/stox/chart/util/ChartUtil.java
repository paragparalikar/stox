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
			calendar.add(Calendar.YEAR, -10);
			return calendar.getTime();
		case M:
			calendar.add(Calendar.YEAR, -25);
			return calendar.getTime();
		case H:
		case M30:
			calendar.add(Calendar.DATE, -30);
			return calendar.getTime();
		case M15:
		case M10:
		case M5:
			calendar.add(Calendar.DATE, -14);
			return calendar.getTime();
		case M1:
			calendar.add(Calendar.DATE, -7);
			return calendar.getTime();
		}
	}

}

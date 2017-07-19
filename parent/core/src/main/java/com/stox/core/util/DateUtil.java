package com.stox.core.util;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("deprecation")
public class DateUtil {

	public static boolean isMarketOpen() {
		return isMarketOpen(Calendar.getInstance());
	}

	public static boolean isMarketOpen(final Calendar now) {
		final Calendar from = Calendar.getInstance();
		from.set(Calendar.HOUR_OF_DAY, 9);
		from.set(Calendar.MINUTE, 15);
		final Calendar to = Calendar.getInstance();
		to.set(Calendar.HOUR_OF_DAY, 15);
		to.set(Calendar.MINUTE, 30);
		return !DateUtil.isWeekend(now) && now.after(from) && now.before(to);
	}

	public static void main(String[] args) {
		System.out.println(isMarketOpen());
	}

	public static boolean isWeekend(final Calendar calendar) {
		return (Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK)) || (Calendar.SATURDAY == calendar.get(Calendar.DAY_OF_WEEK));
	}

	public static boolean isWeekend() {
		return isWeekend(Calendar.getInstance());
	}

	public static boolean isToday(final Date date) {
		return DateUtil.equalsDate(date, new Date());
	}

	public static boolean equalsDate(final Date one, final Date two) {
		return (one.getYear() == two.getYear()) && (one.getMonth() == two.getMonth()) && (one.getDate() == two.getDate());
	}

	public static boolean isYesterday(final Date date) {
		synchronized (DateUtil.calendar) {
			DateUtil.calendar.setTimeInMillis(System.currentTimeMillis());
			DateUtil.calendar.add(Calendar.DATE, -1);
			final Date yesterday = DateUtil.calendar.getTime();
			return DateUtil.equalsDate(date, yesterday);
		}
	}

	public static boolean beforeTime(final Date date, final int hour, final int minute, final int second) {
		return hour == date.getHours() ? (minute == date.getMinutes() ? second > date.getSeconds() : minute > date.getMinutes()) : hour > date.getHours();
	}

	private static final Calendar calendar = Calendar.getInstance();

	public static void trim(final Date date) {
		synchronized (DateUtil.calendar) {
			DateUtil.calendar.setTime(date);
			DateUtil.calendar.set(Calendar.HOUR, 0);
			DateUtil.calendar.set(Calendar.MINUTE, 0);
			DateUtil.calendar.set(Calendar.SECOND, 0);
			DateUtil.calendar.set(Calendar.MILLISECOND, 0);
			date.setTime(DateUtil.calendar.getTimeInMillis());
		}
	}

}

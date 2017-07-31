package com.stox.chart.axis.helper;

import java.util.Date;

import javafx.scene.control.Label;

import com.stox.chart.axis.DateAxis;

public class DateAxisTickHelper {

	private static final int YEAR = 4;
	private static final int MONTH = 3;
	private static final int DATE = 2;
	private static final int HOUR = 1;

	private double lastLabelX = -1000;
	private final DateAxis dateAxis;
	private int lastTickLevel = HOUR;

	public DateAxisTickHelper(final DateAxis dateAxis) {
		this.dateAxis = dateAxis;
	}

	public void clear() {
		lastTickLevel = HOUR;
		lastLabelX = -1000;
		dateAxis.getChildren().clear();
	}

	public void addTick(final double x, final long date, final long previousDate) {
		if (x > 0) {
			final boolean hasSpace = (x > (lastLabelX + 20));
			final String text = getText(date, previousDate, hasSpace);
			if (null != text) {
				if (!hasSpace && !dateAxis.getChildren().isEmpty()) {
					dateAxis.getChildren().remove(dateAxis.getChildren().size() - 1);
				}

				final Label label = new Label(text);
				dateAxis.getChildren().add(label);
				label.resizeRelocate(x, 0, 50, 36);
				lastLabelX = x;
			}
		}
	}

	@SuppressWarnings({ "deprecation" })
	private String getText(final long date, final long previousDate, final boolean hasSpace) {
		String text = null;
		final Date current = new Date(date);
		final Date previous = new Date(previousDate);
		if (0 > lastLabelX || current.getYear() != previous.getYear()) {
			lastTickLevel = YEAR;
			text = String.valueOf(current.getYear() + 1900);
		} else if (current.getMonth() != previous.getMonth()) {
			if (hasSpace || lastTickLevel < MONTH) {
				lastTickLevel = MONTH;
				text = getText(current.getMonth());
			}
		} else if (current.getDate() != previous.getDate()) {
			if (hasSpace || lastTickLevel < DATE) {
				lastTickLevel = DATE;
				text = String.valueOf(current.getDate());
			}
		} else {
			if (hasSpace) {
				lastTickLevel = HOUR;
				text = current.getHours() + ":" + current.getMinutes();
			}
		}
		return text;
	}

	private String getText(int month) {
		switch (month) {
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "Mar";
		case 3:
			return "Apr";
		case 4:
			return "May";
		case 5:
			return "Jun";
		case 6:
			return "Jul";
		case 7:
			return "Aug";
		case 8:
			return "Sep";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		default:
			return "";
		}
	}
}

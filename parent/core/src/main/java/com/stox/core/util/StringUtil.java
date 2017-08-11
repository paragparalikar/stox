package com.stox.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringUtil {

	public static String trim(final String text) {
		return null == text ? null : text.trim();
	}

	public static String get(final String[] tokens, final int index) {
		if (null != tokens && index < tokens.length) {
			return tokens[index];
		}
		return null;
	}

	public static Integer parseInt(final String text) {
		if (StringUtil.hasText(text)) {
			try {
				return Integer.parseInt(text);
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	public static Long parseLong(final String text) {
		if (StringUtil.hasText(text)) {
			try {
				return Long.parseLong(text);
			} catch (NumberFormatException e) {

			}
		}
		return null;
	}

	public static Double parseDouble(final String text) {
		if (StringUtil.hasText(text)) {
			try {
				return Double.parseDouble(text);
			} catch (NumberFormatException e) {

			}
		}
		return null;

	}

	public static String nullIfEmpty(final String text) {
		return hasText(text) ? text : null;
	}

	public static String emptyIfNull(final String text) {
		return null == text ? "" : text;
	}

	public static boolean hasText(String text) {
		return (text != null) && (text.trim().length() > 0);
	}

	public static boolean equalsIgnoreCase(String one, String two) {
		return null == one && null == two ? true : (null == one || null == two ? false : (one.equalsIgnoreCase(two)));
	}

	public static int compareIgnoreCase(String one, String two) {
		return null == one && null == two ? 0 : (null == one ? -1 : (null == two ? 1 : one.compareToIgnoreCase(two)));
	}

	public static String stringValueOf(double value) {
		String text = "";
		if (value >= 1000000000) {
			text = "B";
			value /= 1000000000;
		} else if (value >= 1000000) {
			text = "M";
			value /= 1000000;
		} else if (value >= 1000) {
			text = "K";
			value /= 1000;
		}
		return String.valueOf(Constant.currencyFormat.format(value) + text);
	}

	public static String toString(final InputStream inStream) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
		final byte[] buffer = new byte[100 * 1024];
		int nrBytesRead = 0;
		while ((nrBytesRead = inStream.read(buffer)) > 0) {
			baos.write(buffer, 0, nrBytesRead);
		}
		inStream.close();
		return new String(baos.toByteArray());
	}

}

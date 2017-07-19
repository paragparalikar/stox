package com.stox.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

public class StringUtil {

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

	public static String toString(final InputStream inputStream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		final StringBuilder sb = new StringBuilder();
		while (null != (line = reader.readLine())) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static String toString(final ZipInputStream inStream) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(102400);
		final byte[] buffer = new byte[100 * 1024];
		if (inStream.getNextEntry() != null) {
			int nrBytesRead = 0;
			while ((nrBytesRead = inStream.read(buffer)) > 0) {
				baos.write(buffer, 0, nrBytesRead);
			}
		}
		inStream.close();
		return new String(baos.toByteArray());
	}
}

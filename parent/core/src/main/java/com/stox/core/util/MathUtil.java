package com.stox.core.util;

public class MathUtil {

	public static double praportion(final double mina, final double a, final double maxa, final double minb, final double maxb) {
		return minb + (((a - mina) * (maxb - minb)) / (maxa - mina));
	}

	public static double ceil(double value, double boxSize) {
		return Math.ceil(value / boxSize) * boxSize;
	}

	public static double floor(double value, double boxSize) {
		return Math.floor(value / boxSize) * boxSize;
	}

	public static int limit(int min, int value, int max) {
		return Math.max(min, Math.min(max, value));
	}

}

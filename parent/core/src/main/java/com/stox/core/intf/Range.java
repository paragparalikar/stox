package com.stox.core.intf;

public interface Range {

	double getHigh();

	double getValue();

	double getLow();

	public static class DoubleRange implements Range {

		private final double value;

		public DoubleRange(final double value) {
			this.value = value;
		}

		@Override
		public double getHigh() {
			return value;
		}

		@Override
		public double getValue() {
			return value;
		}

		@Override
		public double getLow() {
			return value;
		}

	}

}

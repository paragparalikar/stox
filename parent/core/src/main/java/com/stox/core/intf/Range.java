package com.stox.core.intf;

public interface Range {

	double getHigh();

	double getValue();

	double getLow();

	public static class DoubleRange implements Range {

		private final double low, value, high;

		public DoubleRange(final double value) {
			this(value, value, value);
		}
		
		public DoubleRange(final double low, final double value, final double high) {
			this.low = low;
			this.value = value;
			this.high = high;
		}

		@Override
		public double getHigh() {
			return high;
		}

		@Override
		public double getValue() {
			return value;
		}

		@Override
		public double getLow() {
			return low;
		}

	}

}

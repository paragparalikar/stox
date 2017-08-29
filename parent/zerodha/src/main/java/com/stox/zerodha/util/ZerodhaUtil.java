package com.stox.zerodha.util;

import com.stox.core.model.BarSpan;

public class ZerodhaUtil {
	
	public static String stringValue(final BarSpan barSpan) {
		switch (barSpan) {
		case M:
		case W:
		case D:
			return "day";
		case H:
			return "60minute";
		case M1:
			return "60minute";
		case M10:
			return "10minute";
		case M15:
			return "15minute";
		case M30:
			return "30minute";
		case M5:
			return "5minute";
		default:
			return null;
		}
	}

}

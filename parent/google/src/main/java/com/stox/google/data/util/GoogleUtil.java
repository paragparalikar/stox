package com.stox.google.data.util;

import com.stox.core.model.Exchange;

public class GoogleUtil {

	public static String getGoogleExchangeCode(final Exchange exchange) {
		switch (exchange) {
		case BSE:
			return "BOM";
		case NSE:
			return "NSE";
		default:
			return null;
		}
	}

	public static Exchange getExchange(final String googleExchangeCode) {
		switch (googleExchangeCode) {
		case "NSE":
			return Exchange.NSE;
		case "BOM":
			return Exchange.BSE;
		default:
			return null;
		}
	}

}

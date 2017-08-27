package com.stox.zerodha.util;

import com.stox.core.model.Exchange;
import com.stox.core.model.InstrumentType;
import com.stox.zerodha.ZerodhaConstant;
import com.stox.zerodha.model.ZerodhaExchange;

public class ZerodhaUtil {

	public static InstrumentType toInstrumentType(final String instrument_type) {
		switch (instrument_type) {
		case ZerodhaConstant.CE:
			return InstrumentType.CALL;
		case ZerodhaConstant.EQ:
			return InstrumentType.EQUITY;
		case ZerodhaConstant.FUT:
			return InstrumentType.FUTURE;
		case ZerodhaConstant.PE:
			return InstrumentType.PUT;
		}
		return null;
	}

	public static Exchange toExchange(final ZerodhaExchange exchange) {
		switch (exchange) {
		case BFO:
		case BSE:
			return Exchange.BSE;
		case NFO:
		case NSE:
			return Exchange.NSE;
		case CDS:
			return Exchange.CDS;
		case MCX:
			return Exchange.MCX;
		}
		return null;
	}

}

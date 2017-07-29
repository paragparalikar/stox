package com.stox.zerodha.util;

import com.stox.core.model.Exchange;
import com.stox.core.model.InstrumentType;
import com.stox.zerodha.ZerodhaConstant;

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

	public static Exchange toExchange(final String exchange) {
		switch (exchange) {
		case ZerodhaConstant.BFO:
		case ZerodhaConstant.BSE:
			return Exchange.BSE;
		case ZerodhaConstant.NFO:
		case ZerodhaConstant.NSE:
			return Exchange.NSE;
		case ZerodhaConstant.CDS:
			return Exchange.CDS;
		case ZerodhaConstant.MCX:
			return Exchange.MCX;
		}
		return null;
	}

}

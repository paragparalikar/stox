package com.stox.google.data.tick;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.stox.core.model.Exchange;
import com.stox.core.model.Tick;
import com.stox.google.data.util.GoogleUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class GoogleTick extends Tick{
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private String t;
	
	private String e;
	
	private Double l_fix;
	
	private String lt_dts;
	
	private int s;
	
	private Double c;
	
	private Double c_fix;
	
	private Double cp;
	
	private Double cp_fix;
	
	private String ccol;
	
	private Double pcls_fix;
	
	@Override
	public Date getLastTradeDate() {
		try {
			return DATEFORMAT.parse(lt_dts);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public int getLastTradeSize() {
		return 0;
	}
	
	public String getInstrumentCode() {
		return t;
	}
	
	public Exchange getExchange() {
		return GoogleUtil.getExchange(e);
	}
	
	public double getLastTradePrice() {
		return l_fix;
	}
	
	public double getChange() {
		return c_fix;
	}
	
	public double getPercentageChange() {
		return cp_fix;
	}
	

}

package com.stox.zerodha.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Data;

@Data
public class ZerodhaSession {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private ZerodhaProduct[] product;
	private ZerodhaOrderType[] orderType;
	private ZerodhaExchange[] exchange;

	private String publicToken;
	private String accessToken;
	private String requestToken;
	
	private String loginTime;
	
	@JsonUnwrapped
	private ZerodhaUser user;
	
	@JsonIgnore
	private final Map<String, List<String>> cookies = new HashMap<>();

	@JsonIgnore
	public Date getLoginDate() {
		try {
			return null == loginTime ? null : DATEFORMAT.parse(loginTime);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}

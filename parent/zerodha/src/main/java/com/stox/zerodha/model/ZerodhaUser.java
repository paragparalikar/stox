package com.stox.zerodha.model;

import lombok.Data;

@Data
public class ZerodhaUser {

	private String clientId;
	
	private String userName;
	
	private String email;
	
	private Boolean passwordReset;
	
	private String userType;
	
}

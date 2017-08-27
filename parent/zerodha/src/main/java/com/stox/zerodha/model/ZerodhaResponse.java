package com.stox.zerodha.model;

import lombok.Data;

@Data
public class ZerodhaResponse<T> {

	private String status;
	
	private String message;
	
	private T data;
	
}

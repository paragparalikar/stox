package com.stox.screener;

import com.stox.screen.Screen;

import lombok.Data;

@Data
public class ScreenConfiguration {
	
	private int span = 1;
	
	private int offset = 0;
	
	private Screen<?> screen;
	
	private Object configuration;

}

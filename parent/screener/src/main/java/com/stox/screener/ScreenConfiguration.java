package com.stox.screener;

import com.stox.screen.Screen;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
public class ScreenConfiguration {
	
	@Default
	private int span = 1;
	
	@Default
	private int offset = 0;
	
	private Screen<?> screen;
	
	private Object configuration;

}

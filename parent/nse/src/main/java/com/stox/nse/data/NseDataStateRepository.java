package com.stox.nse.data;

import java.io.File;

import org.springframework.stereotype.Component;

import com.stox.core.util.Constant;

@Component
public class NseDataStateRepository {

	private NseDataState dataState;

	private File getDataStateFile() {
		return new File(Constant.PATH + "com.stox.nse.data.state.json");
	}

	public NseDataState getDataState() {
		try {
			if (null == dataState) {
				dataState = Constant.objectMapper.readerFor(NseDataState.class).readValue(getDataStateFile());
			}
		} catch (Exception e) {
			dataState = new NseDataState();
		}
		return dataState;
	}

	public void persistDataState() {
		try {
			Constant.objectMapper.writerFor(NseDataState.class).writeValue(getDataStateFile(), dataState);
		} catch (Exception e) {

		}
	}

}

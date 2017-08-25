package com.stox.nse.data;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.stox.core.util.Constant;
import com.stox.core.util.FileUtil;

@Component
public class NseDataStateRepository {

	private NseDataState dataState;

	private File getDataStateFile() throws IOException {
		return FileUtil.getFile(Constant.PATH + "nse" + File.separator + "com.stox.nse.data.state.json");
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

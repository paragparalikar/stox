package com.stox.chart.drawing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.core.util.FileUtil;

@Component 
public class JsonFileDrawingStateRepository implements DrawingStateRepository {
	
	private String getPath(final Instrument instrument) {
		return Constant.PATH + "chart" + File.separator + "com.stox.chart."+instrument.getId()+".json";
	}

	@Override
	public List<Drawing.State<?>> load(Instrument instrument) {
		try {
			final File file = new File(getPath(instrument));
			if(file.exists()) {
				return Constant.objectMapper.readValue(file, new TypeReference<ArrayList<Drawing.State<?>>>() {});
			}
			return Collections.emptyList();
		}catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(Instrument instrument, List<Drawing.State<?>> states) {
		try {
			final File file = FileUtil.getFile(getPath(instrument));
			Constant.objectMapper.writeValue(file, states);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

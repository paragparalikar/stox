package com.stox.nse;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.util.Constant;

@Component
@Lazy(false)
public class InstrumentManager {

	@Autowired
	private NseBarDownloader barDownloader;

	@Autowired
	private NseInstrumentDownloader instrumentDownloader;

	@PostConstruct
	public void postContruct() {
		final DownloadState state = getDownloadState();
		if (null == state.getLastInstrumentDownloadDate()) {
			instrumentDownloader.download(instruments -> {
				return null;
			});
		}
	}

	private DownloadState getDownloadState() {
		try {
			final CsvSchema schema = Constant.csvMapper.schemaFor(DownloadState.class).withHeader();
			return Constant.csvMapper.reader(schema).forType(DownloadState.class).readValue(new File(Constant.PATH + "com.stox.nse.download.state.json"));
		} catch (FileNotFoundException e) {
			return new DownloadState();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

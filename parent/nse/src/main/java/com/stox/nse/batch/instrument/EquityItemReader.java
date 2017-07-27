package com.stox.nse.batch.instrument;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.core.io.InputStreamResource;

import com.stox.core.model.Instrument;

public class EquityItemReader extends PoiItemReader<Instrument> implements RowMapper<Instrument> {

	public EquityItemReader(final String path) {
		try {
			setLinesToSkip(1);
			setRowMapper(this);
			setResource(new InputStreamResource(new BufferedInputStream(new FileInputStream(path))));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Instrument mapRow(final RowSet rowSet) throws Exception {
		return new Instrument();
	}

}

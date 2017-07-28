package com.stox.nse.batch.instrument;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.core.io.InputStreamResource;

import com.stox.core.model.Instrument;

public class ExcelInstrumentItemReader implements ItemReader<Instrument> {

	private PoiItemReader<Instrument> delegate;
	private final String directoryPath;
	private final RowMapper<Instrument> rowMapper;
	private final ExecutionContext executionContext = new ExecutionContext();

	public ExcelInstrumentItemReader(final String directoryPath, final RowMapper<Instrument> rowMapper) {
		this.rowMapper = rowMapper;
		this.directoryPath = directoryPath;
	}

	@Override
	public Instrument read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (null == delegate) {
			delegate = new PoiItemReader<>();
			delegate.setLinesToSkip(1);
			delegate.setRowMapper(rowMapper);
			final File directory = new File(directoryPath);
			if (directory.exists() && directory.isDirectory()) {
				final String[] filePaths = directory.list((dir, name) -> {
					return name.endsWith(".xls") || name.endsWith(".xlsx");
				});
				if (null != filePaths && 0 < filePaths.length) {
					delegate.setResource(new InputStreamResource(new BufferedInputStream(new FileInputStream(directory.getAbsolutePath() + File.separator + filePaths[0]))));
					delegate.open(executionContext);
				} else {
					throw new UnexpectedInputException("No xls or xlsx file found in " + directoryPath);
				}
			} else {
				throw new UnexpectedInputException("Directory does not exists : " + directoryPath);
			}
		}
		return delegate.read();
	}

}

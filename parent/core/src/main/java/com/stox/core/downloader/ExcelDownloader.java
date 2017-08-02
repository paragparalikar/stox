package com.stox.core.downloader;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stox.core.util.Constant;

@Data
@AllArgsConstructor
public abstract class ExcelDownloader<T> implements Downloader<T, Row> {
	private static final Logger log = LoggerFactory.getLogger("com.stox.core.downloader.ExcelDownloader");

	private String url;
	private int linesToSkip;

	public ExcelDownloader(final String url) {
		this(url, 0);
	}

	@Override
	public List<T> download() throws Exception {
		log.info("Downloading from " + url + "(linesToSkip=" + linesToSkip + ")");
		final List<T> items = new ArrayList<>();
		try (Workbook workbook = new XSSFWorkbook(createInputStream())) {
			final Sheet sheet = workbook.getSheetAt(0);
			final Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				final Row row = rowIterator.next();
				if (linesToSkip > row.getRowNum()) {
					continue;
				}
				items.add(parse(row));
			}
		}
		return items;
	}

	protected InputStream createInputStream() throws Exception {
		final String fileName = UUID.randomUUID().toString();
		final Path targetPath = Paths.get(Constant.PATH + fileName);
		Files.copy(new URL(getUrl()).openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		return Files.newInputStream(targetPath, StandardOpenOption.DELETE_ON_CLOSE);
	}

}

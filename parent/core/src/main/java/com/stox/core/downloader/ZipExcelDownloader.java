package com.stox.core.downloader;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.stox.core.util.Constant;

public abstract class ZipExcelDownloader<T> extends ExcelDownloader<T> {

	public ZipExcelDownloader(String url) {
		super(url);
	}

	public ZipExcelDownloader(String url, int linesToSkip) {
		super(url, linesToSkip);
	}

	@Override
	public List<T> download() throws Exception {
		return super.download();
	}

	@Override
	protected InputStream createInputStream() throws Exception {
		final ZipInputStream zis = new ZipInputStream(new URL(getUrl()).openStream());
		final ZipEntry zipEntry = zis.getNextEntry();
		final Path targetPath = Paths.get(Constant.TEMPDIR + zipEntry.getName());
		if (!Files.exists(targetPath)) {
			Files.createDirectories(targetPath.getParent());
			Files.createFile(targetPath);
		}
		Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
		return Files.newInputStream(targetPath, StandardOpenOption.DELETE_ON_CLOSE);
	}
}

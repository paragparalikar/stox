package com.stox.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FileUtil {

	private FileUtil() {

	}

	public static boolean isValidZip(final File file) {
		try (final ZipFile zipFile = new ZipFile(file)) {
			validZipFile(zipFile);
			final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			while (zipEntries.hasMoreElements()) {
				final ZipEntry zipEntry = zipEntries.nextElement();
				zipFile.getInputStream(zipEntry);
				validateZipEntry(zipEntry);
			}
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	private static void validateZipEntry(final ZipEntry zipEntry) {
		zipEntry.getComment();
		zipEntry.getCompressedSize();
		zipEntry.getCrc();
		zipEntry.getCreationTime();
		zipEntry.getExtra();
		zipEntry.getLastAccessTime();
		zipEntry.getLastModifiedTime();
		zipEntry.getMethod();
		zipEntry.getName();
		zipEntry.getSize();
		zipEntry.getTime();
	}

	private static void validZipFile(final ZipFile zipFile) {
		zipFile.size();
		zipFile.getName();
		zipFile.getComment();
	}

	public static File getFile(final String path) throws IOException {
		final File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	public static void truncateFile(final String path) throws IOException {
		final FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE);
		fileChannel.truncate(0);
		fileChannel.close();
	}

}

package com.stox.core.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class UnzipTasklet implements Tasklet {

	private final String archivePath;
	private final String targetDirectoryPath;

	public UnzipTasklet(final String archivePath, final String targetDirectoryPath) {
		this.archivePath = archivePath;
		this.targetDirectoryPath = targetDirectoryPath;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		final byte[] buffer = new byte[1024];
		try (final ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath))) {
			ZipEntry zipEntry = null;
			final List<String> fileNames = new LinkedList<>();
			while (null != (zipEntry = zis.getNextEntry())) {
				final File file = new File(targetDirectoryPath + File.separator + zipEntry.getName());
				file.getParentFile().mkdirs();
				try (final FileOutputStream fos = new FileOutputStream(file)) {
					int len;
					while (0 < (len = zis.read(buffer))) {
						fos.write(buffer, 0, len);
					}
					fileNames.add(zipEntry.getName());
					zis.closeEntry();
				} catch (Exception e) {
					throw e;
				}
			}
			return RepeatStatus.FINISHED;
		} catch (Exception e) {
			throw e;
		}
	}
}

package com.stox.nse.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
			while (null != (zipEntry = zis.getNextEntry())) {
				try (final FileOutputStream fos = new FileOutputStream(new File(targetDirectoryPath + zipEntry.getName()))) {
					int len;
					while (0 < (len = zis.read(buffer))) {
						fos.write(buffer, 0, len);
					}
				} catch (Exception e) {
					throw e;
				}
			}
			zis.closeEntry();
			return RepeatStatus.FINISHED;
		} catch (Exception e) {
			return RepeatStatus.CONTINUABLE;
		}
	}
}

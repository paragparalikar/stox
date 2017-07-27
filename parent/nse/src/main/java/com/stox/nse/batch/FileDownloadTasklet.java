package com.stox.nse.batch;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class FileDownloadTasklet implements Tasklet {

	private final String sourcePath;
	private final String targetPath;

	public FileDownloadTasklet(final String sourcePath, final String targetPath) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Files.copy(new URL(sourcePath).openStream(), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
		return RepeatStatus.FINISHED;
	}

}

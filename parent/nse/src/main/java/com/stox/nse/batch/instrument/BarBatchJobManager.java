package com.stox.nse.batch.instrument;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.nse.NseProperties;

@Component
public class BarBatchJobManager {
	public static final String LENGTH_JOB_NAME = "com.stox.job.download.nse.bar.length";
	public static final String BREDTH_JOB_NAME = "com.stox.job.download.nse.bar.breadth";

	@Autowired
	private NseProperties properties;

	@Autowired
	private JobOperator jobOperator;

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private InstrumentRepository instrumentRepository;

	@Autowired
	private BarRepository barRepository;

	@Async
	public void lengthDownload() {

	}

	public void breadthDownload() {

	}

}

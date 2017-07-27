package com.stox.nse.batch.instrument;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.model.Instrument;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.Constant;
import com.stox.nse.batch.FileDownloadTasklet;
import com.stox.nse.batch.UnzipTasklet;

@Component
public class InstrumentBatchJobManager {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private InstrumentRepository instrumentRepository;

	@Async
	public void executeInstrumentDownloadJob() {
		try {
			final String url = "https://www.nseindia.com/content/nsccl/nsccl_ann19.zip";
			final String directory = Constant.TEMPDIR;
			final String zipFilePath = directory + "nsccl_ann19.zip";
			final String excelFilePath = directory + "nsccl_ann19.xlsx";

			final InstrumentDownloadDecider decider = new InstrumentDownloadDecider();
			final Flow flow = new FlowBuilder<Flow>("instrumentDownloadFlow").start(decider).on(FlowExecutionStatus.STOPPED.getName()).end(FlowExecutionStatus.STOPPED.getName())
					.from(decider).on(FlowExecutionStatus.COMPLETED.getName()).end(FlowExecutionStatus.COMPLETED.getName()).build();
			final Step fileDownloadStep = stepBuilderFactory.get("equityFileDownloadStep").tasklet(new FileDownloadTasklet(url, zipFilePath)).build();
			final Step fileUnzipStep = stepBuilderFactory.get("equityFileUnzipStep").tasklet(new UnzipTasklet(zipFilePath, directory)).build();
			final Step step = stepBuilderFactory.get("equityStep").<Instrument, Instrument> chunk(100000).reader(new EquityItemReader(excelFilePath))
					.writer(new InstrumentItemWriter(instrumentRepository)).build();
			final Job job = jobBuilderFactory.get("instrumentDownloadJob").start(flow).on(FlowExecutionStatus.COMPLETED.getName()).to(fileDownloadStep).next(fileUnzipStep)
					.next(step).end().build();
			jobLauncher.run(job, new JobParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

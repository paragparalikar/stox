package com.stox.nse.batch.instrument;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.batch.CsvRowMapper;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Exchange;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.nse.NseProperties;
import com.stox.nse.batch.instrument.mapper.LengthBarDownloadCsvRowMapper;

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

	private final CsvRowMapper<Bar> rowMapper = new LengthBarDownloadCsvRowMapper();

	@Async
	@SuppressWarnings("unchecked")
	public void lengthDownload() {
		final List<Step> steps = instrumentRepository.getAllInstruments().stream().filter(instrument -> {
			return Exchange.NSE.equals(instrument.getExchange()) && InstrumentType.EQUITY.equals(instrument.getType());
		}).map(instrument -> {
			final BarLengthDownloadItemReader itemReader = new BarLengthDownloadItemReader(properties.getBarDownloadUrl(), instrument, rowMapper);
			itemReader.setLinesToSkip(1);
			itemReader.setName(LENGTH_JOB_NAME + "." + instrument.getSymbol() + ".item-reader");
			final ItemWriter<Bar> itemWriter = bars -> {
				barRepository.save((List<Bar>) bars, instrument.getId(), BarSpan.D);
			};
			return stepBuilderFactory.get(LENGTH_JOB_NAME + "." + instrument.getSymbol()).<Bar, Bar> chunk(Integer.MAX_VALUE).reader(itemReader).writer(itemWriter).build();
		}).collect(Collectors.toList());
		final List<Flow> flows = steps
				.stream()
				.map(step -> {
					return new FlowBuilder<Flow>(step.getName() + "-flow").start(step).on(FlowExecutionStatus.FAILED.getName()).end().on(FlowExecutionStatus.COMPLETED.getName())
							.end().build();
				}).collect(Collectors.toList());
		final Flow parentFlow = new FlowBuilder<Flow>(LENGTH_JOB_NAME + "-flow").split(taskExecutor).add(flows.toArray(new Flow[flows.size()]))
				.on(FlowExecutionStatus.COMPLETED.getName()).end().on(FlowExecutionStatus.FAILED.getName()).fail().end();
		final Job job = jobBuilderFactory.get(LENGTH_JOB_NAME).start(parentFlow).on(FlowExecutionStatus.COMPLETED.getName()).end().on(FlowExecutionStatus.FAILED.getName()).fail()
				.end().build();
		final JobParameters jobParameters = new JobParameters();
		try {
			jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}

	@Async
	public void breadthDownload() {

	}

}

package com.stox.nse.batch.instrument;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.stox.core.batch.ArchiveExtractionTasklet;
import com.stox.core.batch.CsvItemReader;
import com.stox.core.batch.CsvRowMapper;
import com.stox.core.batch.ExcelItemReader;
import com.stox.core.batch.FileDownloadTasklet;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.Constant;
import com.stox.nse.NseProperties;
import com.stox.nse.batch.instrument.mapper.CorporateBondInstrumentRowMapper;
import com.stox.nse.batch.instrument.mapper.DebtCsvRowMapper;
import com.stox.nse.batch.instrument.mapper.DepositoryReceiptCsvRowMapper;
import com.stox.nse.batch.instrument.mapper.EquityCsvRowMapper;
import com.stox.nse.batch.instrument.mapper.EtfCsvRowMapper;
import com.stox.nse.batch.instrument.mapper.GsecInstrumentRowMapper;
import com.stox.nse.batch.instrument.mapper.MutualFundCloseEndedCsvRowMapper;
import com.stox.nse.batch.instrument.mapper.MutualFundInstrumentRowMapper;
import com.stox.nse.batch.instrument.mapper.PreferenceShareCsvRowMapper;
import com.stox.nse.batch.instrument.mapper.WarrantCsvRowMapper;

@Component
public class InstrumentBatchJobManager {
	private static final String JOB_NAME = "com.stox.job.download.nse.instrument";

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

	@SuppressWarnings("unchecked")
	private final ItemWriter<Instrument> instrumentsItemWriter = instruments -> {
		instrumentRepository.save((List<Instrument>) instruments);
	};

	private JobParameters getJobParameters() {
		return new JobParametersBuilder().addString("MONTH", new SimpleDateFormat("MMM-yyyy").format(new Date())).toJobParameters();
	}

	@Async
	public void executeInstrumentDownloadJob() {
		try {
			final List<Long> jobInstanceIds = jobOperator.getJobInstances(JOB_NAME, 0, 1);
			if (null != jobInstanceIds && !jobInstanceIds.isEmpty()) {
				final List<Long> jobExecutionIds = jobOperator.getExecutions(jobInstanceIds.get(0));
				for (final Long jobExecutionId : jobExecutionIds) {
					final JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
					if (null != jobExecution && ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
						final Date date = jobExecution.getEndTime();
						final Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.MONTH, -1);
						if (date.after(calendar.getTime())) {
							return;
						}
					}
				}
			}
		} catch (NoSuchJobException | NoSuchJobInstanceException e) {
		}
		doExecuteInstrumentDownloadJob();
	}

	private void doExecuteInstrumentDownloadJob() {
		try {
			final Flow mfFlow = archivedExcelFlow(JOB_NAME + "." + InstrumentType.MUTUAL_FUND.getName(), properties.getMutualFundsInstrumentDownloadUrl(),
					new MutualFundInstrumentRowMapper());
			final Flow cbFlow = archivedExcelFlow(JOB_NAME + "." + InstrumentType.CORPORATE_BOND.getName(), properties.getCorporateBondsInstrumentDownloadUrl(),
					new CorporateBondInstrumentRowMapper());
			final Flow gsecFlow = archivedExcelFlow(JOB_NAME + "." + InstrumentType.GOVERNMENT_SECURITY.getName(), properties.getGsecInstrumentsDownloadUrl(),
					new GsecInstrumentRowMapper());
			final Flow equityFlow = csvFlow(JOB_NAME + "." + InstrumentType.EQUITY, properties.getEquityInstrumentDownloadUrl(), new EquityCsvRowMapper());
			final Flow depositoryReceiptFlow = csvFlow(JOB_NAME + "." + InstrumentType.DEPOSITORY_RECEIPTS, properties.getDepositoryReceiptsInstrumentDownloadUrl(),
					new DepositoryReceiptCsvRowMapper());
			final Flow preferenceShareFlow = csvFlow(JOB_NAME + "." + InstrumentType.PREFERENCE_SHARES, properties.getPreferenceSharesInstrumentDownloadUrl(),
					new PreferenceShareCsvRowMapper());
			final Flow debtFlow = csvFlow(JOB_NAME + "." + InstrumentType.DEBT, properties.getDebtInstrumentDownloadUrl(), new DebtCsvRowMapper());
			final Flow warrantFlow = csvFlow(JOB_NAME + "." + InstrumentType.WARRANTS, properties.getWarrantInstrumentDownloadUrl(), new WarrantCsvRowMapper());
			final Flow mfCloseEndedFlow = csvFlow(JOB_NAME + "." + InstrumentType.MUTUAL_FUND_CE, properties.getMutualFundsCloseEndedInstrumentDownloadUrl(),
					new MutualFundCloseEndedCsvRowMapper());
			final Flow etfFlow = csvFlow(JOB_NAME + "." + InstrumentType.ETF, properties.getEtfInstrumentDownloadUrl(), new EtfCsvRowMapper());
			final Job job = jobBuilderFactory.get(JOB_NAME).start(mfFlow).split(new SyncTaskExecutor())
					.add(cbFlow, gsecFlow, equityFlow, depositoryReceiptFlow, preferenceShareFlow, debtFlow, warrantFlow, mfCloseEndedFlow, etfFlow)
					.on(FlowExecutionStatus.COMPLETED.getName()).end().on(FlowExecutionStatus.FAILED.getName()).fail().end().listener(new JobExecutionListenerSupport() {
						@Override
						public void afterJob(JobExecution jobExecution) {
							super.afterJob(jobExecution);
							if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
								instrumentRepository.flush();
							}
						}
					}).build();

			jobLauncher.run(job, getJobParameters());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Flow csvFlow(final String flowName, final String url, final CsvRowMapper<Instrument> rowMapper) {
		final CsvItemReader<Instrument> reader = new CsvItemReader<>(url, rowMapper);
		reader.setLinesToSkip(1);
		reader.setName(flowName + "-item-reader");
		final Step step = stepBuilderFactory.get(flowName + "-read-write-step").<Instrument, Instrument> chunk(Integer.MAX_VALUE).reader(reader).writer(instrumentsItemWriter)
				.build();
		return new FlowBuilder<Flow>(flowName).start(step).on(FlowExecutionStatus.FAILED.getName()).fail().from(step).on(FlowExecutionStatus.COMPLETED.getName()).end().build();
	}

	private Flow archivedExcelFlow(final String flowName, final String url, final RowMapper<Instrument> rowMapper) {
		final String directoryPath = Constant.TEMPDIR + flowName;
		final String archivePath = Constant.TEMPDIR + flowName + ".zip";
		final Step fileDownloadStep = stepBuilderFactory.get(flowName + "-file-download-step").tasklet(new FileDownloadTasklet(url, archivePath)).build();
		final Step fileUnzipStep = stepBuilderFactory.get(flowName + "-file-unzip-step").tasklet(new ArchiveExtractionTasklet(archivePath, directoryPath)).build();
		final ExcelItemReader<Instrument> itemReader = new ExcelItemReader<>(directoryPath, rowMapper);
		final Step readWriteStep = stepBuilderFactory.get(flowName + "-read-write-step").<Instrument, Instrument> chunk(Integer.MAX_VALUE).reader(itemReader)
				.writer(instrumentsItemWriter).build();
		return new FlowBuilder<Flow>(flowName).start(fileDownloadStep).on(FlowExecutionStatus.FAILED.getName()).fail().next(fileUnzipStep).on(FlowExecutionStatus.FAILED.getName())
				.fail().next(readWriteStep).on(FlowExecutionStatus.FAILED.getName()).fail().end();
	}
}

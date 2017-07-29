package com.stox.nse;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.event.InstrumentsChangedEvent;
import com.stox.nse.batch.instrument.BarBatchJobManager;
import com.stox.nse.batch.instrument.InstrumentBatchJobManager;

@Component
public class NseDownloadManager {

	@Autowired
	private JobOperator jobOperator;

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private InstrumentBatchJobManager instrumentBatchJobManager;

	@Autowired
	private BarBatchJobManager barBatchJobManager;

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		downloadInstruments();
		if (areInstrumentsAvailable()) {
			if (isLengthBarDownloadCompleted()) {
				barBatchJobManager.breadthDownload();
			} else {
				barBatchJobManager.lengthDownload();
			}
		}
	}

	@EventListener(InstrumentsChangedEvent.class)
	public void onInstrumentsChanged(final InstrumentsChangedEvent event) {
		if (!isLengthBarDownloadCompleted()) {
			barBatchJobManager.lengthDownload();
		}
	}

	private boolean areInstrumentsAvailable() {
		try {
			final List<Long> jobInstanceIds = jobOperator.getJobInstances(InstrumentBatchJobManager.JOB_NAME, 0, Integer.MAX_VALUE);
			for (final Long jobInstanceId : jobInstanceIds) {
				final List<Long> jobExecutionIds = jobOperator.getExecutions(jobInstanceId);
				for (final Long jobExecutionId : jobExecutionIds) {
					final JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
					if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
						return true;
					}
				}
			}
		} catch (NoSuchJobException | NoSuchJobInstanceException e) {
		}
		return false;
	}

	private boolean isLengthBarDownloadCompleted() {
		try {
			final List<Long> jobInstanceIds = jobOperator.getJobInstances(BarBatchJobManager.LENGTH_JOB_NAME, 0, 1);
			final List<Long> jobExecutionIds = jobOperator.getExecutions(jobInstanceIds.get(0));
			final Long jobExecutionId = jobExecutionIds.get(0);
			final JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
			return ExitStatus.COMPLETED.equals(jobExecution.getExitStatus());
		} catch (NoSuchJobException | NoSuchJobInstanceException e) {
			return false;
		}
	}

	private void downloadInstruments() {
		try {
			final List<Long> jobInstanceIds = jobOperator.getJobInstances(InstrumentBatchJobManager.JOB_NAME, 0, 1);
			final List<Long> jobExecutionIds = jobOperator.getExecutions(jobInstanceIds.get(0));
			final Long jobExecutionId = jobExecutionIds.get(0);
			final JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
			if (ExitStatus.FAILED.equals(jobExecution.getExitStatus())) {
				jobOperator.restart(jobExecutionId);
				return;
			} else if (ExitStatus.COMPLETED.equals(jobExecution.getExitStatus())) {
				final Date jobEnDate = jobExecution.getEndTime();
				final Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, -1);
				if (jobEnDate.before(calendar.getTime())) {
					instrumentBatchJobManager.executeInstrumentDownloadJob();
				}
			}
		} catch (NoSuchJobException | NoSuchJobInstanceException | JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | JobRestartException
				| JobParametersInvalidException e) {
			instrumentBatchJobManager.executeInstrumentDownloadJob();
		}
	}

}

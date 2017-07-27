package com.stox.nse.batch.instrument;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.util.Constant;
import com.stox.nse.DownloadState;

public class InstrumentDownloadDecider implements JobExecutionDecider {

	private DownloadState getDownloadState() {
		try {
			final CsvSchema schema = Constant.csvMapper.schemaFor(DownloadState.class).withHeader();
			return Constant.csvMapper.reader(schema).forType(DownloadState.class).readValue(new File(Constant.PATH + "com.stox.nse.download.state.json"));
		} catch (FileNotFoundException e) {
			return new DownloadState();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, org.springframework.batch.core.StepExecution stepExecution) {
		final DownloadState state = getDownloadState();
		if (null == state.getLastInstrumentDownloadDate()) {
			return FlowExecutionStatus.COMPLETED;
		} else {
			return FlowExecutionStatus.STOPPED;
		}
	}

}

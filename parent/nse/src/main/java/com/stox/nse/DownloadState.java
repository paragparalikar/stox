package com.stox.nse;

import java.util.Date;

import lombok.Data;

@Data
public class DownloadState {

	private boolean equitySpanDownloadCompleted;

	private Date lastInstrumentDownloadDate;

	private Date lastDailyBarDownloadDate;

}

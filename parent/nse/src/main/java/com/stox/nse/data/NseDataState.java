package com.stox.nse.data;

import java.util.Date;

import lombok.Data;

@Data
public class NseDataState {

	private Boolean barLengthDownloadCompleted;

	private Date lastInstrumentDownloadDate;

	private Date lastBarDownloadDate;

}

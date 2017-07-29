package com.stox.nse;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:nse.properties")
public class NseProperties {

	@Value("${com.stox.nse.url.instrument.EQ}")
	private String equityInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.MF}")
	private String mutualFundsInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.MFCE}")
	private String mutualFundsCloseEndedInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.CB}")
	private String corporateBondsInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.GSEC}")
	private String gsecInstrumentsDownloadUrl;

	@Value("${com.stox.nse.url.instrument.DR}")
	private String depositoryReceiptsInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.PS}")
	private String preferenceSharesInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.DE}")
	private String debtInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.WR}")
	private String warrantInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.ETF}")
	private String etfInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.ID}")
	private String indexInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.bar}")
	private String barDownloadUrl;

}

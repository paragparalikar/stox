package com.stox.nse;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:nse.properties")
public class NseProperties {

	@Value("${com.stox.nse.url.instrument.mf}")
	private String mutualFundsInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.cb}")
	private String corporateBondsInstrumentDownloadUrl;

	@Value("${com.stox.nse.url.instrument.gsec}")
	private String gsecInstrumentsDownloadUrl;

}

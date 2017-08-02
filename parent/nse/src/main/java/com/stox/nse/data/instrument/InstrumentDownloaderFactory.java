package com.stox.nse.data.instrument;

import org.springframework.core.env.Environment;

import com.stox.core.downloader.Downloader;
import com.stox.core.model.Instrument;
import com.stox.core.model.InstrumentType;

public class InstrumentDownloaderFactory {

	private final Environment environment;
	private final String prefix = "com.stox.nse.url.instrument.";

	public InstrumentDownloaderFactory(final Environment environment) {
		this.environment = environment;
	}

	private String getDownloadUrl(final InstrumentType type) {
		return environment.getProperty(prefix + type.getId());
	}

	public Downloader<Instrument, ?> getDownloader(final InstrumentType type) {
		Downloader<Instrument, ?> downloader = null;
		switch (type) {
		case CALL:
			break;
		case CORPORATE_BOND:
			downloader = new CorporateBondDownloader(getDownloadUrl(InstrumentType.CORPORATE_BOND));
			break;
		case DEBT:
			downloader = new DebtDownloader(getDownloadUrl(InstrumentType.DEBT));
			break;
		case DEPOSITORY_RECEIPTS:
			downloader = new DepositoryReceiptDownloader(getDownloadUrl(InstrumentType.DEPOSITORY_RECEIPTS));
			break;
		case EQUITY:
			downloader = new EquityDownloader(getDownloadUrl(InstrumentType.EQUITY));
			break;
		case ETF:
			downloader = new EtfDownloader(getDownloadUrl(InstrumentType.ETF));
			break;
		case FUTURE:
			break;
		case GOVERNMENT_SECURITY:
			downloader = new GsecDownloader(getDownloadUrl(InstrumentType.GOVERNMENT_SECURITY));
			break;
		case INDEX:
			downloader = new IndexDownloader();
			break;
		case MUTUAL_FUND:
			downloader = new MutualFundDownloader(getDownloadUrl(InstrumentType.MUTUAL_FUND));
			break;
		case MUTUAL_FUND_CE:
			downloader = new MutualFundCloseEndedDownloader(getDownloadUrl(InstrumentType.MUTUAL_FUND_CE));
			break;
		case PREFERENCE_SHARES:
			downloader = new PreferenceShareDownloader(getDownloadUrl(InstrumentType.PREFERENCE_SHARES));
			break;
		case PUT:
			break;
		case WARRANTS:
			downloader = new WarrantDownloader(getDownloadUrl(InstrumentType.WARRANTS));
			break;
		default:
			break;
		}
		return downloader;
	}
}
